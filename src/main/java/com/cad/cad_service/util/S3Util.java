package com.cad.cad_service.util;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.*;
import com.cad.cad_service.controller.CadController;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.concurrent.*;

@Component
@RequiredArgsConstructor
public class S3Util {
    private final Logger log = LoggerFactory.getLogger(CadController.class);
    private final TransferManager transferManager;
    private final AmazonS3Client s3Client;
    private static final int TIMEOUT = 5;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;
    @Value("${cloud.aws.s3.algorithm}")
    public String algorithm;
    @Value("${cloud.aws.s3.key}")
    public String key;

    @MeasureExecutionTime
    public void downloadFolder(String project) {
        try {
            project = URLDecoder.decode(project, StandardCharsets.UTF_8);
            ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            ListObjectsV2Result result = s3Client.listObjectsV2(bucket, project);
            for (S3ObjectSummary summary: result.getObjectSummaries()) {
                GetObjectRequest getObjectRequest = new GetObjectRequest(bucket, summary.getKey());
                executor.submit(() -> {
                    s3Client.getObject(getObjectRequest, new File(summary.getKey()));
                });
            }
            executor.shutdown();
            executor.awaitTermination(TIMEOUT, TimeUnit.MINUTES);
        } catch (AmazonServiceException e) {
            log.error("Amazon service exception: ", e);
        } catch (InterruptedException e) {
            log.error("awaitTermination exception: ", e);
        }
    }

//    @MeasureExecutionTime
//    public void downloadFolder(String project) { // 리펙토링 전 S3 다운로드 함수
//        try {
//            File s3Dir = new File("s3-download");
//            project = URLDecoder.decode(project, StandardCharsets.UTF_8);
//            MultipleFileDownload download = transferManager.downloadDirectory(bucket, project, s3Dir);
//            download.waitForCompletion();
//        } catch (AmazonServiceException e) {
//            log.error("Amazon service exception: ", e);
//        } catch (InterruptedException e) {
//            log.error("Thread sleep exception: ", e);
//        }
//    }

    public String uploadImg(String title, ByteArrayOutputStream outputStream) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(outputStream.size());

            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            title = title.replace(".dwg", ".jpeg");
            PutObjectRequest request = new PutObjectRequest(bucket, title, inputStream, metadata);
            s3Client.putObject(request);
            String pathUrl = s3Client.getUrl(bucket, title).toString();
            outputStream.close();
            inputStream.close();
            return pathUrl;
        } catch (IOException e) {
            log.error("Stream IOException: ", e);
            return null;
        }
    }

    public String encryptImgUrl(String imgUrl) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), algorithm);
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encryptedBytes = cipher.doFinal(imgUrl.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            log.error("Cipher getInstance error: ", e);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            log.error("Cipher doFinal error: ", e);
        }
        return "";
    }
}
