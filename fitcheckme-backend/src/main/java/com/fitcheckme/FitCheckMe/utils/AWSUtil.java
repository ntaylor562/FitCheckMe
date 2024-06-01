package com.fitcheckme.FitCheckMe.utils;

import java.time.Duration;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Component
public class AWSUtil {
	@Value("${fitcheckme.aws-region-name}")
	private String regionName;

	@Value("${fitcheckme.aws-access-key-id}")
	private String accessKeyId;

	@Value("${fitcheckme.aws-access-key-secret}")
	private String accessKeySecret;

	@Value("${fitcheckme.aws-region-name}")
	private String s3RegionName;

	public String createPresignedPutUrl(String bucketName, String keyName, Map<String, String> metadata) {
		try (S3Presigner presigner = S3Presigner.builder()
		.region(Region.of(regionName))
		.credentialsProvider(
			StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, accessKeySecret))
		)
		.build()) {
			PutObjectRequest objectRequest = PutObjectRequest.builder()
					.bucket(bucketName)
					.key(keyName)
					.metadata(metadata)
					.build();

			PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
					.signatureDuration(Duration.ofMinutes(1)) // The URL expires in 10 minutes.
					.putObjectRequest(objectRequest)
					.build();

			PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
			// String myURL = presignedRequest.url().toString();
			// logger.info("Presigned URL to upload a file to: [{}]", myURL);
			// logger.info("HTTP method: [{}]", presignedRequest.httpRequest().method());

			return presignedRequest.url().toExternalForm();
		}
	}

}
