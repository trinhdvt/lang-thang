package com.langthang.controller;

import com.langthang.dto.PostReportDTO;
import com.langthang.services.IAdminServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Validated
@RestController
public class ReportController {

    @Autowired
    private IAdminServices adminServices;

    @GetMapping("/report")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Object> getListReport(
            @PageableDefault(sort = {"isSolved", "reportedDate"})
                    Pageable pageable) {

        List<PostReportDTO> listOfReports = adminServices.getPostReport(pageable);

        return ResponseEntity.ok(listOfReports);
    }

    @GetMapping("/report/{report_id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Object> getReportDetail(
            @PathVariable("report_id") int reportId) {

        PostReportDTO postReportDTO = adminServices.getPostReportById(reportId);

        return ResponseEntity.ok(postReportDTO);
    }

    @PostMapping("/report")
    @PreAuthorize("hasAuthority('ROLE_MEMBER')")
    public ResponseEntity<Object> createReport(
            @RequestParam("postId") int postId,
            @RequestParam("content") String reportContent,
            Authentication authentication) {

        String reportAccount = authentication.getName();

        adminServices.createReport(reportAccount, postId, reportContent);

        return ResponseEntity.accepted().build();
    }

    @PutMapping("/report/{report_id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Object> markAsSolved(
            @PathVariable("report_id") int reportId,
            @RequestParam("decision") @NotBlank String decision) {

        PostReportDTO postReportDTO = adminServices.solveReport(reportId, decision);

        return ResponseEntity.ok(postReportDTO);
    }
}
