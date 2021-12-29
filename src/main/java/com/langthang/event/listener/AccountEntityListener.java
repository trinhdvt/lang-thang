package com.langthang.event.listener;


import com.langthang.model.entity.Account;
import com.langthang.utils.MyStringUtils;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

public class AccountEntityListener {

    private static final String defaultAvatarLink = "https://langthang-user-photos.s3-ap-southeast-1.amazonaws.com/avatar2.png";

    @PrePersist
    @PreUpdate
    private void onAnyUpdate(Account acc) {
        if (acc.getAvatarLink() == null || acc.getAvatarLink().isEmpty()) {
            acc.setAvatarLink(defaultAvatarLink);
        }

        acc.setName(MyStringUtils.escapeHtml(acc.getName()));
        acc.setAbout(MyStringUtils.escapeHtml(acc.getAbout()));
        acc.setAvatarLink(MyStringUtils.escapeHtml(acc.getAvatarLink()));
        acc.setFbLink(MyStringUtils.escapeHtml(acc.getFbLink()));
        acc.setInstagramLink(MyStringUtils.escapeHtml(acc.getInstagramLink()));
    }
}