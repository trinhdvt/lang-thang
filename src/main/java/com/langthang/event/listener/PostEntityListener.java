package com.langthang.event.listener;


import com.langthang.model.entity.Post;
import com.langthang.utils.MyStringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Date;

@Slf4j
@Component
public class PostEntityListener {

    @PreUpdate
    @PrePersist
    private void onAnyPostUpdate(Post post) {
        String slug = MyStringUtils.createSlug(post.getTitle()) + "-" + RandomStringUtils.randomAlphanumeric(5);
        String encodedTitle = MyStringUtils.escapeHtml(post.getTitle());
        String encodedContent = MyStringUtils.escapeHtml(post.getContent());
        String encodedThumbnail = MyStringUtils.escapeHtml(post.getPostThumbnail());

        if (post.getCreatedDate() == null) {
            post.setCreatedDate(new Date());
        }

        if (post.isPublished() && post.getPublishedDate() == null) {
            post.setPublishedDate(new Date());
        }

        post.setTitle(encodedTitle);
        post.setContent(encodedContent);
        post.setSlug(slug);
        post.setPostThumbnail(encodedThumbnail);
    }
}