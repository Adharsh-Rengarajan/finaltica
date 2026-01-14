package com.finaltica.application.service;

import java.io.ByteArrayInputStream;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Service
public class S3Service {

	@Autowired
	private S3Client s3Client;

	@Value("${aws.s3.bucket-name}")
	private String bucketName;

	@Value("${aws.region}")
	private String region;

	public String uploadFile(String fileName, byte[] fileContent, String contentType) {
		PutObjectRequest putRequest = PutObjectRequest.builder().bucket(bucketName).key(fileName)
				.contentType(contentType).build();

		s3Client.putObject(putRequest,
				RequestBody.fromInputStream(new ByteArrayInputStream(fileContent), fileContent.length));

		return fileName;
	}

	public String generatePresignedUrl(String fileName) {
		S3Presigner presigner = S3Presigner.builder().region(software.amazon.awssdk.regions.Region.of(region)).build();

		GetObjectPresignRequest getObjectRequest = GetObjectPresignRequest.builder()
				.signatureDuration(Duration.ofHours(1)).getObjectRequest(req -> req.bucket(bucketName).key(fileName))
				.build();

		PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(getObjectRequest);
		String url = presignedRequest.url().toString();

		presigner.close();
		return url;
	}

	public void deleteFile(String fileName) {
		DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder().bucket(bucketName).key(fileName).build();

		s3Client.deleteObject(deleteRequest);
	}
}