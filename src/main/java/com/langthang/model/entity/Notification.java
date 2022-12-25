package com.langthang.model.entity;

import com.langthang.event.listener.NotificationEntityListener;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "notification")
@EntityListeners(NotificationEntityListener.class)
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_account_id")
    private Account sourceAccount;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "notify_date")
    @CreatedDate
    private Instant notifyDate;

    @Column(name = "is_seen")
    private boolean seen;

    public Notification(Account account, Post post, Account sourceAccount, String content) {
        this.account = account;
        this.post = post;
        this.sourceAccount = sourceAccount;
        this.content = content;
        this.seen = false;
    }

    @Override
    public String toString() {
        return "Notify{" +
               "id=" + id +
               ", content='" + content + '\'' +
               ", notifyDate=" + notifyDate +
               ", isSeen=" + seen +
               '}';
    }
}