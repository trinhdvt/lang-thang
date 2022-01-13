package com.langthang.event.listener;


import com.langthang.model.entity.Account;
import com.langthang.utils.MyStringUtils;
import org.apache.commons.lang3.RandomStringUtils;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

public class AccountEntityListener {

    private static final String defaultAvatarLink = "https://cdn.langthang.tech/avatar2.png";

    @PrePersist
    @PreUpdate
    private void onAnyUpdate(Account acc) {
        if (acc.getAvatarLink() == null || acc.getAvatarLink().isEmpty()) {
            acc.setAvatarLink(defaultAvatarLink);
        }

        acc.setName(MyStringUtils.escapeHtml(acc.getName()));
        acc.setSlug(MyStringUtils.createSlug(acc.getName() + "-" + RandomStringUtils.randomAlphanumeric(3)));
        acc.setAbout(MyStringUtils.escapeHtml(acc.getAbout()));
        acc.setAvatarLink(MyStringUtils.escapeHtml(acc.getAvatarLink()));
        acc.setFbLink(MyStringUtils.escapeHtml(acc.getFbLink()));
        acc.setInstagramLink(MyStringUtils.escapeHtml(acc.getInstagramLink()));
    }
}