package com.branches.user.dto.request;

public record UpdateUserFotoDePerfilRequest(
        String base64Image,
        String fileName
) {
}
