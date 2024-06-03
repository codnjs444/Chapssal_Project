package com.chapssal.video;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;

import java.io.File;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

//AWS S3 클라우드 스토리지와 연동하기 위한 클래스

@Service
public class S3Service {

    private final S3Client s3;
    private final Region region;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    public S3Service() {
        this.region = Region.of("ap-northeast-2"); // 지역을 변수로 저장
        this.s3 = S3Client.builder()
                .region(region)
                .build();
    }

    public void uploadFile(String key, File file) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .acl(ObjectCannedACL.PUBLIC_READ) // 퍼블릭 읽기 권한 설정
                    .build();

            PutObjectResponse response = s3.putObject(putObjectRequest, Paths.get(file.getPath()));
            System.out.println("File uploaded successfully: " + response);
        } catch (S3Exception e) {
            System.err.println("S3 Exception: " + e.getMessage());
            System.err.println("AWS Error Code: " + e.awsErrorDetails().errorCode());
            System.err.println("AWS Error Message: " + e.awsErrorDetails().errorMessage());
            e.printStackTrace();
        }
    }

    public String getFileUrl(String key) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region.id(), key);
    }
}
