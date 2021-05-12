package com.langthang.services;

import com.langthang.dto.BasicAccountDTO;

public interface IUserServices {

    BasicAccountDTO getDetailInformation(int accountId);

    BasicAccountDTO getDetailInformation(String email);

    int followOrUnfollow(String currentAccEmail, int accountId);
}
