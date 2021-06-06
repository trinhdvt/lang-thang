package com.langthang.event.listener;


import com.langthang.model.Post;
import com.langthang.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Date;

@Slf4j
@Component
public class PostEntityListener {

    private static JobLauncher jobLauncher;

    private static Job sendFollowerNotificationJob;

    @Autowired
    public void init(JobLauncher asyncJobLauncher, Job sendFollowerNotificationJob) {
        PostEntityListener.jobLauncher = asyncJobLauncher;
        PostEntityListener.sendFollowerNotificationJob = sendFollowerNotificationJob;
    }

    @PreUpdate
    @PrePersist
    private void onAnyPostUpdate(Post post) {
        String slug = Utils.createSlug(post.getTitle()) + "-" + System.currentTimeMillis();
        String encodedTitle = Utils.escapeHtml(post.getTitle());
        String encodedContent = Utils.escapeHtml(post.getContent());
        String encodedThumbnail = Utils.escapeHtml(post.getPostThumbnail());

        if (post.getPublishedDate() == null) {
            post.setPublishedDate(new Date());
        }
        post.setTitle(encodedTitle);
        post.setContent(encodedContent);
        post.setSlug(slug);
        post.setPostThumbnail(encodedThumbnail);
    }

    @PostPersist
    private void onNewPostCreated(Post newPost) {
        if (!newPost.isPublished())
            return;

        new Thread(() -> {
            try {
                JobParameters jobParameters = new JobParametersBuilder()
                        .addLong("authorId", (long) newPost.getAccount().getId())
                        .addLong("postId", (long) newPost.getId())
                        .addDate("run-time", new Date())
                        .toJobParameters();

                jobLauncher.run(sendFollowerNotificationJob, jobParameters);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
