package com.langthang.services;

import com.langthang.dto.CommentDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICommentServices {

    CommentDTO addNewComment(int postId, Integer parentId, String content, String accEmail);

    CommentDTO modifyComment(int commentId, String content, String accEmail);

    int deleteComment(int commentId, String accEmail);

    List<CommentDTO> getAllCommentOfPost(int postId, Pageable pageable);

    List<CommentDTO> getAllCommentOfPost(String slug, Pageable pageable);

    int likeOrUnlikeComment(int commentId, String accEmail);
}