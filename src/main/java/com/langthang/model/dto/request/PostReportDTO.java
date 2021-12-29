package com.langthang.model.dto.request;

import com.langthang.model.dto.response.PostResponseDTO;
import com.langthang.model.dto.response.AccountDTO;
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