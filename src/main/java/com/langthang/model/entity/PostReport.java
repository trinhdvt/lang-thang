package com.langthang.model.entity;

import com.langthang.utils.MyStringUtils;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "post_report")
public class PostReport {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private Date reportedDate;

    private String content;

    private boolean isSolved;

    private String decision;

    public PostReport(Account account, Post post, String content) {
        this.account = account;
        this.post = post;
        this.content = content;
        this.isSolved = false;
        this.reportedDate = new Date();
    }

    @PrePersist
    @PreUpdate
    public void escapeHtml() {
        this.content = MyStringUtils.escapeHtml(content);
        this.decision = MyStringUtils.escapeHtml(decision);
    }

    @Override
    public String toString() {
        return "PostReport{" +
                "id=" + id +
                ", reportedDate=" + reportedDate +
                ", content='" + content + '\'' +
                ", isSolved=" + isSolved +
                ", decision='" + decision + '\'' +
                '}';
    }
}