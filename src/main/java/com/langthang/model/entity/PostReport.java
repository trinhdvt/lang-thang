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

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    private Date reportDate;
    private String content;
    private boolean isSolved;
    private String decision;

    @Override
    public String toString() {
        return "PostReport{" +
                "id=" + id +
                ", account_email=" + account.getEmail() +
                ", post_id=" + post.getId() +
                ", reportDate=" + reportDate +
                ", content='" + content + '\'' +
                ", isSolved=" + isSolved +
                ", decision='" + decision + '\'' +
                '}';
    }
}
