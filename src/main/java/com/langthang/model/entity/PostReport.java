package com.langthang.model.entity;

import lombok.*;

import javax.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
