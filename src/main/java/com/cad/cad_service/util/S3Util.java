package com.cad.cad_service.util;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.MultipleFileDownload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferProgress;
import com.cad.cad_service.controller.CadController;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.PropertySource;
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
import java.text.DecimalFormat;
import java.util.Base64;

@Component
@RequiredArgsConstructor
//@PropertySource(value = "application.properties")
public class S3Util {
    private final Logger log = LoggerFactory.getLogger(CadController.class);
    private final TransferManager transferManager;
    private final AmazonS3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;
    @Value("${cloud.aws.s3.algorithm}")
    public String algorithm;
    @Value("${cloud.aws.s3.key}")
    public String key;

    @MeasureExecutionTime
    public void downloadFolder(String project) {
        try {
            File s3Dir = new File("s3-download");
            project = URLDecoder.decode(project, StandardCharsets.UTF_8);
            MultipleFileDownload download = transferManager.downloadDirectory(bucket, project, s3Dir);

            DecimalFormat decimalFormat = new DecimalFormat("##0.00");

//            while (!download.isDone()) {
//                TransferProgress progress = download.getProgress();
//                double percent = progress.getPercentTransferred();
//                System.out.println("[ download ]" + decimalFormat.format(percent) + "% download progressing ...");
//                Thread.sleep(3000);
//            }
        } catch (AmazonServiceException e) {
            log.error("Amazon service exception: ", e);
        }
//        } catch (InterruptedException e) {
//            log.error("Thread sleep exception: ", e);
//        }
}

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
