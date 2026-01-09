package com.branches.auth.service;

import com.branches.auth.dto.ClientInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import ua_parser.Client;
import ua_parser.Parser;

@Service
public class ClientInfoService {
    public ClientInfo execute(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");

        String addressIp = xForwardedFor != null && !xForwardedFor.isEmpty()
                ? xForwardedFor.split(",")[0]
                : request.getRemoteAddr();

        Parser parser = new Parser();
        String userAgent = request.getHeader("User-Agent");
        Client client = parser.parse(userAgent);

        return new ClientInfo(
                addressIp,
                client.userAgent.toString(),
                client.device.family,
                client.userAgent.family,
                client.os.family
        );
    }
}
