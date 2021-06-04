package com.langthang.services;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public interface IStorageServices {
    String uploadFile(MultipartFile multipartFile);

    String deleteFile(String filename);

    void deleteFiles(List<String> filesName);

    Set<String> getAllFiles();
}
