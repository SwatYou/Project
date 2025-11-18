package files.custom;

import java.sql.SQLException;
import javax.sql.DataSource;
import java.sql.Connection;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import files.service.UsersService;
import files.shared.UserDto;
import files.user.CreateUser;
import files.user.CreateUserResponse;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/users")
public class UsersController {

    private DataSource dataSource;

    private final UsersService usersService;
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public UsersController(@Lazy UsersService usersService, UsersRepository usersRepository,
            PasswordEncoder passwordEncoder) {
        this.usersService = usersService;
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("")
    public String main() {
        return "Users Microservice ...!!!";
    }

    @GetMapping("/me")
    public Mono<String> getAuthenticatedUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName());
    }

    @PostMapping("")
    public ResponseEntity<CreateUserResponse> createUser(@Valid @RequestBody CreateUser request) {

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserDto userDto = modelMapper.map(request, UserDto.class);

        userDto.setEncryptedPassword(passwordEncoder.encode(request.getPassword()));

        UserDto createdUser = usersService.createUser(userDto);

        System.out.println("\n==== STORED USERS IN DB ====");
        usersRepository.findAll().forEach(u -> System.out.println("Email: " + u.getEmail() +
                " | Encrypted Password: " + u.getEncryptedPassword()));
        System.out.println("============================\n");

        CreateUserResponse response = modelMapper.map(createdUser, CreateUserResponse.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/check-db")
    public String checkDb() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            return "Connected to: " + conn.getMetaData().getURL();
        }
    }
}