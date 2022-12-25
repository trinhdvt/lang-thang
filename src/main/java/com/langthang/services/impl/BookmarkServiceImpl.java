package com.langthang.services.impl;

import com.langthang.exception.HttpError;
import com.langthang.exception.NotFoundError;
import com.langthang.model.entity.Account;
import com.langthang.model.entity.BookmarkedPost;
import com.langthang.model.entity.Post;
import com.langthang.repository.AccountRepository;
import com.langthang.repository.BookmarkedPostRepo;
import com.langthang.repository.PostRepository;
import com.langthang.services.IBookmarkServices;
import com.langthang.specification.PostSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Service
@Transactional
public class BookmarkServiceImpl implements IBookmarkServices {

    private final AccountRepository accRepo;

    private final PostRepository postRepo;

    private final BookmarkedPostRepo bookmarkRepo;

    @Override
    public int bookmarkPost(int postId, String currentEmail) {
        bookmarkRepo.findBookmarkedPostByPost_IdAndAccount_Email(postId, currentEmail)
                .orElseThrow(() -> new HttpError("Already bookmarked", HttpStatus.NO_CONTENT));

        Post post = postRepo.findOne(PostSpec.isPublished(postId))
                .orElseThrow(() -> NotFoundError.build(Post.class));

        Account currentAcc = accRepo.getByEmail(currentEmail);
        BookmarkedPost newBookmark = new BookmarkedPost(currentAcc, post);
        bookmarkRepo.save(newBookmark);
        return postRepo.countBookmarks(postId);
    }

    @Override
    public int deleteBookmark(int postId, String accEmail) {
        return bookmarkRepo.findBookmarkedPostByPost_IdAndAccount_Email(postId, accEmail)
                .map(bp -> {
                    bookmarkRepo.delete(bp);
                    return postRepo.countBookmarks(postId);
                })
                .orElseThrow(() -> new NotFoundError(BookmarkedPost.class));
    }
}