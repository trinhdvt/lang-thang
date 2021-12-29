package com.langthang.services;

import com.langthang.model.dto.request.PostReportDTO;
import com.langthang.model.dto.response.SystemReportDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IAdminServices {
    SystemReportDTO getBasicSystemReport();

    void assignRoleAdminToUser(int userId);

    List<PostReportDTO> getListOfPostReport(Pageable pageable);

    PostReportDTO getPostReportById(int reportId);

    PostReportDTO solveReport(int reportId, String decision);
}