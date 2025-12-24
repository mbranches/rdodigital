package com.branches.user.service;

import com.branches.tenant.service.GetTenantIdByIdExternoService;
import com.branches.user.domain.UserEntity;
import com.branches.user.dto.request.UpdateUserInfosRequest;
import com.branches.user.repository.UserRepository;
import com.branches.usertenant.domain.UserTenantEntity;
import com.branches.usertenant.service.GetCurrentUserTenantService;
import com.branches.utils.FullNameFormatter;
import com.branches.utils.ValidateFullName;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UpdateUserInfosService {
    private final FullNameFormatter fullNameFormatter;
    private final ValidateFullName validateFullName;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final GetTenantIdByIdExternoService getTenantIdByIdExternoService;
    private final GetCurrentUserTenantService getCurrentUserTenantService;

    @Transactional
    public void execute(String tenantExternalId, List<UserTenantEntity> userTenants, UpdateUserInfosRequest request) {
        Long tenantId = getTenantIdByIdExternoService.execute(tenantExternalId);

        UserTenantEntity currentUserTenant = getCurrentUserTenantService.execute(userTenants, tenantId);
        UserEntity userEntity = currentUserTenant.getUser();

        validateFullName.execute(request.nome());
        String formattedName = fullNameFormatter.execute(request.nome());
        userEntity.setNome(formattedName);

        if (request.password() != null && !request.password().isBlank()) {
            userEntity.setPassword(passwordEncoder.encode(request.password()));
        }

        userRepository.save(userEntity);
    }
}
