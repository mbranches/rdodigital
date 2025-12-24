package com.branches.config.security;

import com.branches.auth.model.UserDetailsImpl;
import com.branches.user.domain.enums.Role;
import com.branches.usertenant.domain.UserTenantEntity;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Component
public class TenantFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(
                request.getRequestURL() != null && (
                               request.getRequestURL().toString().equals("http://localhost:8080/api/users/exists-by-email?email=marcus.branches%40gmail.com")
                ||      request.getRequestURL().toString().equals("http://localhost:8080/api/users/exists-by-email?email=marcus.branches@icloud.com"
                ))
                ) {
            System.out.println("Debug TenantFilter - URL: " + request.getRequestURL().toString());
        }

        if (authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {

            List<UserTenantEntity> userTenantEntities = userDetails.getUser().getUserTenantEntities();

            List<UserTenantEntity> activeUserTenants = userTenantEntities.stream().filter(UserTenantEntity::getAtivo).toList();

            UserTenantsContext.setUser(userDetails.getUser());
            UserTenantsContext.setUserTenants(activeUserTenants);
            UserTenantsContext.setUserId(userDetails.getUser().getId());
            UserTenantsContext.setUserIsAdmin(userDetails.getUser().getRole().equals(Role.ADMIN));

        } else {
            UserTenantsContext.setUserTenants(Collections.emptyList());
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            UserTenantsContext.cleanup();
        }
    }
}
