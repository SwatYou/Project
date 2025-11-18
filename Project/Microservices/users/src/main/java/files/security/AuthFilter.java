package files.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.core.io.buffer.DataBufferUtils;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

@Component
public class AuthFilter extends AuthenticationWebFilter {

    private final ObjectMapper mapper = new ObjectMapper();

    public AuthFilter(ReactiveAuthenticationManager authenticationManager) {
        super(authenticationManager);

        setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers("/login"));
        setServerAuthenticationConverter(loginRequestConverter());
        setAuthenticationSuccessHandler(this::onAuthSuccess);
    }

    private ServerAuthenticationConverter loginRequestConverter() {
        return exchange -> extractBody(exchange)
                .map(body -> new UsernamePasswordAuthenticationToken(
                        body.email, body.password, new ArrayList<>()));
    }

    private Mono<LoginRequest> extractBody(ServerWebExchange exchange) {
        return DataBufferUtils.join(exchange.getRequest().getBody())
                .map(buffer -> {
                    byte[] bytes = new byte[buffer.readableByteCount()];
                    buffer.read(bytes);
                    DataBufferUtils.release(buffer);
                    try {
                        return mapper.readValue(bytes, LoginRequest.class);
                    } catch (Exception e) {
                        throw new RuntimeException("Invalid JSON", e);
                    }
                });
    }

    private Mono<Void> onAuthSuccess(WebFilterExchange exchange, Authentication auth) {
        UserDetails user = (UserDetails) auth.getPrincipal();

        try {
            String secret = "32-char-long-super-secret-key!!!";
            SecretKey key = Keys.hmacShaKeyFor(Base64.getEncoder().encode(secret.getBytes(StandardCharsets.UTF_8)));
            Instant now = Instant.now();
            String token = Jwts.builder()
                    .setSubject(user.getUsername())
                    .setIssuedAt(Date.from(now))
                    .setExpiration(Date.from(now.plusMillis(900_000)))
                    .signWith(key)
                    .compact();

            ServerHttpResponse response = exchange.getExchange().getResponse();
            response.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + token);

            return response.setComplete();
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
    private static class LoginRequest {
        public String email;
        public String password;
    }
}