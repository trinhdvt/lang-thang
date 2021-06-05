package com.langthang.controller;

import com.langthang.annotation.ValidImage;
import com.langthang.services.IStorageServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@Validated
public class FileUploadController {

    private final IStorageServices storageServices;

    @Autowired
    public FileUploadController(IStorageServices IStorageServices) {
        this.storageServices = IStorageServices;
    }

    /**
     * Upload a file to AWS S3 Storage
     *
     * @param multipartFile file to upload
     * @return URL that client can access via browsers
     */
    @PostMapping(value = "/upload")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> uploadFile(
            @RequestParam("upload") @ValidImage MultipartFile multipartFile) {

        String publicUrl = storageServices.uploadImage(multipartFile);

        Map<String, String> response = new HashMap<>();
        response.put("url", publicUrl);
        return ResponseEntity.ok(response);
    }


}
