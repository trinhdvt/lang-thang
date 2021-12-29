package com.langthang.services;

import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.Set;

public interface IStorageServices {

    void uploadImage(MultipartFile multipartFile, String filename);

    void uploadFile(String absPath);

    void deleteImages(Collection<String> filesName);

    Set<String> getAllImages();
}