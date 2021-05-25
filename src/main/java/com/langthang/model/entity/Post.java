package com.langthang.model.entity;

import com.langthang.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    private String content;

    private String slug;

    private Date publishedDate;

    private String postThumbnail;

    private boolean status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @OneToMany(mappedBy = "post"
            , fetch = FetchType.LAZY)
    private Set<PostReport> postReports;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "post_tag",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> postTag;

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

    @PrePersist
    @PreUpdate
    public void encodeContentAndCreateSlug() {
        slug = Utils.createSlug(title) + "-" + System.currentTimeMillis();
        content = Utils.escapeHtml(content);
        title = Utils.escapeHtml(title);
        postThumbnail = Utils.escapeHtml(postThumbnail);
        publishedDate = new Date();
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", publishedDate=" + publishedDate +
                ", postThumbnail='" + postThumbnail + '\'' +
                '}';
    }
}
