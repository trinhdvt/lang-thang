package com.langthang.services;

import com.langthang.dto.PostReportDTO;
import com.langthang.dto.SystemReportDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IAdminServices {
    SystemReportDTO getBasicSystemReport();

    void assignRoleAdminToUser(int userId);

    List<PostReportDTO> getListOfPostReport(Pageable pageable);

    PostReportDTO getPostReportById(int reportId);

    PostReportDTO solveReport(int reportId, String decision);
}
