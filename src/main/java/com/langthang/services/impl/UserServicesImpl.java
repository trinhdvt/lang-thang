package com.langthang.services.impl;

import com.langthang.exception.HttpError;
import com.langthang.exception.NotFoundError;
import com.langthang.mapper.UserMapper;
import com.langthang.model.dto.request.AccountInfoDTO;
import com.langthang.model.dto.response.AccountDTO;
import com.langthang.model.dto.v2.response.UserDtoV2;
import com.langthang.model.entity.Account;
import com.langthang.model.entity.FollowingRelationship;
import com.langthang.model.entity.Post;
import com.langthang.model.entity.PostReport;
import com.langthang.repository.AccountRepository;
import com.langthang.repository.FollowRelationshipRepo;
import com.langthang.repository.PostReportRepository;
import com.langthang.repository.PostRepository;
import com.langthang.services.IUserServices;
import com.langthang.specification.AccountSpec;
import com.langthang.specification.PostSpec;
import com.langthang.utils.AssertUtils;
import com.langthang.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.langthang.specification.AccountSpec.*;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Service
@Transactional
public class UserServicesImpl implements IUserServices {

    private final AccountRepository accRepo;

    private final FollowRelationshipRepo followRepo;

    private final PasswordEncoder passwordEncoder;

    private final PostRepository postRepo;

    private final PostReportRepository reportRepo;

    private final UserMapper userMapper;

    @Override
    public AccountDTO getDetailInformationById(int accountId) {
        return this.toDetailAccountDTO(findEnabledUser(accountId, "", ""));
    }

    @Override
    public AccountDTO getDetailInformationByEmail(String email) {
        return this.toDetailAccountDTO(findEnabledUser(-1, email, ""));
    }

    private Account findEnabledUser(@NonNull Integer id,
                                    @NonNull String email,
                                    @NonNull String slug) {
        return accRepo.findOne(hasId(id).or(hasEmail(email)).or(hasSlug(slug)))
                .filter(Account::isEnabled)
                .orElseThrow(() -> new NotFoundError(Account.class));
    }

    @Override
    public AccountDTO getDetailInformationBySlug(String slug) {
        return this.toDetailAccountDTO(findEnabledUser(-1, "", slug));
    }

    @Override
    public UserDtoV2 getMyProfile(Integer userId) {
        return userMapper.toDto(findEnabledUser(userId, "", ""));
    }

    @Override
    public int followOrUnfollow(String currentAccEmail, int accountId) {
        Account currentAcc = accRepo.getByEmail(currentAccEmail);

        Account willFollowAcc = accRepo.findById(accountId).filter(Account::isEnabled)
                .orElseThrow(() -> new NotFoundError(Account.class));

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
        return accRepo.getTopFollowingAccount(PageRequest.of(0, num))
                .map(this::toDetailAccountDTO)
                .getContent();
    }

    @Override
    public List<AccountDTO> getListOfUserInSystem(Pageable pageable) {
        return accRepo.findAll(pageable)
                .map(this::toDetailAccountDTO)
                .getContent();
    }

    @Override
    public AccountDTO updateBasicInfo(String currentEmail, AccountInfoDTO newInfo) {
        Account account = accRepo.getByEmail(currentEmail);

        account.setName(newInfo.getName());
        account.setAbout(newInfo.getAbout());
        account.setFbLink(newInfo.getFbLink());
        account.setAvatarLink(newInfo.getAvatarLink());

        Account savedAccount = accRepo.saveAndFlush(account);

        return AccountDTO.toBasicAccount(savedAccount);
    }

    @Override
    public void checkEmailAndPassword(String currentEmail, String oldPassword) {
        Account account = accRepo.getByEmail(currentEmail);

        String currentPassword = account.getPassword();
        AssertUtils.isTrue(passwordEncoder.matches(oldPassword, currentPassword),
                new HttpError("Wrong old password", HttpStatus.UNPROCESSABLE_ENTITY));
    }

    @Override
    public void updatePassword(String currentEmail, String newPassword) {
        Account account = accRepo.getByEmail(currentEmail);

        account.setPassword(passwordEncoder.encode(newPassword));
        accRepo.saveAndFlush(account);
    }

    @Override
    public void createReport(String reporterEmail, int postId, String reportContent) {
        Post reportPost = postRepo.findOne(PostSpec.isPublished(postId))
                .orElseThrow(() -> NotFoundError.build(Post.class));

        if (reportPost.getAuthor().getEmail().equals(reporterEmail)) {
            throw new HttpError("Cannot report your-self", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        Account reporter = accRepo.getByEmail(reporterEmail);
        PostReport postReport = new PostReport(reporter, reportPost, reportContent);

        reportRepo.save(postReport);
    }

    @Override
    public List<AccountDTO> getFollower(int accountId, Pageable pageable) {
        return accRepo.findOne(AccountSpec.isEnabled(accountId))
                .map(account -> accRepo.getFollowedAccount(account.getId(), pageable))
                .map(listOfFollowers -> listOfFollowers.stream().map(account -> {
                    AccountDTO dto = AccountDTO.toBasicAccount(account);
                    String currentAccEmail = SecurityUtils.getLoggedInEmail();
                    if (currentAccEmail != null) {
                        Account currentAcc = accRepo.getByEmail(currentAccEmail);
                        boolean isFollowed = followRepo.existsByAccount_IdAndFollowingAccountId(currentAcc.getId(), account.getId());
                        dto.setFollowed(isFollowed);
                    }
                    return dto;
                }).toList())
                .orElseThrow(() -> new NotFoundError(Account.class));
    }

    private AccountDTO toDetailAccountDTO(Account account) {
        AccountDTO basicAccountDTO = AccountDTO.toBasicAccount(account);

        basicAccountDTO.setFollowCount(accRepo.countFollowing(account.getId()));
        basicAccountDTO.setPostCount(accRepo.countPublishedPost(account.getId()));
        basicAccountDTO.setBookmarkOnOwnPostCount(accRepo.countBookmarkOnMyPost(account.getId()));
        basicAccountDTO.setCommentOnOwnPostCount(accRepo.countCommentOnMyPost(account.getId()));

        String currentAccEmail = SecurityUtils.getLoggedInEmail();
        if (currentAccEmail != null) {
            Account currentAcc = accRepo.getByEmail(currentAccEmail);
            boolean isFollowed = followRepo.existsByAccount_IdAndFollowingAccountId(currentAcc.getId(), account.getId());
            basicAccountDTO.setFollowed(isFollowed);
        }

        return basicAccountDTO;
    }
}