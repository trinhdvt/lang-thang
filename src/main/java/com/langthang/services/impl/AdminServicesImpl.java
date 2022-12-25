package com.langthang.services.impl;

import com.langthang.exception.HttpError;
import com.langthang.exception.NotFoundError;
import com.langthang.model.constraints.Role;
import com.langthang.model.dto.request.PostReportDTO;
import com.langthang.model.dto.response.AccountDTO;
import com.langthang.model.dto.response.PostResponseDTO;
import com.langthang.model.dto.response.SystemReportDTO;
import com.langthang.model.entity.Account;
import com.langthang.model.entity.Post;
import com.langthang.model.entity.PostReport;
import com.langthang.repository.AccountRepository;
import com.langthang.repository.PostReportRepository;
import com.langthang.repository.PostRepository;
import com.langthang.services.IAdminServices;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Service
@Transactional
public class AdminServicesImpl implements IAdminServices {

    private final AccountRepository accRepo;

    private final PostRepository postRepo;

    private final PostReportRepository reportRepo;

    @Override
    public SystemReportDTO getBasicSystemReport() {
        long numberOfAccount = accRepo.count();

        long numberOfPost = postRepo.count();

        long numberOfReportedPost = reportRepo.count();

        return new SystemReportDTO(numberOfAccount, numberOfPost, numberOfReportedPost);
    }

    @Override
    public void assignRoleAdminToUser(int userId) {
        accRepo.findById(userId)
                .filter(Account::isEnabled)
                .ifPresentOrElse(acc -> {
                    acc.setRole(Role.ROLE_ADMIN);
                    accRepo.save(acc);
                }, () -> {
                    throw new HttpError("No enabled account with id: " + userId,
                            HttpStatus.UNPROCESSABLE_ENTITY);
                });
    }

    @Override
    public List<PostReportDTO> getListOfPostReport(Pageable pageable) {
        return reportRepo.findAll(pageable)
                .map(this::toBasicPostReportDTO)
                .getContent();
    }

    @Override
    public PostReportDTO getPostReportById(int reportId) {
        return reportRepo.findById(reportId)
                .map(this::toPostReportDetailDTO)
                .orElseThrow(() -> new NotFoundError(PostReport.class));
    }

    @Override
    public PostReportDTO solveReport(int reportId, String decision) {
        return reportRepo.findById(reportId)
                .map(report -> {
                    if (report.isSolved()) throw new HttpError("Already solved", HttpStatus.NOT_ACCEPTABLE);

                    report.setDecision(decision);
                    report.setSolved(true);
                    return reportRepo.saveAndFlush(report);
                }).map(this::toBasicPostReportDTO)
                .orElseThrow(() -> new NotFoundError(PostReport.class));
    }

    private PostReportDTO toBasicPostReportDTO(PostReport postReport) {
        return PostReportDTO.builder()
                .reportId(postReport.getId())
                .reportDate(postReport.getReportedDate())
                .solved(postReport.isSolved())
                .reportContent(postReport.getContent())
                .decision(postReport.getDecision())
                .build();
    }

    private PostReportDTO toPostReportDetailDTO(PostReport postReport) {
        Account reporter = postReport.getAccount();
        AccountDTO reporterDTO = AccountDTO.toBasicAccount(reporter);

        Post reportedPost = postReport.getPost();
        PostResponseDTO reportedPostDTO = null;
        if (reportedPost != null) {
            reportedPostDTO = new PostResponseDTO(reportedPost.getId(), reportedPost.getSlug());
        }

        PostReportDTO postReportDTO = toBasicPostReportDTO(postReport);
        postReportDTO.setReporter(reporterDTO);
        postReportDTO.setReportedPost(reportedPostDTO);

        return postReportDTO;
    }
}