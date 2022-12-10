package com.langthang.event.listener;


import com.langthang.model.entity.Account;
import com.langthang.utils.MyStringUtils;
import org.apache.commons.lang3.RandomStringUtils;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

public class AccountEntityListener {

    private static final String defaultAvatarLink = "https://cdn.trinhdvt.tech/avatar2.webp";

    @PrePersist
    @PreUpdate
    private void onAnyUpdate(Account acc) {
        if (acc.getAvatarLink() == null || acc.getAvatarLink().isEmpty()) {
            acc.setAvatarLink(defaultAvatarLink);
        }


        acc.setSlug(MyStringUtils.createSlug(acc.getName() + "-" + RandomStringUtils.randomAlphanumeric(3)));
    }
}