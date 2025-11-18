package files.security;

import files.service.UserServiceImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserDetailsServiceImpl implements ReactiveUserDetailsService {

    private final UserServiceImpl usersService;

    public UserDetailsServiceImpl(UserServiceImpl usersService) {
        this.usersService = usersService;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return usersService.getUserDetailsByEmail(username)
                .map(userDto -> org.springframework.security.core.userdetails.User.builder()
                        .username(userDto.getEmail())
                        .password(userDto.getEncryptedPassword())
                        .roles("USER")
                        .build());
    }
}