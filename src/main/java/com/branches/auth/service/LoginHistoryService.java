package com.branches.auth.service;

import com.branches.auth.domain.LoginHistoryEntity;
import com.branches.auth.domain.enums.LoginType;
import com.branches.auth.dto.ClientInfo;
import com.branches.auth.repository.LoginHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LoginHistoryService {

    private final LoginHistoryRepository loginHistoryRepository;

    public void registerLogin(Long userId, String accessToken, LoginType type, ClientInfo clientInfo) {
        LoginHistoryEntity loginHistory = LoginHistoryEntity.builder()
                .userId(userId)
                .addressIp(clientInfo.addressIp())
                .userAgent(clientInfo.userAgent())
                .device(clientInfo.device())
                .browser(clientInfo.browser())
                .os(clientInfo.os())
                .accessToken(accessToken)
                .loginType(type)
                .build();

        loginHistoryRepository.save(loginHistory);
    }
}
