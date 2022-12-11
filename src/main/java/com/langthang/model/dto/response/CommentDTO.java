package com.langthang.model.dto.response;

import com.langthang.model.entity.Account;
import com.langthang.model.entity.Comment;
import com.langthang.utils.SecurityUtils;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class CommentDTO {

    private Integer commentId;
    private Integer parentId;
    private AccountDTO commenter;
    private int postId;
    private boolean isMyComment;
    private Instant commentDate;
    private String content;
    private boolean isLiked;
    private int likeCount;
    private List<CommentDTO> childComments;

    public static CommentDTO toCommentDTO(Comment comment) {
        if (comment == null)
            return null;

        Account commenter = comment.getAccount();
        AccountDTO commenterDTO = AccountDTO.toBasicAccount(commenter);

        return CommentDTO.builder()
                .commenter(commenterDTO)
                .postId(comment.getPost().getId())
                .commentId(comment.getId())
                .parentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .commentDate(comment.getCommentDate())
                .content(comment.getContent())
                .isMyComment(commenter.getEmail().equals(SecurityUtils.getLoggedInEmail()))
                .likeCount(comment.getLikedAccounts().size())
                .isLiked(comment.getLikedAccounts().stream()
                        .anyMatch(a -> a.getEmail().equals(SecurityUtils.getLoggedInEmail())))
                .build();
    }
}