package com.itau.desafio.vendas.audit.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;

@Component
@Profile("s3")
public class S3BucketHealthIndicator implements HealthIndicator {

    private final S3Client s3Client;
    private final String bucketName;

    public S3BucketHealthIndicator(S3Client s3Client, @Value("${audit.s3.bucket-name}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    @Override
    public Health health() {
        try {
            HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();
            s3Client.headBucket(headBucketRequest);
            return Health.up()
                    .withDetail("bucketName", bucketName)
                    .withDetail("message", "Acesso ao bucket S3 normal.")
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withException(e)
                    .withDetail("bucketName", bucketName)
                    .build();
        }
    }
}