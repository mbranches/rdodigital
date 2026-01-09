package com.branches.utils;

import com.branches.exception.InternalServerError;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
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

            if (originalImage == null) {
                throw new InternalServerError("Não foi possível ler a imagem");
            }

            // Corrigir orientação da imagem baseado nos metadados EXIF
            BufferedImage correctedImage = correctImageOrientation(imageBytes, originalImage);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Thumbnails.of(correctedImage)
                    .size(maxWidth, maxHeight)
                    .keepAspectRatio(true)
                    .outputFormat(outputFormat.getFormat())
                    .outputQuality(percentageQuality)
                    .toOutputStream(outputStream);

            log.info("Compressão concluída com sucesso!");

            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Ocorreu um erro ao comprimir a imagem: {}", e.getMessage());
            throw new InternalServerError("Ocorreu um erro ao comprimir a imagem");
        }
    }

    private BufferedImage correctImageOrientation(byte[] imageBytes, BufferedImage originalImage) {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(imageBytes));
            ExifIFD0Directory exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

            if (exifIFD0Directory == null) {
                return originalImage;
            }

            int orientation = exifIFD0Directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
            log.info("Orientação EXIF detectada: {}", orientation);

            return rotateImageBasedOnOrientation(originalImage, orientation);
        } catch (Exception e) {
            log.warn("Não foi possível ler metadados EXIF da imagem, usando imagem original: {}", e.getMessage());
            log.warn("Retornando imagem original sem correção de orientação.");
            return originalImage;
        }
    }

    private BufferedImage rotateImageBasedOnOrientation(BufferedImage image, int orientation) {
        AffineTransform transform = new AffineTransform();

        switch (orientation) {
            case 1:
                // Normal, sem rotação necessária
                return image;
            case 2:
                // Flip horizontal
                transform.scale(-1.0, 1.0);
                transform.translate(-image.getWidth(), 0);
                break;
            case 3:
                // Rotação 180°
                transform.translate(image.getWidth(), image.getHeight());
                transform.rotate(Math.PI);
                break;
            case 4:
                // Flip vertical
                transform.scale(1.0, -1.0);
                transform.translate(0, -image.getHeight());
                break;
            case 5:
                // Flip horizontal + rotação 270° CW
                transform.rotate(-Math.PI / 2);
                transform.scale(-1.0, 1.0);
                break;
            case 6:
                // Rotação 90° CW
                transform.translate(image.getHeight(), 0);
                transform.rotate(Math.PI / 2);
                break;
            case 7:
                // Flip horizontal + rotação 90° CW
                transform.scale(-1.0, 1.0);
                transform.translate(-image.getHeight(), 0);
                transform.translate(0, image.getWidth());
                transform.rotate(3 * Math.PI / 2);
                break;
            case 8:
                // Rotação 270° CW
                transform.translate(0, image.getWidth());
                transform.rotate(3 * Math.PI / 2);
                break;
            default:
                return image;
        }

        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);

        // Para rotações de 90° ou 270°, precisamos trocar largura e altura
        int newWidth = image.getWidth();
        int newHeight = image.getHeight();
        if (orientation >= 5) {
            newWidth = image.getHeight();
            newHeight = image.getWidth();
        }

        BufferedImage rotatedImage = new BufferedImage(newWidth, newHeight, image.getType());
        return op.filter(image, rotatedImage);
    }
}
