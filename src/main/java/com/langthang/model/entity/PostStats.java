package com.langthang.model.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "post_stats")
public class PostStats {
    @Id
    @Column(name = "post_id")
    private Integer postId;

    @OneToOne
    @PrimaryKeyJoinColumn
    private Post post;

    private int viewCount = 0;
    private int bookmarkCount = 0;
    private int commentCount = 0;

    @Override
    public String toString() {
        return "PostStats{" +
                "postId=" + postId +
                ", viewCount=" + viewCount +
                ", bookmarkCount=" + bookmarkCount +
                ", commentCount=" + commentCount +
                '}';
    }
}
