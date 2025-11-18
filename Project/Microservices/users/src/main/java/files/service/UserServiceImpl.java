package files.service;

import java.util.UUID;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import files.custom.UsersRepository;
import files.shared.UserDto;
import files.user.UserEntity;
import reactor.core.publisher.Mono;

@Primary
@Service
public class UserServiceImpl implements UsersService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UsersRepository usersRepository,
                           PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto createUser(UserDto user) {
        user.setUserId(UUID.randomUUID().toString());
        user.setEncryptedPassword(passwordEncoder.encode(user.getPassword()));

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserEntity userEntity = modelMapper.map(user, UserEntity.class);
        usersRepository.save(userEntity);

        return modelMapper.map(userEntity, UserDto.class);
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return getUserDetailsByEmail(username)
                .map(userDto -> org.springframework.security.core.userdetails.User.builder()
                        .username(userDto.getEmail())
                        .password(userDto.getEncryptedPassword())
                        .roles("USER")
                        .build());
    }

    public Mono<UserDto> getUserDetailsByEmail(String email) {
        return Mono.fromCallable(() -> usersRepository.findByEmail(email))
                .switchIfEmpty(Mono.error(new UsernameNotFoundException(email)))
                .map(userEntity -> {
                    ModelMapper modelMapper = new ModelMapper();
                    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
                    return modelMapper.map(userEntity, UserDto.class);
                });
    }
}
