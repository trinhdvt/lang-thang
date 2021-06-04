package com.langthang.services.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.langthang.exception.CustomException;
import com.langthang.services.IStorageServices;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AwsStorageServicesImpl implements IStorageServices {

    @Value("${cloud.aws.bucket.name}")
    private String bucketName;

    @Value("${cloud.aws.public.base-url}")
    private String basePublicURL;

    private final AmazonS3 s3Client;

    @Autowired
    public AwsStorageServicesImpl(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public String uploadFile(MultipartFile multipartFile) {
        File uploadFile = convertToFile(multipartFile);
        String filename = System.currentTimeMillis() + "_" + StringUtils.deleteWhitespace(multipartFile.getOriginalFilename());

        PutObjectRequest objectRequest = new PutObjectRequest(bucketName, filename, uploadFile);
        objectRequest.withCannedAcl(CannedAccessControlList.PublicRead);
        s3Client.putObject(objectRequest);

        uploadFile.delete();
        return basePublicURL + "/" + filename;
    }

    @Override
    public String deleteFile(String filename) {
        s3Client.deleteObject(bucketName, filename);
        return filename;
    }

    @Override
    public void deleteFiles(List<String> filesName) {
        try {
            DeleteObjectsRequest dor = new DeleteObjectsRequest(bucketName)
                    .withKeys(filesName.toArray(new String[0]));
            s3Client.deleteObjects(dor);
        } catch (AmazonServiceException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public Set<String> getAllFiles() {
        ObjectListing objectListing = s3Client.listObjects(bucketName);
        return objectListing.getObjectSummaries()
                .stream()
                .map(S3ObjectSummary::getKey)
                .collect(Collectors.toSet());
    }

    private File convertToFile(MultipartFile multipartFile) {
        if (multipartFile.getOriginalFilename() != null) {
            File convertedFile = new File(multipartFile.getOriginalFilename());

            try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
                fos.write(multipartFile.getBytes());
                return convertedFile;
            } catch (IOException ex) {
                throw new CustomException("Cannot upload this file: " + ex.getMessage(),
                        HttpStatus.UNPROCESSABLE_ENTITY);
            }
        } else {
            throw new CustomException("File name is null", HttpStatus.BAD_REQUEST);
        }
    }
}
