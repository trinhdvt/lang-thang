package com.langthang.model.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
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

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Gender gender = Gender.UNKNOWN;

    private boolean status;

    private String avatarLink;

    private Date dateOfBirth;

    private String about;

    private String occupation;

    private Date lastLogin;

    private Integer loginCount;

    @OneToOne(mappedBy = "account"
            , cascade = CascadeType.ALL
            , fetch = FetchType.LAZY)
    private ExternalAccount externalAccount;

    @OneToMany(mappedBy = "account"
            , fetch = FetchType.EAGER
            , cascade = CascadeType.ALL)
    private Set<Post> posts;

    @OneToMany(mappedBy = "account"
            , fetch = FetchType.LAZY
            , cascade = CascadeType.ALL)
    private Set<FollowingRelationship> following;

    @OneToMany(mappedBy = "account"
            , fetch = FetchType.EAGER
            , cascade = CascadeType.ALL)
    private Set<Notify> notifies;

    @OneToMany(mappedBy = "account"
            , fetch = FetchType.LAZY)
    private Set<PostReport> postReports;

    @OneToMany(mappedBy = "account"
            , fetch = FetchType.EAGER)
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

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", status='" + status + '\'' +
                ", name='" + name + '\'' +
                ", role=" + role +
                ", gender=" + gender +
                ", avatarLink='" + avatarLink + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", about='" + about + '\'' +
                ", occupation='" + occupation + '\'' +
                ", lastLogin=" + lastLogin +
                ", loginCount=" + loginCount +
                '}';
    }
}
