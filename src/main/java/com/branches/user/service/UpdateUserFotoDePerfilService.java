package com.branches.user.service;

import com.branches.external.aws.S3UploadFile;
import com.branches.user.domain.UserEntity;
import com.branches.user.dto.request.UpdateUserFotoDePerfilRequest;
import com.branches.user.repository.UserRepository;
import com.branches.utils.CompressImage;
import com.branches.utils.FileContentType;
import com.branches.utils.ImageOutPutFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UpdateUserFotoDePerfilService {
    private final CompressImage compressImage;
    private final S3UploadFile s3UploadFile;
    private final UserRepository userRepository;

    public void execute(UserEntity user, UpdateUserFotoDePerfilRequest request) {
        String base64Image = request.base64Image();
        String fileName = "foto-perfil-%s.jpeg".formatted(user.getIdExterno());

        byte[] compressedImage = compressImage.execute(base64Image, 500, 500, 0.7, ImageOutPutFormat.JPEG);

        String urlFotoPerfil = s3UploadFile.execute(fileName, "users/%s/foto-de-perfil".formatted(user.getIdExterno()), compressedImage, FileContentType.JPEG);

        user.setFotoUrl(urlFotoPerfil);

        userRepository.save(user);
    }
}
