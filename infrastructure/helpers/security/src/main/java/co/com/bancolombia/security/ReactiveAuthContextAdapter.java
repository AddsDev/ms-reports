package co.com.bancolombia.security;

import co.com.bancolombia.model.common.gateways.AuthContextPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReactiveAuthContextAdapter implements AuthContextPort {

    @Override
    public Mono<AuthUser> currentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(this::toAuthUser);
    }

    private Mono<AuthUser> toAuthUser(Authentication auth) {
        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            return Mono.empty();
        }
        String sub = jwt.getSubject();
        String email = jwt.getClaimAsString("email");

        List<String> roles = new ArrayList<>();
        Optional.ofNullable(jwt.getClaimAsStringList("roles")).ifPresent(roles::addAll);

        var realm = jwt.getClaimAsMap("realm_access");
        if (realm != null && realm.get("roles") instanceof List<?> l) {
            l.forEach(r -> roles.add(String.valueOf(r)));
        }

        List<String> perms = Optional.ofNullable(jwt.getClaimAsStringList("permissions")).orElse(List.of());

        return Mono.just(new AuthContextPort.AuthUser(sub, email, roles, perms));
    }
}
