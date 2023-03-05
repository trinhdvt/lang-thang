package com.langthang.model.entity;

import com.langthang.event.listener.entity.AccountEntityListener;
import com.langthang.model.constraints.Role;
import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Type;

import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "account")
@EntityListeners(AccountEntityListener.class)
public class Account {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "password", nullable = false, length = 64)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", unique = true, length = 500)
    private String slug;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "role", nullable = false, columnDefinition = "user_role")
    @Type(PostgreSQLEnumType.class)
    private Role role = Role.ROLE_USER;

    @Builder.Default
    @Column(name = "enabled", nullable = false)
    private boolean enabled = false;

    @Column(name = "avatar_link", length = 500)
    private String avatarLink;

    @Column(name = "about", columnDefinition = "TEXT")
    private String about;

    @Column(name = "fb_link", length = 500)
    private String fbLink;

    @Column(name = "instagram_link", length = 500)
    private String instagramLink;

    @Column(name = "register_token", length = 100)
    private String registerToken;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    @OrderBy("publishedDate DESC")
    @LazyCollection(LazyCollectionOption.EXTRA)
    private Set<Post> posts;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.EXTRA)
    private Set<FollowingRelationship> following;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    @OrderBy("notifyDate DESC ")
    private List<Notification> notifications;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    @OrderBy("reportedDate DESC")
    private List<PostReport> postReports;

    @OneToMany(mappedBy = "account")
    private Set<BookmarkedPost> bookmarkedPosts;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private Set<Comment> comments;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "comment_like",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "comment_id")
    )
    private Set<Comment> likedComments;

    @Override
    public String toString() {
        return "Account{" +
               "id=" + id +
               ", email='" + email + '\'' +
               ", password='" + password + '\'' +
               ", status='" + enabled + '\'' +
               ", name='" + name + '\'' +
               ", role=" + role +
               ", avatarLink='" + avatarLink + '\'' +
               ", about='" + about + '\'' +
               '}';
    }
}