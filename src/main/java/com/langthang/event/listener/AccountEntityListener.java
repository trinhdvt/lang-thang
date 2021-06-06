package com.langthang.event.listener;


import com.langthang.model.Account;
import com.langthang.utils.Utils;

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

        acc.setName(Utils.escapeHtml(acc.getName()));
        acc.setAbout(Utils.escapeHtml(acc.getAbout()));
        acc.setAvatarLink(Utils.escapeHtml(acc.getAvatarLink()));
        acc.setFbLink(Utils.escapeHtml(acc.getFbLink()));
        acc.setInstagramLink(Utils.escapeHtml(acc.getInstagramLink()));
    }
}
