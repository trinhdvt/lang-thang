package com.langthang.model.entity;

import com.langthang.event.listener.CommentEntityListener;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToMany(mappedBy = "likedComments", fetch = FetchType.EAGER)
    private Set<Account> likedAccounts;

    private String content;

    private Date commentDate;

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
        this.commentDate = new Date();
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