package com.langthang.services.impl;

import com.langthang.exception.CustomException;
import com.langthang.model.entity.Account;
import com.langthang.model.entity.BookmarkedPost;
import com.langthang.model.entity.Post;
import com.langthang.repository.AccountRepository;
import com.langthang.repository.BookmarkedPostRepo;
import com.langthang.repository.PostRepository;
import com.langthang.services.IBookmarkServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BookmarkServiceImpl implements IBookmarkServices {

    @Autowired
    private AccountRepository accRepo;

    @Autowired
    private PostRepository postRepo;

    @Autowired
    private BookmarkedPostRepo bookmarkRepo;

    @Override
    public int bookmarkPost(int postId, String accEmail) {
        Post post = postRepo.findPostByIdAndStatus(postId, true);

        if (post == null) {
            throw new CustomException("Post with id: " + postId + " not found", HttpStatus.NOT_FOUND);
        }

        Account acc = accRepo.findByEmail(accEmail);

        BookmarkedPost newBookmark = new BookmarkedPost(acc, post);
        bookmarkRepo.save(newBookmark);

        return postRepo.countBookmarks(postId);
    }

    @Override
    public int deleteBookmark(int postId, String accEmail) {
        BookmarkedPost existingBookmark = bookmarkRepo.findBookmarkedPostByPost_IdAndAccount_Email(postId, accEmail);

        if (existingBookmark == null) {
            throw new CustomException("Post with id: " + postId + " not found", HttpStatus.NOT_FOUND);
        }
        bookmarkRepo.delete(existingBookmark);

        return postRepo.countBookmarks(postId);
    }
}
