package com.langthang.model.dto.response;

import lombok.Data;

@Data
public class SystemReportDTO {

    /**
     * How many User registered in our website ?
     */
    private long userCount;

    /**
     * How many post were published ?
     */
    private long postCount;

    /**
     * How many post were reported by other User ?
     */
    private long reportedPostCount;

    public SystemReportDTO(long userCount, long postCount, long reportedPostCount) {
        this.userCount = userCount;
        this.postCount = postCount;
        this.reportedPostCount = reportedPostCount;
    }
}