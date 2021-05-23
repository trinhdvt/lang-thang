package com.langthang.model.entity;

import com.langthang.utils.Utils;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "account")
public class Account {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String email;

    private String password;

    private String name;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.ROLE_MEMBER;

    @Builder.Default
    private boolean enabled = false;

    private String avatarLink;

    private String about;

    private String occupation;

    private String fbLink;

    private String instagramLink;

    @OneToMany(mappedBy = "account"
            , fetch = FetchType.LAZY
            , cascade = CascadeType.ALL)
    @OrderBy("publishedDate DESC")
    private List<Post> posts;

    @OneToMany(mappedBy = "account"
            , fetch = FetchType.LAZY
            , cascade = CascadeType.ALL)
    private Set<FollowingRelationship> following;

    @OneToMany(mappedBy = "account"
            , fetch = FetchType.LAZY
            , cascade = CascadeType.ALL)
    @OrderBy("notifyDate DESC ")
    private List<Notify> notifies;

    @OneToMany(mappedBy = "account"
            , fetch = FetchType.LAZY)
    @OrderBy("reportedDate DESC")
    private List<PostReport> postReports;

    @OneToMany(mappedBy = "account"
            , fetch = FetchType.LAZY)
    private Set<BookmarkedPost> bookmarkedPosts;

    @OneToMany(mappedBy = "account"
            , fetch = FetchType.LAZY
            , cascade = CascadeType.ALL)
    private Set<Comment> comments;

    @ManyToMany(fetch = FetchType.LAZY
            , cascade = CascadeType.ALL)
    @JoinTable(
            name = "comment_like",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "comment_id")
    )
    private Set<Comment> likedComments;

    @PrePersist
    @PreUpdate
    public void setAvatarLink() {
        if (this.avatarLink == null || this.avatarLink.isEmpty()) {
            this.avatarLink = "https://langthang-user-photos.s3-ap-southeast-1.amazonaws.com/avatar2.png";
        }
        this.name = Utils.escapeHtml(name);
        this.about = Utils.escapeHtml(about);
        this.avatarLink = Utils.escapeHtml(avatarLink);
        this.fbLink = Utils.escapeHtml(fbLink);
        this.instagramLink = Utils.escapeHtml(instagramLink);
    }

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
