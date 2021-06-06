package com.langthang.services.impl;

import com.langthang.exception.CustomException;
import com.langthang.model.Account;
import com.langthang.model.BookmarkedPost;
import com.langthang.model.Post;
import com.langthang.repository.AccountRepository;
import com.langthang.repository.BookmarkedPostRepo;
import com.langthang.repository.PostRepository;
import com.langthang.services.IBookmarkServices;
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
        if (existingBookmark != null) {
            throw new CustomException("Already bookmarked", HttpStatus.NO_CONTENT);
        }

        Post post = postRepo.findPostByIdAndPublished(postId, true);

        if (post == null) {
            throw new CustomException("Post with id: " + postId + " not found", HttpStatus.NOT_FOUND);
        }

        Account currentAcc = accRepo.findAccountByEmail(currentEmail);

        BookmarkedPost newBookmark = new BookmarkedPost(currentAcc, post);
        bookmarkRepo.save(newBookmark);

        return postRepo.countBookmarks(postId);
    }

    @Override
    public int deleteBookmark(int postId, String accEmail) {
        BookmarkedPost existingBookmark = bookmarkRepo.findBookmarkedPostByPost_IdAndAccount_Email(postId, accEmail);

        if (existingBookmark == null) {
            throw new CustomException("Post with id: " + postId + " not bookmarked!", HttpStatus.NOT_FOUND);
        }
        bookmarkRepo.delete(existingBookmark);

        return postRepo.countBookmarks(postId);
    }
}
