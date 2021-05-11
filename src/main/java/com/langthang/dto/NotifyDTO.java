package com.langthang.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@ToString
public class NotifyDTO {
    @NotNull
    private int accountId;

    @NotNull
    private String content;

    private Date notifyDate;

    private int postId;
}
