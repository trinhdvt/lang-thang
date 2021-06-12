package com.langthang.services.impl;

import com.langthang.dto.AccountDTO;
import com.langthang.dto.AccountInfoDTO;
import com.langthang.exception.CustomException;
import com.langthang.model.Account;
import com.langthang.model.FollowingRelationship;
import com.langthang.model.Post;
import com.langthang.model.PostReport;
import com.langthang.repository.AccountRepository;
import com.langthang.repository.FollowRelationshipRepo;
import com.langthang.repository.PostReportRepository;
import com.langthang.repository.PostRepository;
import com.langthang.services.IUserServices;
import com.langthang.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Service
@Transactional
public class UserServicesImpl implements IUserServices {

    private final AccountRepository accRepo;

    private final FollowRelationshipRepo followRepo;

    private final PasswordEncoder passwordEncoder;

    private final PostRepository postRepo;

    private final PostReportRepository reportRepo;

    @Override
    public AccountDTO getDetailInformation(int accountId) {
        Account account = accRepo.findAccountByIdAndEnabled(accountId, true);

        if (account == null) {
            throw new CustomException("Account with id: " + accountId + " not found", HttpStatus.NOT_FOUND);
        }

        return toDetailAccountDTO(account);
    }

    @Override
    public AccountDTO getDetailInformation(String email) {
        Account account = accRepo.findAccountByEmailAndEnabled(email, true);

        if (account == null) {
            throw new CustomException("Account with email: " + email + " not found", HttpStatus.NOT_FOUND);
        }

        return toDetailAccountDTO(account);
    }

    @Override
    public int followOrUnfollow(String currentAccEmail, int accountId) {
        Account currentAcc = accRepo.findAccountByEmail(currentAccEmail);

        Account willFollowAcc = accRepo.findAccountByIdAndEnabled(accountId, true);

        if (willFollowAcc == null) {
            throw new CustomException("Not found", HttpStatus.NOT_FOUND);
        }

        int currentAccId = currentAcc.getId();
        int willFollowAccId = willFollowAcc.getId();
        boolean isFollowed = followRepo.existsByAccount_IdAndFollowingAccountId(currentAccId, willFollowAccId);

        if (isFollowed) {
            followRepo.deleteByAccount_IdAndFollowingAccountId(currentAccId, willFollowAccId);
        } else {
            FollowingRelationship newRelationship = new FollowingRelationship(currentAccId, willFollowAccId);
            followRepo.saveAndFlush(newRelationship);
        }

        return accRepo.countFollowing(willFollowAccId);
    }

    @Override
    public List<AccountDTO> getTopFollowUser(int num) {
        List<Account> accountList = accRepo.getTopFollowingAccount(PageRequest.of(0, num));

        return accountList.stream().map(this::toDetailAccountDTO).collect(Collectors.toList());
    }

    @Override
    public List<AccountDTO> getListOfUserInSystem(Pageable pageable) {
        Page<Account> accountList = accRepo.findAll(pageable);

        return accountList.map(this::toDetailAccountDTO).getContent();
    }

    @Override
    public AccountDTO updateBasicInfo(String currentEmail, AccountInfoDTO newInfo) {
        Account account = accRepo.findAccountByEmail(currentEmail);

        account.setName(newInfo.getName());
        account.setAbout(newInfo.getAbout());
        account.setFbLink(newInfo.getFbLink());
        account.setAvatarLink(newInfo.getAvatarLink());

        Account savedAccount = accRepo.saveAndFlush(account);

        return AccountDTO.toBasicAccount(savedAccount);
    }

    @Override
    public void checkEmailAndPassword(String currentEmail, String oldPassword) {
        Account account = accRepo.findAccountByEmail(currentEmail);

        String currentPassword = account.getPassword();
        if (!passwordEncoder.matches(oldPassword, currentPassword)) {
            throw new CustomException("Wrong old password", HttpStatus.UNPROCESSABLE_ENTITY);
        }

    }

    @Override
    public void updatePassword(String currentEmail, String newPassword) {
        Account account = accRepo.findAccountByEmail(currentEmail);

        account.setPassword(passwordEncoder.encode(newPassword));
        accRepo.saveAndFlush(account);
    }

    @Override
    public void createReport(String reporterEmail, int postId, String reportContent) {
        Post reportPost = postRepo.findPostByIdAndPublished(postId, true);
        if (reportPost == null) {
            throw new CustomException("Post with id: " + postId + " not found!", HttpStatus.NOT_FOUND);
        }

        if (reportPost.getAccount().getEmail().equals(reporterEmail)) {
            throw new CustomException("Cannot report your-self", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        Account reporter = accRepo.findAccountByEmail(reporterEmail);
        PostReport postReport = new PostReport(reporter, reportPost, reportContent);

        reportRepo.save(postReport);
    }

    private AccountDTO toDetailAccountDTO(Account account) {
        AccountDTO basicAccountDTO = AccountDTO.toBasicAccount(account);

        basicAccountDTO.setFollowCount(accRepo.countFollowing(account.getId()));
        basicAccountDTO.setPostCount(accRepo.countPublishedPost(account.getId()));
        basicAccountDTO.setBookmarkOnOwnPostCount(accRepo.countBookmarkOnMyPost(account.getId()));
        basicAccountDTO.setCommentOnOwnPostCount(accRepo.countCommentOnMyPost(account.getId()));

        String currentAccEmail = SecurityUtils.getUsername();
        if (currentAccEmail != null) {
            Account currentAcc = accRepo.findAccountByEmail(currentAccEmail);
            boolean isFollowed = followRepo.existsByAccount_IdAndFollowingAccountId(currentAcc.getId(), account.getId());
            basicAccountDTO.setFollowed(isFollowed);
        }

        return basicAccountDTO;
    }
}
