package com.langthang.repository;

import com.langthang.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CommentRepository extends CrudRepository<Comment, Integer> {

    Page<Comment> getCommentsByPost_Id(int postId, Pageable pageable);

    @Query(value = "select count(account_id) " +
            "from comment_like " +
            "where comment_id=?1", nativeQuery = true)
    int countCommentLike(int commentId);

    @Query("select count(c) " +
            "from Comment c  " +
            "where c.post.id=?1")
    int countCommentInPost(int postId);
}
