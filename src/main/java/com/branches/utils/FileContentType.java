package com.branches.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FileContentType {
    HEIC("image/heic", "heic"),
    JPEG("image/jpeg", "jpeg"),
    PNG("image/png", "png"),
    PDF("application/pdf", "pdf"),
    MP4("video/mp4", "mp4"),
    AVI("video/x-msvideo", "avi"),
    MOV("video/quicktime", "mov");

    private final String mimeType;
    private final String extension;
}
