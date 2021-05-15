package com.langthang.services.impl;

import com.langthang.dto.AccountDTO;
import com.langthang.dto.CommentDTO;
import com.langthang.dto.NotificationDTO;
import com.langthang.exception.CustomException;
import com.langthang.model.entity.Account;
import com.langthang.model.entity.Comment;
import com.langthang.model.entity.Post;
import com.langthang.repository.AccountRepository;
import com.langthang.repository.CommentRepository;
import com.langthang.repository.PostRepository;
import com.langthang.services.ICommentServices;
import com.langthang.services.INotificationServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentServicesImpl implements ICommentServices {

    @Autowired
    private CommentRepository commentRepo;

    @Autowired
    private AccountRepository accRepo;

    @Autowired
    private PostRepository postRepo;

    @Autowired
    private INotificationServices notificationServices;

    @Override
    public CommentDTO addNewComment(int postId, String content, String commenterEmail) {
        Post post = postRepo.findPostByIdAndStatus(postId, true);

        if (post == null) {
            throw new CustomException("Post with id: " + postId + " not found!", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        Account commenter = accRepo.findAccountByEmail(commenterEmail);
        Comment comment = new Comment(commenter, post, content);

        notificationServices.createNotification(commenter, post.getAccount(), post, NotificationDTO.TYPE.COMMENT);

        Comment savedComment = commentRepo.save(comment);

        return toCommentDTO(savedComment, commenterEmail);
    }

    @Override
    public CommentDTO modifyComment(int commentId, String content, String accEmail) {
        Comment oldComment = commentRepo.findById(commentId).orElse(null);

        if (oldComment == null) {
            throw new CustomException("Comment not found", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        Account commenter = oldComment.getAccount();
        if (!commenter.getEmail().equals(accEmail)) {
            throw new CustomException("Forbidden", HttpStatus.FORBIDDEN);
        }

        oldComment.setContent(content);
        Comment newComment = commentRepo.save(oldComment);

        return toCommentDTO(newComment, accEmail);
    }

    @Override
    public int deleteComment(int commentId, String accEmail) {
        Comment existingComment = commentRepo.findById(commentId).orElse(null);

        if (existingComment == null) {
            throw new CustomException("Comment not existed", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if (!existingComment.getAccount().getEmail().equals(accEmail)) {
            throw new CustomException("Forbidden", HttpStatus.FORBIDDEN);
        }

        commentRepo.delete(existingComment);
        return commentRepo.countCommentInPost(existingComment.getPost().getId());
    }

    @Override
    public List<CommentDTO> getAllCommentOfPost(int postId, String accEmail) {
        Post post = postRepo.findPostByIdAndStatus(postId, true);

        if (post == null) {
            throw new CustomException("Not found", HttpStatus.NOT_FOUND);
        }

        return post.getComments().stream()
                .map(c -> toCommentDTO(c, accEmail))
                .collect(Collectors.toList());

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
                    NotificationDTO.TYPE.LIKE);
        }

        accRepo.saveAndFlush(currentAcc);

        return commentRepo.countCommentLike(commentId);
    }

    private CommentDTO toCommentDTO(Comment savedComment, String currentEmail) {
        Account commenter = savedComment.getAccount();

        AccountDTO commenterDTO = AccountDTO.toBasicAccount(commenter);

        return CommentDTO.builder()
                .commenter(commenterDTO)
                .postId(savedComment.getPost().getId())
                .commentId(savedComment.getId())
                .commentDate(savedComment.getCommentDate())
                .content(savedComment.getContent())
                .isMyComment(commenter.getEmail().equals(currentEmail))
                .likeCount(savedComment.getLikedAccounts().size())
                .isLiked(savedComment.getLikedAccounts().stream().anyMatch(a -> a.getEmail().equals(currentEmail)))
                .build();
    }
}
