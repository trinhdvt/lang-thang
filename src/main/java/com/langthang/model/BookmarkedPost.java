package com.langthang.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@Table(name = "bookmarked_post")
public class BookmarkedPost {
    @EmbeddedId
    private BookmarkedPostKey id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("accountId")
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postId")
    @JoinColumn(name = "post_id")
    private Post post;

    private Date bookmarkedDate;

    public BookmarkedPost(Account account, Post post) {
        this.id = new BookmarkedPostKey(account.getId(), post.getId());
        this.account = account;
        this.post = post;
        this.bookmarkedDate = new Date();
    }
}

