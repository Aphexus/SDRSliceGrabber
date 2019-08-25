package net.nicholaspurdy.gtrslicegrabber.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;
import java.net.URISyntaxException;

@PropertySources({
        @PropertySource("classpath:aws.properties")
})
@Configuration
public class S3Config {

    @Bean
    @Profile("test")
    public S3Client testS3Client(Environment env) throws URISyntaxException {

        return S3Client.builder()
                .credentialsProvider(AnonymousCredentialsProvider.create())
                .endpointOverride(new URI("http://127.0.0.1:8001"))
                .region(Region.of(env.getProperty("aws.region")))
                .build();

    }

    @Bean
    @Profile("default")
    public S3Client defaultS3Client(Environment env) {

        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(
                env.getProperty("aws.accessKeyId"),
                env.getProperty("aws.secretAccessKey"));

        return S3Client.builder()
                .region(Region.of(env.getProperty("aws.region")))
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .build();
    }

    @Bean
    @Profile("!default & !test")
    public S3Client s3Client() {
        return S3Client.create();
    }

}
