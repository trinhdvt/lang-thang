package com.langthang.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class CommentDTO {
    private int commentId;
    private BasicAccountDTO commenter;
    private boolean isMyComment;
    private Date commentDate;
    private String content;
    private boolean isLiked;
    private int likeCount;
}
