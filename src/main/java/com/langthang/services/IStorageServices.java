package com.langthang.services;

import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.Set;

public interface IStorageServices {
    String uploadImage(MultipartFile multipartFile);

    void uploadFile(String absPath);

    void deleteImage(String filename);

    void deleteImages(Collection<String> filesName);

    Set<String> getAllImages();
}
