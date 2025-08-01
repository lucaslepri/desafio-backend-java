package com.itau.desafio.vendas.audit.infrastructure.aws.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import com.amazonaws.xray.jakarta.servlet.AWSXRayServletFilter;
import jakarta.servlet.Filter;

@Configuration
@Profile("s3")
public class AWSS3Config {

    @Bean
    public S3Client s3Client(@Value("${audit.s3.region}") String region) {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .build();
    }

    @Bean
    public Filter tracingFilter() {
        return new AWSXRayServletFilter("ms-audit");
    }
}