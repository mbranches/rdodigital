package com.branches.external.aws;

import com.branches.exception.InternalServerError;
import com.branches.utils.FileContentType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Component
public class S3UploadFile {
    @Value("${aws.accessKeyId}")
    private String accessKeyId;
    @Value("${aws.secretAccessKey}")
    private String secretAccessKey;
    @Value("${aws.s3.bucketName}")
    private String bucketName;

    public String execute(String fileName, String path, byte[] fileContent, FileContentType contentType) {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);

        try(S3Client s3Client = S3Client.builder()
                .region(Region.US_EAST_2)
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build()) {
            log.info("Subindo arquivo {} para o S3 na pasta {}", fileName, path);

            String formattedFileName = fileName.replaceAll("\\.[^.]+$", "." + contentType.getExtension()).replaceAll(" ", "_");
            String fullFileName = path + "/" + formattedFileName;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fullFileName)
                    .contentType(contentType.getMimeType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileContent));

            log.info("Arquivo {} enviado com sucesso para o S3", fileName);

            return "https://%s.s3.us-east-2.amazonaws.com/%s".formatted(bucketName, fullFileName);
        } catch (Exception e) {
            throw new InternalServerError("Não foi possível salvar o arquivo: " + e.getMessage());
        }
    }
}
