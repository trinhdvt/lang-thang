package com.langthang.controller.comment;

import com.langthang.dto.CommentDTO;
import com.langthang.services.ICommentServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommentController {

    @Autowired
    private ICommentServices commentServices;

    @GetMapping("/comment/post/{post_id}")
    public ResponseEntity<Object> getCommentOfPost(
            @PathVariable("post_id") int postId,
            Authentication authentication) {

        String accEmail = authentication != null ? authentication.getName() : null;
        List<CommentDTO> commentList = commentServices.getAllCommentOfPost(postId, accEmail);

        return ResponseEntity.ok(commentList);
    }

    @PutMapping("/comment/like/{comment_id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> likeOrUnlikeComment(
            @PathVariable("comment_id") int commentId,
            Authentication authentication) {

        String accEmail = authentication.getName();
        commentServices.likeOrUnlikeComment(commentId, accEmail);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/comment/post/{post_id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> addCommentToPost(
            @PathVariable("post_id") int postId,
            @RequestParam("content") String content,
            Authentication authentication) {

        String accEmail = authentication.getName();

        CommentDTO newComment = commentServices.addNewComment(postId, content, accEmail);

        return ResponseEntity.ok(newComment);
    }

    @PutMapping("/comment/{comment_id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> modifyComment(
            @PathVariable("comment_id") int commentId,
            @RequestParam("content") String content,
            Authentication authentication) {

        String accEmail = authentication.getName();

        CommentDTO modifiedComment = commentServices.modifyComment(commentId, content, accEmail);

        return ResponseEntity.ok(modifiedComment);
    }

    @DeleteMapping("/comment/{comment_id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> deleteComment(
            @PathVariable("comment_id") int commentId,
            Authentication authentication) {

        String accEmail = authentication.getName();
        commentServices.deleteComment(commentId, accEmail);

        return ResponseEntity.noContent().build();
    }
}
