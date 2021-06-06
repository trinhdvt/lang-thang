package com.langthang.services.impl;

import com.langthang.dto.CommentDTO;
import com.langthang.exception.CustomException;
import com.langthang.model.Account;
import com.langthang.model.Comment;
import com.langthang.model.Post;
import com.langthang.repository.AccountRepository;
import com.langthang.repository.CommentRepository;
import com.langthang.repository.PostRepository;
import com.langthang.services.ICommentServices;
import com.langthang.services.INotificationServices;
import com.langthang.utils.constraints.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Service
@Transactional
public class CommentServicesImpl implements ICommentServices {

    private final CommentRepository commentRepo;

    private final AccountRepository accRepo;

    private final PostRepository postRepo;

    private final INotificationServices notificationServices;

    @Override
    public CommentDTO addNewComment(int postId, String content, String commenterEmail) {
        Post post = postRepo.findPostByIdAndPublished(postId, true);

        if (post == null) {
            throw new CustomException("Post not found!", HttpStatus.NOT_FOUND);
        }

        Account commenter = accRepo.findAccountByEmail(commenterEmail);
        Comment comment = new Comment(commenter, post, content);

        Comment savedComment = commentRepo.save(comment);

        return CommentDTO.toCommentDTO(savedComment);
    }

    @Override
    public CommentDTO modifyComment(int commentId, String content, String accEmail) {
        Comment oldComment = commentRepo.findById(commentId).orElse(null);

        if (oldComment == null) {
            throw new CustomException("Not found", HttpStatus.NOT_FOUND);
        }

        Account commenter = oldComment.getAccount();
        if (!commenter.getEmail().equals(accEmail)) {
            throw new CustomException("Access denied", HttpStatus.UNAUTHORIZED);
        }

        oldComment.setContent(content);
        Comment newComment = commentRepo.save(oldComment);

        return CommentDTO.toCommentDTO(newComment);
    }

    @Override
    public int deleteComment(int commentId, String accEmail) {
        Comment existingComment = commentRepo.findById(commentId).orElse(null);

        if (existingComment == null) {
            throw new CustomException("Not found", HttpStatus.NOT_FOUND);
        }

        if (!existingComment.getAccount().getEmail().equals(accEmail)) {
            throw new CustomException("Access denied", HttpStatus.UNAUTHORIZED);
        }

        commentRepo.delete(existingComment);
        return commentRepo.countCommentInPost(existingComment.getPost().getId());
    }

    @Override
    public List<CommentDTO> getAllCommentOfPost(int postId, Pageable pageable) {

        if (!postRepo.existsByIdAndPublished(postId, true)) {
            throw new CustomException("Not found", HttpStatus.NOT_FOUND);
        }

        return commentRepo.getCommentsByPost_Id(postId, pageable)
                .map(CommentDTO::toCommentDTO)
                .getContent();
    }

    @Override
    public List<CommentDTO> getAllCommentOfPost(String slug, Pageable pageable) {
        Post post = postRepo.findPostBySlugAndPublished(slug, true);
        if (post == null) {
            throw new CustomException("Not found", HttpStatus.NOT_FOUND);
        }

        return commentRepo.getCommentsByPost_Id(post.getId(), pageable)
                .map(CommentDTO::toCommentDTO)
                .getContent();
    }

    @Override
    public int likeOrUnlikeComment(int commentId, String currentEmail) {
        Comment comment = commentRepo.findById(commentId).orElse(null);

        if (comment == null) {
            throw new CustomException("Comment not found", HttpStatus.NOT_FOUND);
        }

        Account currentAcc = accRepo.findAccountByEmail(currentEmail);
        boolean isLiked = currentAcc.getLikedComments().removeIf(cm -> cm.getId() == commentId);

        if (!isLiked) {
            currentAcc.getLikedComments().add(comment);

            notificationServices.createNotification(currentAcc,
                    comment.getAccount(),
                    comment.getPost(),
                    NotificationType.LIKE);
        }

        accRepo.saveAndFlush(currentAcc);

        return commentRepo.countCommentLike(commentId);
    }
}
