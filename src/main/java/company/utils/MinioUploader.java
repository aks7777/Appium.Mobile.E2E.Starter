package company.utils;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class MinioUploader {

    private static final String MINIO_URL = ReadProperties.getValue("minioUrl");
    private static final String ACCESS_KEY = ReadProperties.getValue("minioAccessKey");
    private static final String SECRET_KEY = ReadProperties.getValue("minioSecretKey");
    private static final String BUCKET_NAME = ReadProperties.getValue("minioBucketName");
    private static final boolean ENABLE_MINIO = Boolean.parseBoolean(ReadProperties.getValue("enableMinio"));
    private static final String MINIO_REGION = ReadProperties.getValue("minioRegion");

    public String uploadFile(String folderName, File file) {
        // Check if Minio upload is enabled
        if (!ENABLE_MINIO) {
            System.out.println("Minio upload is disabled.");
            return null;
        }

        try {
            String fileName = file.getName();

            // Only .mp4 files are allowed
            if (!fileName.toLowerCase().endsWith(".mp4")) {
                System.err.println("Unsupported file format. Only .mp4 files are allowed.");
                return null;
            }

            // Create Minio client object
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(MINIO_URL)
                    .credentials(ACCESS_KEY, SECRET_KEY)
                    .region(MINIO_REGION)
                    .build();



            InputStream inputStream = new FileInputStream(file);

            long startTime = System.currentTimeMillis(); // Start time

            // Upload file to Minio
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(folderName + "/" + fileName)
                            .stream(inputStream, file.length(), -1)
                            .contentType("video/mp4")
                            .build()
            );

            long endTime = System.currentTimeMillis(); // End time

            // Calculate upload time in seconds
            long timeTakenMillis = endTime - startTime;
            double timeTakenSeconds = timeTakenMillis / 1000.0;
            double timeTakenMinutes = timeTakenSeconds / 60.0;

            // File size in MB
            double fileSizeMB = file.length() / (1024.0 * 1024.0);

            // Upload speed in MB/sec
            double uploadSpeedMBps = fileSizeMB / timeTakenSeconds;

            // Print upload details
            System.out.printf("Upload completed: Time taken = %.2f minutes (%.2f seconds), Speed = %.2f MB/sec\n",
                    timeTakenMinutes, timeTakenSeconds, uploadSpeedMBps);

            // Return the file URL
            return MINIO_URL + "/" + BUCKET_NAME + "/" + folderName + "/" + fileName;

        } catch (Exception e) {
            System.err.println("Exception occurred: " + e.getMessage());
            return null;
        }
    }
}
