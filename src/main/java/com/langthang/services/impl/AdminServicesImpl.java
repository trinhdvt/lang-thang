package com.langthang.services.impl;

import com.langthang.dto.AccountDTO;
import com.langthang.dto.PostReportDTO;
import com.langthang.dto.SystemReportDTO;
import com.langthang.exception.CustomException;
import com.langthang.model.Account;
import com.langthang.model.Post;
import com.langthang.model.PostReport;
import com.langthang.model.Role;
import com.langthang.repository.AccountRepository;
import com.langthang.repository.PostReportRepository;
import com.langthang.repository.PostRepository;
import com.langthang.services.IAdminServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminServicesImpl implements IAdminServices {

    @Autowired
    private AccountRepository accRepo;

    @Autowired
    private PostRepository postRepo;

    @Autowired
    private PostReportRepository reportRepo;

    @Override
    public SystemReportDTO getBasicSystemReport() {
        long numberOfAccount = accRepo.count();

        long numberOfPost = postRepo.count();

        long numberOfReportedPost = reportRepo.count();

        return new SystemReportDTO(numberOfAccount, numberOfPost, numberOfReportedPost);
    }

    @Override
    public void updateUserToAdmin(int userId) {
        Account account = accRepo.findAccountByIdAndEnabled(userId, true);

        if (account == null) {
            throw new CustomException("No enabled account with id: " + userId,
                    HttpStatus.UNPROCESSABLE_ENTITY);
        }

        account.setRole(Role.ROLE_ADMIN);
        accRepo.saveAndFlush(account);
    }

    @Override
    public List<PostReportDTO> getPostReport(Pageable pageable) {
        Page<PostReport> reportList = reportRepo.findAll(pageable);

        return reportList.map(this::toBasicPostReportDTO).getContent();
    }

    @Override
    public PostReportDTO getPostReportById(int reportId) {
        PostReport postReport = reportRepo.findById(reportId).orElse(null);
        if (postReport == null) {
            throw new CustomException("Report with ID: " + reportId + " not found", HttpStatus.NOT_FOUND);
        }

        return toPostReportDetailDTO(postReport);
    }

    @Override
    public void createReport(String reporterEmail, int postId, String reportContent) {
        Post reportPost = postRepo.findPostByIdAndStatus(postId, true);
        if (reportPost == null) {
            throw new CustomException("Post with id: " + postId + " not found!", HttpStatus.NOT_FOUND);
        }

        Account reporter = accRepo.findAccountByEmail(reporterEmail);

        PostReport postReport = new PostReport(reporter, reportPost, reportContent);

        reportRepo.save(postReport);
    }

    @Override
    public PostReportDTO solveReport(int reportId, String decision) {
        PostReport report = reportRepo.findById(reportId).orElse(null);
        if (report == null) {
            throw new CustomException("Report with ID: " + reportId + " not found", HttpStatus.NOT_FOUND);
        }

        report.setDecision(decision);
        report.setSolved(true);

        PostReport savedReport = reportRepo.saveAndFlush(report);

        return toBasicPostReportDTO(savedReport);
    }

    private PostReportDTO toBasicPostReportDTO(PostReport postReport) {

        return PostReportDTO.builder()
                .reportId(postReport.getId())
                .reportDate(postReport.getReportedDate())
                .reportPostId(postReport.getPost().getId())
                .solved(postReport.isSolved())
                .reportContent(postReport.getContent())
                .decision(postReport.getDecision())
                .build();
    }

    private PostReportDTO toPostReportDetailDTO(PostReport postReport) {
        Account reportAccount = postReport.getAccount();

        AccountDTO reporter = AccountDTO.toBasicAccount(reportAccount);
        AccountDTO postOwner = AccountDTO.toBasicAccount(postReport.getPost().getAccount());

        PostReportDTO postReportDTO = toBasicPostReportDTO(postReport);
        postReportDTO.setReporter(reporter);
        postReportDTO.setPostOwner(postOwner);

        return postReportDTO;
    }
}
