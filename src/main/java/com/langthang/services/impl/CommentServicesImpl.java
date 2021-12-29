package com.langthang.services.impl;

import com.langthang.dto.CommentDTO;
import com.langthang.exception.NotFoundError;
import com.langthang.exception.UnauthorizedError;
import com.langthang.model.Account;
import com.langthang.model.Comment;
import com.langthang.model.Post;
import com.langthang.repository.AccountRepository;
import com.langthang.repository.CommentRepository;
import com.langthang.repository.PostRepository;
import com.langthang.services.ICommentServices;
import com.langthang.services.INotificationServices;
import com.langthang.utils.AssertUtils;
import com.langthang.utils.constraints.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Service
@Transactional
@Slf4j
public class CommentServicesImpl implements ICommentServices {

    private final CommentRepository commentRepo;

    private final AccountRepository accRepo;

    private final PostRepository postRepo;

    private final INotificationServices notificationServices;

    @Override
    public CommentDTO addNewComment(int postId, Integer parentId, String content, String commenterEmail) {
        Post post = postRepo.findPostByIdAndPublished(postId, true);
        AssertUtils.notNull(post, new NotFoundError("Post not found!"));

        Account commenter = accRepo.findAccountByEmail(commenterEmail);
        Comment newComment = new Comment(commenter, post, content);

        if (parentId != null) {
            Comment parentComment = commentRepo.findById(parentId).orElseThrow(() -> new NotFoundError("Comment not found!"));
            if (parentComment.getParentComment() != null || parentComment.getPost().getId() != postId) {
                throw new NotFoundError("Comment not found!");
            }
            newComment.setParentComment(parentComment);
        }

        newComment = commentRepo.save(newComment);
        return CommentDTO.toCommentDTO(newComment);
    }

    @Override
    public CommentDTO modifyComment(int commentId, String content, String accEmail) {
        Comment oldComment = commentRepo.findById(commentId).orElse(null);
        AssertUtils.notNull(oldComment, new NotFoundError("Comment not found!"));

        Account commenter = oldComment.getAccount();
        AssertUtils.isTrue(commenter.getEmail().equals(accEmail), new UnauthorizedError("Comment not found"));

        oldComment.setContent(content);
        Comment newComment = commentRepo.save(oldComment);

        return CommentDTO.toCommentDTO(newComment);
    }

    @Override
    public int deleteComment(int commentId, String accEmail) {
        Comment existingComment = commentRepo.findById(commentId).orElse(null);
        AssertUtils.notNull(existingComment, new NotFoundError("Comment not found!"));
        AssertUtils.isTrue(existingComment.getAccount().getEmail().equals(accEmail), new UnauthorizedError("Comment not found"));

        commentRepo.delete(existingComment);
        return commentRepo.countCommentInPost(existingComment.getPost().getId());
    }

    @Override
    public List<CommentDTO> getAllCommentOfPost(int postId, Pageable pageable) {
        AssertUtils.isTrue(postRepo.existsByIdAndPublished(postId, true), new NotFoundError("Post not found!"));

        return commentRepo.getCommentsByPost_Id(postId, pageable)
                .map(comment -> {
                    CommentDTO cmtDTO = CommentDTO.toCommentDTO(comment);
                    if (!comment.getChildComments().isEmpty()) {
                        cmtDTO.setChildComments(comment.getChildComments().stream()
                                .map(CommentDTO::toCommentDTO)
                                .collect(Collectors.toList()));
                    }
                    return cmtDTO;
                })
                .getContent();
    }

    @Override
    public List<CommentDTO> getAllCommentOfPost(String slug, Pageable pageable) {
        Post post = postRepo.findPostBySlugAndPublished(slug, true);
        AssertUtils.notNull(post, new NotFoundError("Post not found!"));

        return commentRepo.getCommentsByPost_Id(post.getId(), pageable)
                .map(CommentDTO::toCommentDTO)
                .getContent();
    }

    @Override
    public int likeOrUnlikeComment(int commentId, String currentEmail) {
        Comment comment = commentRepo.findById(commentId).orElse(null);
        AssertUtils.notNull(comment, new NotFoundError("Comment not found!"));

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