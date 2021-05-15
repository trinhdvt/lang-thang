package com.langthang.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Builder
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostReportDTO {

    int reportId;

    /**
     * Who reported
     */
    AccountDTO reporter;

    /**
     * Who are the owner of reported post
     */
    AccountDTO postOwner;

    /**
     * Reported post's identity
     */
    int reportPostId;

    /**
     * Why they report this post
     */
    String reportContent;

    /**
     * When they report this post
     */
    Date reportDate;

    /**
     * Is Admin solved this report
     */
    boolean solved;

    /**
     * If solved then what is the decision have made
     */
    String decision;
}
