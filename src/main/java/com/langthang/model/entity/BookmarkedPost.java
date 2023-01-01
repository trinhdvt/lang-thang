package com.langthang.model.entity;

import com.langthang.event.listener.BookmarkEntityListener;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@Entity
@Table(name = "bookmarked_post")
@EntityListeners(BookmarkEntityListener.class)
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

    @Column(name = "bookmarked_date")
    private Instant bookmarkedDate;

    public BookmarkedPost(Account account, Post post) {
        this.id = new BookmarkedPostKey(account.getId(), post.getId());
        this.account = account;
        this.post = post;
        this.bookmarkedDate = Instant.now();
    }
}