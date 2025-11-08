package files.custom;

import java.sql.SQLException;
import javax.sql.DataSource;
import java.sql.Connection;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import files.service.UsersService;
import files.shared.UserDto;
import files.user.CreateUser;
import files.user.CreateUserResponse;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class UsersController {

    UsersService usersService;
    private DataSource dataSource;

    @GetMapping("")
    public String main() {
        return "Users Microservice";
    }

    @PostMapping("")
    public ResponseEntity<CreateUserResponse> createUser(@Valid @RequestBody CreateUser user) {

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = modelMapper.map(user, UserDto.class);
        UserDto createdUser = usersService.createUser(userDto);

        CreateUserResponse returnValue = modelMapper.map(createdUser, CreateUserResponse.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
    }

    @GetMapping("/check-db")
    public String checkDb() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            return "Connected to: " + conn.getMetaData().getURL();
        }
    }
}