package com.langthang.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostReportDTO {

    private int reportId;

    /**
     * Who reported
     */
    private AccountDTO reporter;

    /**
     * Who are the owner of reported post
     */
    private AccountDTO postOwner;

    /**
     * Reported post's identity
     */
    private int reportPostId;

    /**
     * Why they report this post
     */
    private String reportContent;

    /**
     * When they report this post
     */
    private Date reportDate;

    /**
     * Is Admin solved this report
     */
    private boolean solved;

    /**
     * If solved then what is the decision have made
     */
    private String decision;
}
