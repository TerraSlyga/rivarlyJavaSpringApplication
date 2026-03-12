package com.example.rivarly.service;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Service class for interacting with MinIO server for file storage and retrieval operations.
 */
@Service
public class MinioService {


    public static final int EXPIRY_DAYS = 7;
    /**
     * The MinioClient instance used for communication with the MinIO server.
     * This client provides methods for file storage, retrieval, and other
     * operations necessary for interacting with the MinIO server.
     */
    private final MinioClient minioClient;

    /**
     * The name of the bucket in the MinIO server where files and objects are stored.
     * This value is usually configured through external properties or environment variables
     * and is injected into the service at runtime. It is used as a key parameter in MinIO
     * operations such as file uploads and generation of pre-signed URLs.
     */
    private final String bucketName;


    /**
     * Constructor for MinioService which initializes the MinIO client and sets the bucket name.
     * This service provides functionality to interact with the MinIO server, such as
     * uploading files and generating pre-signed URLs for accessing objects.
     *
     * @param url       The URL of the MinIO server.
     * @param accessKey The access key used for authentication with the MinIO server.
     * @param secretKey The secret key used for authentication with the MinIO server.
     * @param bucketName The name of the bucket to be used for storage and retrieval of objects.
     */
    public MinioService(
            @Value("${minio.url}") String url,
            @Value("${minio.access-key}") String accessKey,
            @Value("${minio.secret-key}") String secretKey,
            @Value("${minio.bucket-name}") String bucketName) {

        this.minioClient = MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
        this.bucketName = bucketName;
    }


    /**
     * Uploads a file to the specified MinIO bucket with a uniquely generated file name.
     * The original file name is appended to a random UUID to ensure uniqueness.
     *
     * @param file the MultipartFile representing the file to be uploaded
     *             containing metadata such as content type, original name, and content.
     * @return the unique name of the uploaded file in the storage bucket.
     * @throws RuntimeException if an error occurs during the file upload process.
     */
    public String uploadFile(MultipartFile file) {
        try {

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            InputStream inputStream = file.getInputStream();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return fileName;
        } catch (Exception e) {
            throw new RuntimeException("Error while loading image in MinIO", e);
        }
    }


    /**
     * Generates a pre-signed URL for accessing a file stored in the MinIO bucket.
     * The URL allows the client to retrieve the file using HTTP GET requests for a limited time.
     *
     * @param fileName the name of the file whose pre-signed URL is to be generated
     * @return a String containing the pre-signed URL
     * @throws RuntimeException if an error occurs while generating the pre-signed URL
     */
    public String getFileUrl(String fileName) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(fileName)
                            .expiry(EXPIRY_DAYS, TimeUnit.DAYS)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error while getting URL for MinIo image", e);
        }
    }
}
