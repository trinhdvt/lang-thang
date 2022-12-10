package com.langthang.model.entity;

import com.langthang.event.listener.PostEntityListener;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;


    private String content;

    private String slug;

    private Date publishedDate;

    private Date createdDate;

    private String postThumbnail;

    @Column(name = "status")
    private boolean published;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id")
    private Account account;

    @OneToMany(mappedBy = "post"
            , fetch = FetchType.LAZY)
    private Set<PostReport> postReports;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "post_category",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> postCategories;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private Set<BookmarkedPost> bookmarkedPosts;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
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