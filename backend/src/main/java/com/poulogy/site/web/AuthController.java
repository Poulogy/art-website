package com.poulogy.site.web;

import com.poulogy.site.configuration.PassLimiter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final long windowTime = 15 * 60_000L;
    private static final int max_fails = 5;

    private final PasswordEncoder encoder;
    private final PassLimiter limiter;
    private final String password;
    private final HttpSessionSecurityContextRepository contextRepo = new HttpSessionSecurityContextRepository();

    public AuthController(PasswordEncoder encoder, PassLimiter limiter,
                          @Value("${app.author-password}") String password) {
        this.encoder = encoder;
        this.limiter = limiter;
        this.password = encoder.encode(password);
    }

    @PostMapping("/login")
    public Map<String, Boolean> login(@Valid @RequestBody DTO.Login req,
                                      HttpServletRequest request, HttpServletResponse response) {
        String key = "login:" + request.getRemoteAddr();
        if (limiter.count(key, windowTime) >= max_fails)
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Слишком много попыток. Подождите 15 минут.");
        if (!encoder.matches(req.password(), password)) {
            limiter.hit(key);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Неверный пароль");
        }
        if (request.getSession(false) != null) request.changeSessionId();
        var auth = new UsernamePasswordAuthenticationToken("Poulogy", null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContext ctx = SecurityContextHolder.createEmptyContext();
        ctx.setAuthentication(auth);
        SecurityContextHolder.setContext(ctx);
        contextRepo.saveContext(ctx, request, response);
        return Map.of("admin", true);
    }
    @GetMapping("/me")
    public Map<String, Boolean> me(Authentication auth) {
        return Map.of("admin", isAdmin(auth));
    }

    public static boolean isAdmin(Authentication auth) {
        return auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}


