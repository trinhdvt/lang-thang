package com.langthang.controller.v1;

import com.langthang.model.dto.response.PostResponseDTO;
import com.langthang.services.IPostServices;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@RestController
public class BookmarkController {

    private final IPostServices postServices;

    @GetMapping("/bookmark/posts")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    public List<PostResponseDTO> getListOfBookmarkedPost(
            @PageableDefault Pageable pageable,
            Authentication authentication) {

        String currentEmail = authentication.getName();

        return postServices.getBookmarkedPostOfUser(currentEmail, pageable);
    }
}