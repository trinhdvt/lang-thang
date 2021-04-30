package com.langthang.services;

import org.springframework.web.multipart.MultipartFile;

public interface StorageServices {
    String uploadFile(MultipartFile multipartFile);

    String deleteFile(String filename);
}
