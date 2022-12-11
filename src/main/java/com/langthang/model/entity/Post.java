package com.langthang.model.entity;

import com.langthang.event.listener.PostEntityListener;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "post")
@EntityListeners(PostEntityListener.class)
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "title", columnDefinition = "TEXT")
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "slug", unique = true)
    private String slug;

    @Column(name = "published_date")
    private Instant publishedDate;

    @Column(name = "created_date")
    @CreatedDate
    private Instant createdDate;

    @Column(name = "post_thumbnail")
    private String postThumbnail;

    @Column(name = "status")
    private boolean published;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @OneToMany(mappedBy = "post")
    private Set<PostReport> postReports;

    @ManyToMany
    @JoinTable(
            name = "post_category",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> postCategories;

    @OneToMany(mappedBy = "post")
    private Set<BookmarkedPost> bookmarkedPosts;

    @OneToMany(mappedBy = "post")
    @OrderBy("commentDate ASC")
    private List<Comment> comments;

    public Post(String title, String content, String postThumbnail) {
        this.title = title;
        this.content = content;
        this.postThumbnail = postThumbnail;
    }

    @Override
    public String toString() {
        return "Post{" +
               "id=" + id +
               ", title='" + title + '\'' +
               ", slug='" + slug + '\'' +
               ", publishedDate=" + publishedDate +
               ", postThumbnail='" + postThumbnail + '\'' +
               ", published=" + published +
               '}';
    }
}