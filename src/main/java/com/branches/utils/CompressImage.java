package com.branches.utils;

import com.branches.exception.InternalServerError;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Iterator;

@Slf4j
@Component
public class CompressImage {

    static {
        // Registrar suporte para HEIC/HEIF
        try {
            Class.forName("com.github.gotson.nightmonkeys.heif.imageio.plugins.HeifImageReaderSpi");
            log.info("HEIC/HEIF image reader registered successfully");
        } catch (ClassNotFoundException e) {
            log.warn("HEIC/HEIF image reader not available: {}", e.getMessage());
        }
    }

    public byte[] execute(String imageBase64, int maxWidth, int maxHeight, double percentageQuality, ImageOutPutFormat outputFormat) {
        try {
            log.info("Iniciando compressão de imagem");
            log.info("maxWidth: {}, maxHeight: {}, percentageQuality: {}, outputFormat: {}", maxWidth, maxHeight, percentageQuality, outputFormat);
            String formattedImage = imageBase64.substring(imageBase64.indexOf(",") + 1);

            byte[] imageBytes = Base64.getDecoder().decode(formattedImage);

            BufferedImage originalImage = readImage(imageBytes);

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

    private BufferedImage readImage(byte[] imageBytes) throws Exception {
        ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);

        // Tentar ler a imagem com ImageIO (suporta JPEG, PNG, HEIC via plugin)
        BufferedImage image = ImageIO.read(bais);

        if (image != null) {
            return image;
        }

        // Se falhou, tentar com ImageInputStream e iterador de readers
        bais.reset();
        try (ImageInputStream iis = ImageIO.createImageInputStream(bais)) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);

            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                try {
                    reader.setInput(iis);
                    image = reader.read(0);
                    log.info("Imagem lida usando reader: {}", reader.getClass().getSimpleName());
                    return image;
                } finally {
                    reader.dispose();
                }
            }
        }

        throw new InternalServerError("Formato de imagem não suportado");
    }

    private BufferedImage correctImageOrientation(byte[] imageBytes, BufferedImage originalImage) {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(new ByteArrayInputStream(imageBytes));
            ExifIFD0Directory exifIFD0Directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

            if (exifIFD0Directory == null) {
                return originalImage;
            }

            if (!exifIFD0Directory.containsTag(ExifIFD0Directory.TAG_ORIENTATION)) {
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

        int width = image.getWidth();
        int height = image.getHeight();

        int newWidth = width;
        int newHeight = height;
        switch (orientation) {
            case 1:
                return image;

            case 2: // Flip horizontal
                transform.scale(-1, 1);
                transform.translate(-width, 0);
                break;

            case 3: // Rotate 180
                transform.translate(width, height);
                transform.rotate(Math.PI);
                break;

            case 4: // Flip vertical
                transform.scale(1, -1);
                transform.translate(0, -height);
                break;

            case 5: // Transpose
                transform.rotate(Math.PI / 2);
                transform.scale(1, -1);
                newWidth = height;
                newHeight = width;
                break;

            case 6: // Rotate 90 CW
                transform.translate(height, 0);
                transform.rotate(Math.PI / 2);
                newWidth = height;
                newHeight = width;
                break;

            case 7: // Transverse
                transform.rotate(-Math.PI / 2);
                transform.scale(1, -1);
                newWidth = height;
                newHeight = width;
                break;

            case 8: // Rotate 270
                transform.translate(0, width);
                transform.rotate(3 * Math.PI / 2);
                newWidth = height;
                newHeight = width;
                break;

            default:
                return image;
        }

        int imageType = image.getType() == BufferedImage.TYPE_CUSTOM
                ? BufferedImage.TYPE_INT_ARGB
                : image.getType();

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, imageType);
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        return op.filter(image, rotated);
    }
}
