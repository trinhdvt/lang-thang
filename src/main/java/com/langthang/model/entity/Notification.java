package com.langthang.model.entity;

import com.langthang.event.listener.NotificationEntityListener;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

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

    private String content;

    private Date notifyDate;

    @Column(name = "is_seen")
    private boolean seen;

    public Notification(Account account, Post post, Account sourceAccount, String content) {
        this.account = account;
        this.post = post;
        this.sourceAccount = sourceAccount;
        this.content = content;
        this.notifyDate = new Date();
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