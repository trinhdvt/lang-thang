package com.langthang.services;

import com.langthang.model.dto.request.AccountInfoDTO;
import com.langthang.model.dto.response.AccountDTO;
import com.langthang.model.dto.v2.response.UserDtoV2;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUserServices {

    AccountDTO getDetailInformationById(int accountId);

    AccountDTO getDetailInformationByEmail(String email);

    int followOrUnfollow(String currentAccEmail, int accountId);

    List<AccountDTO> getTopFollowUser(int num);

    List<AccountDTO> getListOfUserInSystem(Pageable pageable);

    AccountDTO updateBasicInfo(String currentEmail, AccountInfoDTO newInfo);

    void checkEmailAndPassword(String currentEmail, String oldPassword);

    void updatePassword(String currentEmail, String password);

    void createReport(String reportAccount, int postId, String reportContent);

    List<AccountDTO> getFollower(int accountId, Pageable pageable);

    AccountDTO getDetailInformationBySlug(String slug);

    UserDtoV2 getMyProfile(Integer userId);
}