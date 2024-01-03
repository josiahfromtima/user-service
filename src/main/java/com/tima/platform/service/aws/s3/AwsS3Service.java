package com.tima.platform.service.aws.s3;


import com.tima.platform.model.api.AppResponse;
import com.tima.platform.util.AppUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * @Author: Josiah Adetayo
 * @Email: josleke@gmail.com, josiah.adetayo@meld-tech.com
 * @Date: 12/10/23
 */
@Service
public class AwsS3Service {
    private final Logger logger = Logger.getLogger(AwsS3Service.class.getName());
    @Value("${aws.s3.signedUrl.timeToLive}")
    private int timeToLive;
    @Value("${aws.region}")
    private String region;
    @Value("${aws.s3.image-ext}")
    private String defaultFileExtension;
    private static final String BUCKET_NAME = "tima-resources";

    public Mono<AppResponse> getSignedUrl(String folder, String keyName, String ext) {
        return Mono.just(AppUtil.buildAppResponse(createPreSignedUrl(folder, keyName, ext), "Created Signed Url"));
    }

    /**
     * Create a presigned URL for uploading a String object.
     * @param folderName - The name of the folder in the bucket.
     * @param keyName - The name of the object.
     * @return - The presigned URL for an HTTP PUT.
     */
    private String createPreSignedUrl(String folderName, String keyName, String ext) {
        String message = BUCKET_NAME + " " + keyName+" "+folderName;
        logger.info(message);
        try (S3Presigner preSigner = buildSigner()) {
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(folderName.concat(checkExt(keyName, ext)))
                    .contentType(extension(ext))
                    .metadata(new HashMap<>())
                    .build();

            PutObjectPresignRequest preSignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(timeToLive))  // The URL will expire in 10 minutes.
                    .putObjectRequest(objectRequest)
                    .build();

            PresignedPutObjectRequest preSignedRequest = preSigner.presignPutObject(preSignRequest);
            return preSignedRequest.url().toString();
        }catch (RuntimeException e) {
            logger.severe(e.getLocalizedMessage());
            return "";
        }
    }

    private S3Presigner buildSigner() {
        return S3Presigner.builder().region(Region.of(region)).build();
    }

    private String checkExt(String file, String ext) {
        return file + extension(ext);
    }

    private String extension(String ext) {
        return (Objects.isNull(ext) || ext.isEmpty()) ? defaultFileExtension : ext;
    }

}
