package com.langthang.services;

import com.langthang.dto.PostReportDTO;
import com.langthang.dto.SystemReportDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IAdminServices {
    SystemReportDTO getBasicSystemReport();

    void updateUserToAdmin(int userId);

    List<PostReportDTO> getPostReport(Pageable pageable);

    PostReportDTO getPostReportById(int reportId);

    void createReport(String reportAccount, int postId, String reportContent);

    PostReportDTO solveReport(int reportId, String decision);
}
