package com.langthang.services.impl;

import com.langthang.exception.HttpError;
import com.langthang.exception.NotFoundError;
import com.langthang.model.Account;
import com.langthang.model.BookmarkedPost;
import com.langthang.model.Post;
import com.langthang.repository.AccountRepository;
import com.langthang.repository.BookmarkedPostRepo;
import com.langthang.repository.PostRepository;
import com.langthang.services.IBookmarkServices;
import com.langthang.utils.AssertUtils;
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
        BookmarkedPost existingBookmark = bookmarkRepo.findBookmarkedPostByPost_IdAndAccount_Email(postId, currentEmail);

        AssertUtils.isNull(existingBookmark, new HttpError("Already bookmarked", HttpStatus.NO_CONTENT));

        Post post = postRepo.findPostByIdAndPublished(postId, true);

        AssertUtils.notNull(post, new NotFoundError("Post not found"));

        Account currentAcc = accRepo.findAccountByEmail(currentEmail);

        BookmarkedPost newBookmark = new BookmarkedPost(currentAcc, post);
        bookmarkRepo.save(newBookmark);

        return postRepo.countBookmarks(postId);
    }

    @Override
    public int deleteBookmark(int postId, String accEmail) {
        BookmarkedPost existingBookmark = bookmarkRepo.findBookmarkedPostByPost_IdAndAccount_Email(postId, accEmail);

        AssertUtils.notNull(existingBookmark, new NotFoundError("Bookmark not found"));

        bookmarkRepo.delete(existingBookmark);

        return postRepo.countBookmarks(postId);
    }
}