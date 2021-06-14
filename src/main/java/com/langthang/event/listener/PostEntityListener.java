package com.langthang.event.listener;


import com.langthang.model.Post;
import com.langthang.utils.Utils;
import lombok.extern.slf4j.Slf4j;
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
        String slug = Utils.createSlug(post.getTitle()) + "-" + System.currentTimeMillis();
        String encodedTitle = Utils.escapeHtml(post.getTitle());
        String encodedContent = Utils.escapeHtml(post.getContent());
        String encodedThumbnail = Utils.escapeHtml(post.getPostThumbnail());

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
