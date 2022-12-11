package com.langthang.repository;

import com.langthang.model.entity.Comment;
import com.langthang.model.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    Page<Comment> getCommentsByPost(Post p, Pageable pageable);

    @Query(value = "select count(account_id) " +
                   "from comment_like " +
                   "where comment_id=?1", nativeQuery = true)
    int countCommentLike(int commentId);

    @Query("select count(c) " +
           "from Comment c  " +
            "where c.post.id=?1")
    int countCommentInPost(int postId);
}