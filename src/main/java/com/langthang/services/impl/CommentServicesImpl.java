package com.langthang.services.impl;

import com.langthang.config.RabbitMqConfig;
import com.langthang.event.model.NotificationRequest;
import com.langthang.exception.NotFoundError;
import com.langthang.model.constraints.NotificationType;
import com.langthang.model.dto.response.CommentDTO;
import com.langthang.model.entity.Account;
import com.langthang.model.entity.Comment;
import com.langthang.model.entity.Post;
import com.langthang.repository.AccountRepository;
import com.langthang.repository.CommentRepository;
import com.langthang.repository.PostRepository;
import com.langthang.services.ICommentServices;
import com.langthang.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.langthang.specification.PostSpec.hasSlug;
import static com.langthang.specification.PostSpec.isPublished;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Service
@Transactional
@Slf4j
public class CommentServicesImpl implements ICommentServices {

    private final CommentRepository commentRepo;

    private final AccountRepository accRepo;

    private final PostRepository postRepo;

    private final RabbitTemplate rabbitTemplate;

    @Override
    public CommentDTO addNewComment(int postId, Integer parentId, String content, String commenterEmail) {
        Post post = postRepo.findOne(isPublished(postId))
                .orElseThrow(() -> new NotFoundError(Post.class));

        Account commenter = accRepo.getByEmail(commenterEmail);
        Comment newComment = new Comment(commenter, post, content);

        if (parentId != null) {
            Comment parentComment = commentRepo.findById(parentId)
                    .orElseThrow(() -> new NotFoundError(Comment.class));

            if (parentComment.getParentComment() != null || parentComment.getPost().getId() != postId) {
                throw new NotFoundError(Comment.class);
            }
            newComment.setParentComment(parentComment);
        }

        newComment = commentRepo.save(newComment);
        return CommentDTO.toCommentDTO(newComment);
    }

    @Override
    public CommentDTO modifyComment(int commentId, String content, String accEmail) {
        return commentRepo.findById(commentId)
                .map(comment -> {
                    if (!comment.getAccount().getEmail().equals(accEmail)) throw new NotFoundError(Comment.class);

                    comment.setContent(content);
                    return commentRepo.saveAndFlush(comment);
                })
                .map(CommentDTO::toCommentDTO)
                .orElseThrow(() -> new NotFoundError(Comment.class));
    }

    @Override
    public int deleteComment(int commentId, String accEmail) {
        return commentRepo.findById(commentId)
                .map(comment -> {
                    if (!comment.getAccount().getEmail().equals(accEmail)) throw new NotFoundError(Comment.class);

                    commentRepo.delete(comment);
                    return commentRepo.countCommentInPost(comment.getPost().getId());
                })
                .orElseThrow(() -> new NotFoundError(Comment.class));
    }

    @Override
    public List<CommentDTO> getAllCommentOfPost(int postId, Pageable pageable) {
        return postRepo.findOne(isPublished(postId))
                .map(post -> commentRepo.getCommentsByPost(post, pageable)
                        .map(comment -> {
                            CommentDTO cmtDTO = CommentDTO.toCommentDTO(comment);
                            if (!comment.getChildComments().isEmpty()) {
                                cmtDTO.setChildComments(comment.getChildComments().stream()
                                        .map(CommentDTO::toCommentDTO)
                                        .toList());
                            }
                            return cmtDTO;
                        })
                        .getContent())
                .orElseThrow(() -> new NotFoundError(Post.class));
    }

    @Override
    public List<CommentDTO> getAllCommentOfPost(String slug, Pageable pageable) {
        return postRepo.findOne(hasSlug(slug))
                .filter(Post::isPublished)
                .map(post -> commentRepo.getCommentsByPost(post, pageable))
                .orElseThrow(() -> new NotFoundError(Post.class))
                .map(CommentDTO::toCommentDTO)
                .getContent();
    }

    @Override
    public int likeOrUnlikeComment(int commentId, String currentEmail) {
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new NotFoundError(Comment.class));

        Account currentAcc = SecurityUtils.authenticatedUser();
        boolean isLiked = currentAcc.getLikedComments().removeIf(cm -> cm.getId() == commentId);

        if (!isLiked) {
            currentAcc.getLikedComments().add(comment);

            var notificationRequest = new NotificationRequest(
                    currentAcc.getId(),
                    comment.getPost().getAuthor().getId(),
                    comment.getPost().getId(),
                    NotificationType.LIKE_COMMENT
            );
            rabbitTemplate.convertAndSend(RabbitMqConfig.QK_NOTIFICATION_FACTORY_QUEUE, notificationRequest);
        }

        accRepo.save(currentAcc);
        return commentRepo.countCommentLike(commentId);
    }
}