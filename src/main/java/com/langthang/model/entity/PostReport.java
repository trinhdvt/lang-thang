package com.langthang.model.entity;

import com.langthang.utils.MyStringUtils;
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

    @Column(name = "reported_date", updatable = false)
    @CreatedDate
    private Instant reportedDate;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_solved")
    private boolean isSolved;

    @Column(name = "decision", columnDefinition = "TEXT")
    private String decision;

    public PostReport(Account account, Post post, String content) {
        this.account = account;
        this.post = post;
        this.content = content;
        this.isSolved = false;
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