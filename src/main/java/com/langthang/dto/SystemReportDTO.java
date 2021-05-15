package com.langthang.dto;

import lombok.Value;

@Value
public class SystemReportDTO {

    /**
     * How many User registered in our website ?
     */
    long userCount;


    /**
     * How many post were published ?
     */
    long postCount;


    /**
     * How many post were reported by other User ?
     */
    long reportedPostCount;
}
