package com.langthang.services.impl;

import com.langthang.exception.NotFoundError;
import com.langthang.model.dto.v2.response.PostStatsDto;
import com.langthang.model.entity.Account;
import com.langthang.model.entity.BookmarkedPost;
import com.langthang.model.entity.Post;
import com.langthang.repository.BookmarkedPostRepo;
import com.langthang.repository.PostRepository;
import com.langthang.services.IBookmarkServices;
import com.langthang.specification.PostSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Service
@Transactional
public class BookmarkServiceImpl implements IBookmarkServices {

    private final PostRepository postRepo;

    private final BookmarkedPostRepo bookmarkRepo;

    @Override
    public PostStatsDto bookmarkPost(Integer postId, Account user) {
        return postRepo.findOne(PostSpec.isPublished(postId))
                .map(post -> new BookmarkedPost(user, post))
                .map(bp -> {
                    bookmarkRepo.save(bp);
                    return new PostStatsDto(postRepo.countBookmarks(postId), 0, true, postId);
                })
                .orElseThrow(() -> NotFoundError.build(Post.class));
    }

    @Override
    public PostStatsDto deleteBookmark(Integer postId, Account user) {
        return postRepo.findById(postId)
                .map(post -> new BookmarkedPost(user, post))
                .map(bp -> {
                    bookmarkRepo.delete(bp);
                    return new PostStatsDto(postRepo.countBookmarks(postId), 0, false, postId);
                })
                .orElseThrow(() -> NotFoundError.build(Post.class));
    }
}