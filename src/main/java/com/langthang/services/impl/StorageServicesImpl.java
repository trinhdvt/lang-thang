package com.langthang.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.langthang.exception.HttpError;
import com.langthang.services.IStorageServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class StorageServicesImpl implements IStorageServices {

    @Value("${storage.bucket}")
    private String fileBucket;

    private final Cloudinary cloudinaryClient;

    @Override
    @Async
    public CompletableFuture<String> uploadImage(MultipartFile multipartFile) throws IOException {
        File uploadFile = convertToFile(multipartFile);
        var uploadResult = cloudinaryClient.uploader().upload(uploadFile, uploadConfig());

        String secureUrl = (String) uploadResult.get("secure_url");

        Files.delete(uploadFile.toPath());
        return CompletableFuture.completedFuture(secureUrl);
    }

    @Override
    public void deleteImages(Collection<String> filesName) {
        throw new NotImplementedException("Not implemented yet");
    }

    @Override
    public Set<String> getAllImages() {
        throw new NotImplementedException("Not implemented yet");
    }

    private File convertToFile(MultipartFile multipartFile) {
        if (multipartFile.getOriginalFilename() == null)
            throw new HttpError("File name is null", HttpStatus.BAD_REQUEST);

        try {
            File convertedFile = new File(multipartFile.getOriginalFilename());
            multipartFile.transferTo(convertedFile);
            return convertedFile;
        } catch (IOException ex) {
            throw new HttpError("Cannot upload this file: " + ex.getMessage(),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @SuppressWarnings({"rawtypes"})
    private Map uploadConfig() {
        return ObjectUtils.asMap(
                "resource_type", "image",
                "folder", fileBucket
        );
    }

}