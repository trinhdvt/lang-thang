package com.langthang.model.entity;

import com.langthang.event.listener.entity.NotificationEntityListener;
import com.langthang.model.constraints.NotificationType;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.springframework.lang.Nullable;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "source_account_id")
    private Account sourceAccount;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "notify_date")
    @CreationTimestamp
    private Instant notifyDate;

    @Column(name = "is_seen")
    private boolean seen;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    @Type(PostgreSQLEnumType.class)
    private NotificationType type;

    public Notification(Account account, Post post, Account sourceAccount, String content, @Nullable NotificationType type) {
        this.account = account;
        this.post = post;
        this.sourceAccount = sourceAccount;
        this.content = content;
        this.seen = false;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Notify{" +
               "id=" + id +
               ", type=" + type +
               '}';
    }
}