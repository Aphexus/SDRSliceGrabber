package net.nicholaspurdy.gtrslicegrabber.services;

import com.adobe.testing.s3mock.S3MockApplication;

import net.nicholaspurdy.gtrslicegrabber.job.steps.step1.S3Config;
import net.nicholaspurdy.gtrslicegrabber.job.steps.step1.S3Uploader;
import org.apache.commons.io.FileUtils;
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

    @Value("${slicegrabber.bucketName}")
    private String bucketName;

    private File zipFile;
    private File resultFile;
    private S3MockApplication s3Mock;

    @Before
    public void setup() throws IOException {

        zipFile = new File(System.getProperty("java.io.tmpdir") + "/CUMULATIVE_CREDITS_2016_01_01.zip");
        zipFile.createNewFile();

        resultFile = new File(System.getProperty("java.io.tmpdir") + "/result.zip");

        s3Mock = S3MockApplication.start("--root=" + System.getProperty("java.io.tmpdir") + "/S3Mock",
                "--initialBuckets=" + bucketName);

    }

    @After
    public void tearDown() throws IOException {

        zipFile.delete();
        resultFile.delete();

        DeleteObjectRequest dor = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key("credits/"+zipFile.getName())
                .build();

        s3Client.deleteObject(dor);

        s3Mock.stop();

        FileUtils.forceDelete(new File(System.getProperty("java.io.tmpdir") + "/S3Mock"));
    }

    @Test
    public void testUpload() {

        S3Uploader uploader = new S3Uploader(s3Client, bucketName);


        PutObjectResponse poresponse = uploader.upload(zipFile);
        assertNotNull(poresponse.getValueForField("ETag", String.class));

        GetObjectRequest gorequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key("credits/"+zipFile.getName())
                .build();

        GetObjectResponse goresponse = s3Client.getObject(gorequest, resultFile.toPath());

        assertTrue("application/zip".equals(goresponse.getValueForField("ContentType", String.class).get()));

    }

}
