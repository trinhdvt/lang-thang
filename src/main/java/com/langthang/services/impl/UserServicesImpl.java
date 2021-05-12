package com.langthang.services.impl;

import com.langthang.dto.BasicAccountDTO;
import com.langthang.exception.CustomException;
import com.langthang.model.entity.Account;
import com.langthang.model.entity.FollowingRelationship;
import com.langthang.repository.AccountRepository;
import com.langthang.repository.FollowRelationshipRepo;
import com.langthang.services.IUserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServicesImpl implements IUserServices {

    @Autowired
    private AccountRepository accRepo;

    @Autowired
    private FollowRelationshipRepo followRepo;

    @Override
    public BasicAccountDTO getDetailInformation(int accountId) {
        Account account = accRepo.findAccountByIdAndEnabled(accountId, true);

        if (account == null) {
            throw new CustomException("Account with id: " + accountId + " not found", HttpStatus.NOT_FOUND);
        }

        return toDetailAccountDTO(account);
    }

    @Override
    public BasicAccountDTO getDetailInformation(String email) {
        Account account = accRepo.findAccountByEmailAndEnabled(email, true);

        if (account == null) {
            throw new CustomException("Account with email: " + email + " not found", HttpStatus.NOT_FOUND);
        }

        return toDetailAccountDTO(account);
    }

    @Override
    public int followOrUnfollow(String currentAccEmail, int accountId) {
        Account currentAcc = accRepo.findByEmail(currentAccEmail);

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

    private BasicAccountDTO toDetailAccountDTO(Account account) {
        return BasicAccountDTO.builder()
                .accountId(account.getId())
                .name(account.getName())
                .email(account.getEmail())
                .avatarLink(account.getAvatarLink())
                .fbLink(account.getFbLink())
                .instagramLink(account.getInstagramLink())
                .about(account.getAbout())
                .occupation(account.getOccupation())
                .followCount(accRepo.countFollowing(account.getId()))
                .postCount(account.getPosts().size())
                .bookmarkOnOwnPostCount(accRepo.countBookmarkOnMyPost(account.getId()))
                .commentOnOwnPostCount(accRepo.countCommentOnMyPost(account.getId()))
                .build();
    }
}
