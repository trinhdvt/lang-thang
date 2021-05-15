package com.langthang.services.impl;

import com.langthang.dto.AccountDTO;
import com.langthang.dto.AccountInfoDTO;
import com.langthang.exception.CustomException;
import com.langthang.model.entity.Account;
import com.langthang.model.entity.FollowingRelationship;
import com.langthang.model.entity.Role;
import com.langthang.repository.AccountRepository;
import com.langthang.repository.FollowRelationshipRepo;
import com.langthang.services.IUserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServicesImpl implements IUserServices {

    @Autowired
    private AccountRepository accRepo;

    @Autowired
    private FollowRelationshipRepo followRepo;

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
        account.setOccupation(newInfo.getOccupation());

        Account savedAccount = accRepo.saveAndFlush(account);

        return AccountDTO.toBasicAccount(savedAccount);
    }

    private AccountDTO toDetailAccountDTO(Account account) {
        return AccountDTO.builder()
                .accountId(account.getId())
                .name(account.getName())
                .email(account.getEmail())
                .role(account.getRole() != Role.ROLE_ADMIN ? null : account.getRole())
                .avatarLink(account.getAvatarLink())
                .fbLink(account.getFbLink())
                .instagramLink(account.getInstagramLink())
                .about(account.getAbout())
                .occupation(account.getOccupation())
                .followCount(accRepo.countFollowing(account.getId()))
                .postCount(accRepo.countPublishedPost(account.getId()))
                .bookmarkOnOwnPostCount(accRepo.countBookmarkOnMyPost(account.getId()))
                .commentOnOwnPostCount(accRepo.countCommentOnMyPost(account.getId()))
                .build();
    }
}
