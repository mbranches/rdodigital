package com.branches.utils;

import com.branches.exception.InternalServerError;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Slf4j
@Component
public class CompressImage {
    public byte[] execute(String imageBase64, int maxWidth, int maxHeight, double percentageQuality, ImageOutPutFormat outputFormat) {
        try {
            log.info("Iniciando compressão de imagem");
            log.info("maxWidth: {}, maxHeight: {}, percentageQuality: {}, outputFormat: {}", maxWidth, maxHeight, percentageQuality, outputFormat);
            String formattedImage = imageBase64.substring(imageBase64.indexOf(",") + 1);

            byte[] imageBytes = Base64.getDecoder().decode(formattedImage);

            BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageBytes));

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Thumbnails.of(originalImage)
                    .size(maxWidth, maxHeight)
                    .outputFormat(outputFormat.getFormat())
                    .outputQuality(percentageQuality)
                    .toOutputStream(outputStream);

            log.info("Compressão concluída com sucesso!");

            return outputStream.toByteArray();
        } catch (Exception e) {
            log.info("Ocorreu um erro ao comprimir a imagem: {}", e.getMessage());
            throw new InternalServerError("Ocorreu um erro ao comprimir a imagem");
        }
    }
}
