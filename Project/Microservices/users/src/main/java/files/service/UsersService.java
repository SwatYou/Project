package files.service;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;

import files.shared.UserDto;
import reactor.core.publisher.Mono;

public interface UsersService extends ReactiveUserDetailsService{

    UserDto createUser(UserDto user);

    Mono<UserDto> getUserDetailsByEmail(String email); 
}
