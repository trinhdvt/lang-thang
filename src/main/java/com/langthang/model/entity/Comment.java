package com.langthang.model.entity;

import com.langthang.event.listener.CommentEntityListener;
import lombok.*;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "comment")
@EntityListeners(CommentEntityListener.class)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToMany(mappedBy = "likedComments", fetch = FetchType.EAGER)
    private Set<Account> likedAccounts;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @CreatedDate
    @Column(name = "comment_date", updatable = false)
    private Instant commentDate;

    @ManyToOne(targetEntity = Comment.class)
    @JoinColumn(name = "parent_id")
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment")
    @OrderBy("commentDate ASC")
    private List<Comment> childComments;


    public Comment(Account account, Post post, String content) {
        this.account = account;
        this.post = post;
        this.content = content;
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