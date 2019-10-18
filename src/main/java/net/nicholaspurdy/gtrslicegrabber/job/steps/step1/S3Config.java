package net.nicholaspurdy.gtrslicegrabber.job.steps.step1;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class S3Config {

    @Bean
    @Profile("default | test")
    public S3Client defaultS3Client(Environment env) throws URISyntaxException {

        return S3Client.builder()
                .credentialsProvider(AnonymousCredentialsProvider.create())
                .endpointOverride(new URI(env.getProperty("slicegrabber.s3mockUrl")))
                .region(Region.of(env.getProperty("aws.region")))
                .build();
    }

    @Bean
    @Profile("!default & !test")
    public S3Client s3Client() {
        return S3Client.create();
    }

}
