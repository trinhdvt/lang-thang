package com.langthang.model.entity;

import com.langthang.dto.PostResponseDTO;
import com.langthang.utils.Utils;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = "postToPostDTO",
                classes = {@ConstructorResult(
                        targetClass = PostResponseDTO.class,
                        columns = {
                                @ColumnResult(name = "id", type = Integer.class),
                                @ColumnResult(name = "title", type = String.class),
                                @ColumnResult(name = "slug", type = String.class),
                                @ColumnResult(name = "published_date", type = Date.class),
                                @ColumnResult(name = "post_thumbnail", type = String.class),
                        }
                )}
        ),
        @SqlResultSetMapping(name = "postToPostDTO.count", columns = @ColumnResult(name = "cnt"))
})
@NamedNativeQueries({
        @NamedNativeQuery(
                name = "Post.getPreviewPostByKeyword",
                query = "select id,title,slug,published_date,post_thumbnail from post where match(title, content) against(?1)",
                resultSetMapping = "postToPostDTO"
        ),
        @NamedNativeQuery(
                name = "Post.getPreviewPostByKeyword.count",
                query = "select count(id) as cnt from post where match(title, content) against(?1)",
                resultSetMapping = "postToPostDTO.count"
        )
})

@Entity
@Table(name = "post")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String content;
    private Date publishedDate;
    private Date lastModified;
    private String postThumbnail;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id")
    private Account account;

    @OneToOne(mappedBy = "post"
            , fetch = FetchType.EAGER
            , cascade = CascadeType.ALL)
    private PostStats postStats;

    @OneToMany(mappedBy = "post"
            , fetch = FetchType.LAZY)
    private Set<PostReport> postReports;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "post_tag",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> postTag;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "post_category",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Category> postCategories;

    @OneToMany(mappedBy = "post")
    private Set<BookmarkedPost> bookmarkedPosts;

    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER)
    private Set<Comment> comments;

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", publishedDate=" + publishedDate +
                ", lastModified=" + lastModified +
                ", postThumbnail='" + postThumbnail + '\'' +
                ", postStats=" + postStats +
                '}';
    }
}
