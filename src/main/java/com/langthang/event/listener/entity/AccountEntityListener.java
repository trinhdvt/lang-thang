package com.langthang.event.listener.entity;


import com.langthang.model.entity.Account;
import com.langthang.utils.MyStringUtils;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.apache.commons.lang3.RandomStringUtils;

public class AccountEntityListener {

    private static final String DEFAULT_AVATAR_LINK = "https://cdn.trinhdvt.tech/avatar2.webp";

    @PrePersist
    @PreUpdate
    private void onAnyUpdate(Account acc) {
        if (acc.getAvatarLink() == null || acc.getAvatarLink().isEmpty()) {
            acc.setAvatarLink(DEFAULT_AVATAR_LINK);
        }

        if (acc.getSlug() == null) {
            var slug = MyStringUtils.createSlug(acc.getName() + "-" + RandomStringUtils.randomAlphanumeric(3));
            acc.setSlug(slug);
        }
    }
}
