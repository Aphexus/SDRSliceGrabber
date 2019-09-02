package net.nicholaspurdy.gtrslicegrabber.job.steps.step1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.File;

@Service
public class S3Uploader {

    private static final Logger log = LoggerFactory.getLogger(S3Uploader.class);

    private final S3Client s3Client;
    private final String suffix;

    public S3Uploader(S3Client s3Client, @Value("${slice.bucket.suffix}") String suffix) {
        this.s3Client = s3Client;
        this.suffix = suffix;
    }

    /**
     * This method assumes that file being passed in has the standard file name convention ie.
     * CUMULATIVE_assetClass_date.zip.
     *
     * @param file This zip file.
     *
     */
    public PutObjectResponse upload(File file) {

        String bucketName = "gtr-cumulative-slice-zips-" + suffix;
        String key = file.getName().split("_")[1].toLowerCase() + "/" + file.getName();

        PutObjectRequest por = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        log.info("Uploading " + file.getName() + " to " + bucketName);

        return s3Client.putObject(por, RequestBody.fromFile(file));
    }

}
