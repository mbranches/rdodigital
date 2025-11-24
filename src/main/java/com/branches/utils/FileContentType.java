package com.branches.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileContentType {
    JPEG("image/jpeg", "jpeg"),
    PNG("image/png", "png"),
    PDF("application/pdf", "pdf");

    private final String mimeType;
    private final String extension;
}
