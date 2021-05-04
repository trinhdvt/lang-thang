package com.langthang.controller.upload;

import com.langthang.annotation.ValidImage;
import com.langthang.services.IStorageServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
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
    @PostMapping
    public ResponseEntity<Object> uploadFile(
            @RequestParam("image") @ValidImage MultipartFile multipartFile) {
        String uploadURL = storageServices.uploadFile(multipartFile);

        return new ResponseEntity<>(uploadURL, HttpStatus.OK);
    }


}
