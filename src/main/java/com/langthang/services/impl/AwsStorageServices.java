package com.langthang.services.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.langthang.services.StorageServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AwsStorageServices implements StorageServices {

    @Value("${cloud.aws.bucket.name}")
    private String bucketName;

    @Value("${cloud.aws.public.base-url}")
    private String basePublicURL;

    private final AmazonS3 s3Client;

    @Autowired
    public AwsStorageServices(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public String uploadFile(MultipartFile multipartFile) {
        File uploadFile = convertToFile(multipartFile);
        String filename = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();

//        PutObjectRequest objectRequest = new PutObjectRequest(bucketName, filename, uploadFile);
//        objectRequest.withCannedAcl(CannedAccessControlList.PublicRead);
//        s3Client.putObject(objectRequest);

        uploadFile.delete();
        return basePublicURL + "/" + filename;
    }

    @Override
    public String deleteFile(String filename) {
        s3Client.deleteObject(bucketName, filename);
        return filename;
    }

    public List<String> getAllFileInBucket() {
        ObjectListing objectListing = s3Client.listObjects(bucketName);
        return objectListing.getObjectSummaries()
                .stream()
                .map(S3ObjectSummary::getKey)
                .collect(Collectors.toList());
    }

    private File convertToFile(MultipartFile multipartFile) {
        if (multipartFile.getOriginalFilename() != null) {
            File convertedFile = new File(multipartFile.getOriginalFilename());

            try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
                fos.write(multipartFile.getBytes());
                return convertedFile;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
}
