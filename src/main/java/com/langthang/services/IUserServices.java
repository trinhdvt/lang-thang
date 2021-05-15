package com.langthang.services;

import com.langthang.dto.AccountDTO;
import com.langthang.dto.AccountInfoDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUserServices {

    AccountDTO getDetailInformation(int accountId);

    AccountDTO getDetailInformation(String email);

    int followOrUnfollow(String currentAccEmail, int accountId);

    List<AccountDTO> getTopFollowUser(int num);

    List<AccountDTO> getListOfUserInSystem(Pageable pageable);

    AccountDTO updateBasicInfo(String currentEmail, AccountInfoDTO newInfo);
}
