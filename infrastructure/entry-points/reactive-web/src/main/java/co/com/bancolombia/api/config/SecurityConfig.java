package co.com.bancolombia.api.config;

import co.com.bancolombia.model.common.gateways.TraceLoggerPort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Stream;

@Configuration
@EnableReactiveMethodSecurity
public class SecurityConfig {
    private final TraceLoggerPort logger;

    public SecurityConfig(@Qualifier("entryPointLogger") TraceLoggerPort logger) {
        this.logger = logger;
    }
    @Bean
    public SecurityWebFilterChain springSecurityWebFilterChain(ServerHttpSecurity http,
                                                               Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthConverter,
                                                               ServerAuthenticationEntryPoint entryPoint,
                                                               ServerAccessDeniedHandler accessDeniedHandler) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(HttpMethod.GET, "/actuator/**").permitAll()
                        .pathMatchers( "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter))
                        .authenticationEntryPoint(entryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .build();
    }

    @Bean
    public Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtMonoConverter() {
        return (Jwt jwt) -> {
            logger.info("jwt token: {}", jwt.getTokenValue());
            Set<String> roles = new LinkedHashSet<>();

            //Scope basic
            Object scope = Optional.ofNullable(jwt.getClaim("scope")).orElse(jwt.getClaim("scp"));
            if (scope instanceof String s) roles.addAll(Arrays.asList(s.split("\\s+")));
            if (scope instanceof Collection<?> c) c.forEach(v -> roles.add(String.valueOf(v)));

            //Custom roles
            Object customRoles = jwt.getClaim("roles");
            if (customRoles instanceof Collection<?> cr) cr.forEach(r -> roles.add(String.valueOf(r)));

            //ROLE_ prefix
            var authorities = roles.stream()
                    .flatMap(r -> Stream.of("ROLE_" + r.toUpperCase(), r))
                    .distinct()
                    .map(SimpleGrantedAuthority::new).toList();

            return Mono.just(new JwtAuthenticationToken(jwt, authorities));

        };
    }

    @Bean
    ServerAuthenticationEntryPoint entryPoint() {
        logger.trace("entryPoint UNAUTHORIZED");
        return (exchange, ex) -> {
            var resp = exchange.getResponse();
            resp.setStatusCode(HttpStatus.UNAUTHORIZED);
            return resp.setComplete();
        };
    }
    @Bean
    ServerAccessDeniedHandler accessDeniedHandler() {
        logger.trace("accessDeniedHandler FORBIDDEN");
        return (exchange, ex) -> {
            var resp = exchange.getResponse();
            resp.setStatusCode(HttpStatus.FORBIDDEN);
            return resp.setComplete();
        };
    }
}
