package com.langthang.dto;

import com.langthang.model.Account;
import com.langthang.model.Comment;
import com.langthang.utils.Utils;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class CommentDTO {

    private int commentId;

    private AccountDTO commenter;

    private int postId;

    private boolean isMyComment;

    private Date commentDate;

    private String content;

    private boolean isLiked;

    private int likeCount;

    public static CommentDTO toCommentDTO(Comment comment) {
        Account commenter = comment.getAccount();
        AccountDTO commenterDTO = AccountDTO.toBasicAccount(commenter);

        return CommentDTO.builder()
                .commenter(commenterDTO)
                .postId(comment.getPost().getId())
                .commentId(comment.getId())
                .commentDate(comment.getCommentDate())
                .content(comment.getContent())
                .isMyComment(commenter.getEmail().equals(Utils.getCurrentAccEmail()))
                .likeCount(comment.getLikedAccounts().size())
                .isLiked(comment.getLikedAccounts().stream()
                        .anyMatch(a -> a.getEmail().equals(Utils.getCurrentAccEmail())))
                .build();
    }
}
