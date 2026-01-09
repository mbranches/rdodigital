package com.branches.config.security;

import com.branches.auth.model.UserDetailsImpl;
import com.branches.auth.service.JwtService;
import com.branches.user.domain.UserEntity;
import com.branches.user.service.GetUserByIdExternoService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class TokenAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final GetUserByIdExternoService getUserByIdExternoService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        String token = recoveryToken(authorizationHeader);

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                String userExternalId = jwtService.validateToken(token);

                UserEntity user = getUserByIdExternoService.execute(userExternalId);

                UserDetailsImpl userDetails = new UserDetailsImpl(user);

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"error\": 401, \"message\": \"Token JWT inválido\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recoveryToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}
