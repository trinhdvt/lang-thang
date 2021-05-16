package com.langthang.model.entity;

import com.langthang.utils.Utils;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToMany(mappedBy = "likedComments", fetch = FetchType.EAGER)
    private Set<Account> likedAccounts = new HashSet<>();

    private String content;

    private Date commentDate;

    public Comment(Account account, Post post, String content) {
        this.account = account;
        this.post = post;
        this.content = content;
        this.commentDate = new Date();
    }

    @PrePersist
    @PreUpdate
    @PostUpdate
    void encodeContent() {
        this.content = Utils.escapeHtml(content);
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", commentDate=" + commentDate +
                '}';
    }
}
