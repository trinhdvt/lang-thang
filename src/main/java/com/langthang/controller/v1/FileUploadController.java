package com.langthang.controller.v1;

import com.langthang.annotation.ValidImage;
import com.langthang.services.IStorageServices;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;

@RestController
@Validated
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class FileUploadController {

    private final IStorageServices storageServices;

    @PostMapping(value = "/file")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> uploadFile(@RequestParam("image") @ValidImage
                                             MultipartFile multipartFile) throws IOException {
        var publicUrl = storageServices.uploadImage(multipartFile).join();
        return ResponseEntity.ok(Collections.singletonMap("url", publicUrl));
    }
}