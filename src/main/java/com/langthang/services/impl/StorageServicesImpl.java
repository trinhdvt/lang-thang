package com.langthang.services.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.langthang.exception.HttpError;
import com.langthang.services.IStorageServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class is responsible for uploading and deleting files to AWS S3
 *
 * @deprecated This class is deprecated because it is not used anymore
 */
@Service
@Slf4j
@Deprecated(forRemoval = true)
public class StorageServicesImpl implements IStorageServices {

    private final AmazonS3 s3Client;
    @Value("${cloud.aws.bucket.image-bucket}")
    private String imageBucket;
    @Value("${cloud.aws.bucket.backup-bucket}")
    private String fileBucket;

    @Autowired
    public StorageServicesImpl(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    @Async
    public void uploadImage(MultipartFile multipartFile, String filename) throws IOException {
        File uploadFile = convertToFile(multipartFile);

        PutObjectRequest objectRequest = new PutObjectRequest(imageBucket, filename, uploadFile);
        s3Client.putObject(objectRequest);

        Files.delete(uploadFile.toPath());
    }

    @Override
    public void uploadFile(String absPath) throws IOException {
        File file = new File(absPath);
        if (!file.exists() || !file.isFile()) {
            throw new FileNotFoundException("File not exist");
        }

        String fileName = file.getName();
        PutObjectRequest objectRequest = new PutObjectRequest(fileBucket, fileName, file);
        s3Client.putObject(objectRequest);

        Files.delete(file.toPath());
    }

    @Override
    public void deleteImages(Collection<String> filesName) {
        try {
            DeleteObjectsRequest dor = new DeleteObjectsRequest(imageBucket)
                    .withKeys(filesName.toArray(new String[0]));
            s3Client.deleteObjects(dor);
        } catch (AmazonServiceException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public Set<String> getAllImages() {
        ObjectListing objectListing = s3Client.listObjects(imageBucket);
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
                throw new HttpError("Cannot upload this file: " + ex.getMessage(),
                        HttpStatus.UNPROCESSABLE_ENTITY);
            }
        } else {
            throw new HttpError("File name is null", HttpStatus.BAD_REQUEST);
        }
    }

}