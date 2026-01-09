package com.branches.auth.service;

import com.branches.auth.dto.ClientInfo;
import com.branches.auth.domain.enums.LoginType;
import com.branches.auth.dto.request.LoginRequest;
import com.branches.auth.dto.response.LoginResponse;
import com.branches.auth.model.UserDetailsImpl;
import com.branches.tenant.domain.TenantEntity;
import com.branches.tenant.repository.TenantRepository;
import com.branches.user.domain.UserEntity;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.dto.response.TenantByUserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class LoginService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TenantRepository tenantRepository;
    private final RefreshTokenService refreshTokenService;
    private final LoginHistoryService loginHistoryService;

    public LoginResponse execute(LoginRequest request, ClientInfo clientInfo) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(request.email(), request.password());

        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UserEntity user = userDetails.getUser();
        Long userId = user.getId();

        String accessToken = jwtService.generateToken(user);
        String refreshToken = refreshTokenService.generate(userId);

        List<UserTenantEntity> userTenants = user.getUserTenantEntities();
        List<Long> tenantsIds = userTenants.stream()
                .filter(UserTenantEntity::getAtivo)
                .map(UserTenantEntity::getTenantId)
                .toList();

        List<TenantEntity> tenants = tenantRepository.findAllByIdInAndAtivoIsTrue(tenantsIds);

        loginHistoryService.registerLogin(userId, accessToken, LoginType.LOGIN, clientInfo);

        return new LoginResponse(accessToken, refreshToken, tenants.stream().map(TenantByUserInfoResponse::from).toList());
    }
}
