package net.nicholaspurdy.gtrslicegrabber.services;

import io.findify.s3mock.S3Mock;

import net.nicholaspurdy.gtrslicegrabber.config.S3Config;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * IMPORTANT NOTE: This unit test is more of an integration test in that it needs to be able to communicate with S3.
 */
@RunWith(SpringRunner.class)
@ActiveProfiles({"test"})
@ContextConfiguration(initializers = ConfigFileApplicationContextInitializer.class, classes = {
        S3Config.class,
        PropertySourcesPlaceholderConfigurer.class
})
public class TestS3Uploader {

    @Autowired
    private S3Client s3Client;

    @Value("${slice.bucket.suffix}")
    private String suffix;

    private File zipFile;
    private File resultFile;
    private S3Mock api;

    @Before
    public void setup() throws IOException {

        zipFile = new File(System.getProperty("java.io.tmpdir") + "/CUMULATIVE_CREDITS_2016_01_01.zip");
        zipFile.createNewFile();

        resultFile = new File(System.getProperty("java.io.tmpdir") + "/result.zip");

        api = S3Mock.create(8001, "/tmp/s3");
        api.start();

        s3Client.createBucket(CreateBucketRequest.builder().bucket("gtr-cumulative-slice-zips-"+suffix).build());

    }

    @After
    public void tearDown() {

        zipFile.delete();
        resultFile.delete();

        DeleteObjectRequest dor = DeleteObjectRequest.builder()
                .bucket("gtr-cumulative-slice-zips-"+suffix)
                .key("credits/"+zipFile.getName())
                .build();

        s3Client.deleteObject(dor);



    }

    @Test
    public void testUpload() {

        S3Uploader uploader = new S3Uploader(s3Client, suffix);


        PutObjectResponse poresponse = uploader.upload(zipFile);
        assertNotNull(poresponse.getValueForField("ETag", String.class));

        GetObjectRequest gorequest = GetObjectRequest.builder()
                .bucket("gtr-cumulative-slice-zips-"+suffix)
                .key("credits/"+zipFile.getName())
                .build();

        GetObjectResponse goresponse = s3Client.getObject(gorequest, resultFile.toPath());

        assertTrue("application/zip".equals(goresponse.getValueForField("ContentType", String.class).get()));

    }

}
