package com.langthang.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

public interface IStorageServices {

    void uploadImage(MultipartFile multipartFile, String filename) throws IOException;

    void uploadFile(String absPath) throws IOException;

    void deleteImages(Collection<String> filesName);

    Set<String> getAllImages();
}