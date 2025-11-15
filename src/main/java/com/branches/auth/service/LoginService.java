package com.branches.auth.service;

import com.branches.auth.dto.request.LoginRequest;
import com.branches.auth.dto.response.LoginResponse;
import com.branches.auth.model.UserDetailsImpl;
import com.branches.tenant.domain.TenantEntity;
import com.branches.tenant.repository.TenantRepository;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.dto.response.TenantByUserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class LoginService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TenantRepository tenantRepository;

    public LoginResponse execute(LoginRequest request) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(request.email(), request.password());

        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String userToken = jwtService.generateToken(userDetails);

        List<UserTenantEntity> userTenants = userDetails.getUser().getUserTenantEntities();
        List<Long> tenantsIds = userTenants.stream()
                .filter(UserTenantEntity::getAtivo)
                .map(UserTenantEntity::getTenantId)
                .toList();

        List<TenantEntity> tenants = tenantRepository.findAllByIdInAndAtivoIsTrue(tenantsIds);

        return new LoginResponse(userToken, tenants.stream().map(TenantByUserInfoResponse::from).toList());
    }
}
