package com.langthang.controller.v2;

import com.langthang.controller.v2.definition.BookmarkAPI;
import com.langthang.model.dto.v2.response.PostStatsDto;
import com.langthang.security.services.CurrentUser;
import com.langthang.services.IBookmarkServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Validated
@Slf4j
public class BookmarkControllerV2 implements BookmarkAPI {

    private final IBookmarkServices bookmarkServices;

    @Override
    public PostStatsDto bookmarkPost(
            @PathVariable Integer postId,
            @AuthenticationPrincipal CurrentUser currentUser) {
        return bookmarkServices.bookmarkPost(postId, currentUser.getSource());
    }

    @Override
    public PostStatsDto removeBookmark(
            @PathVariable Integer postId,
            @AuthenticationPrincipal CurrentUser currentUser) {
        return bookmarkServices.deleteBookmark(postId, currentUser.getSource());
    }

}
