package com.langthang.model.entity;

import com.langthang.utils.Utils;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "notify")
public class Notify {
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

    public Notify(Account account, Post post, Account sourceAccount, String content) {
        this.account = account;
        this.post = post;
        this.sourceAccount = sourceAccount;
        this.content = content;
        this.notifyDate = new Date();
        this.seen = false;
    }

    @PreUpdate
    @PrePersist
    public void escapeHtml() {
        this.content = Utils.escapeHtml(content);
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
