package com.langthang.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class PostReportDTO {

    private int reportId;

    private AccountDTO reporter;

    private PostResponseDTO reportedPost;

    private String reportContent;

    private Date reportDate;

    private boolean solved;

    private String decision;
}
