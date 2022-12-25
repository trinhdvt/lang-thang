package com.langthang.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface IStorageServices {

    CompletableFuture<String> uploadImage(MultipartFile multipartFile) throws IOException;

    void deleteImages(Collection<String> filesName);

    Set<String> getAllImages();
}