package com.langthang.controller;

import com.langthang.annotation.ValidImage;
import com.langthang.exception.HttpError;
import com.langthang.services.IStorageServices;
import com.langthang.utils.AssertUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;

@RestController
@Validated
public class FileUploadController {

    private final IStorageServices storageServices;

    @Value("${cloud.aws.public.base-url}")
    private String basePublicURL;

    @Autowired
    public FileUploadController(IStorageServices IStorageServices) {
        this.storageServices = IStorageServices;
    }

    @PostMapping(value = "/upload")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> uploadFile(
            @RequestParam("upload") @ValidImage MultipartFile multipartFile) {
        String originFilename = StringUtils.deleteWhitespace(multipartFile.getOriginalFilename());
        AssertUtils.notNull(originFilename, new HttpError("File name is null", HttpStatus.BAD_REQUEST));

        String extension = originFilename.substring(originFilename.lastIndexOf("."));
        String filename = originFilename.replace(extension, "")
                + "-" + RandomStringUtils.randomAlphanumeric(5) + extension;

        String publicUrl = basePublicURL + "/" + filename;

        // upload asynchronously
        storageServices.uploadImage(multipartFile, filename);

        return ResponseEntity.ok(Collections.singletonMap("url", publicUrl));
    }
}