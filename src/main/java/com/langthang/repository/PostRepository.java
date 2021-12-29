package com.langthang.repository;

import com.langthang.model.entity.Category;
import com.langthang.model.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Integer>, FullTextSearch<Post> {

    Post findPostBySlug(String slug);

    Post findPostByIdAndPublished(int id, boolean isPublished);

    Post findPostBySlugAndPublished(String slug, boolean isPublished);

    Post findPostById(int postId);

    @Query("select count(p) from Post p where p.account.id=?1")
    int countByAccount_Id(int accountId);

    Page<Post> findByAccountNotNullAndPublishedIsTrue(Pageable pageable);

    Page<Post> findByAccount_IdAndPublishedIsTrue(int accountId, Pageable pageable);

    Page<Post> getAllByAccount_EmailAndPublishedIsTrue(String accountEmail, Pageable pageable);

    Page<Post> getAllByAccount_EmailAndPublishedIsFalse(String accountEmail, Pageable pageable);

    @Query("select p from Post p join p.postCategories pc" +
            " where pc=?1 and p.published = true and p.account.id is not null ")
    Page<Post> findPostByCategories(Category category, Pageable pageable);

    @Query("select p " +
            "from Post p join BookmarkedPost bp on p.id = bp.post.id " +
            "where p.published = true and p.account.id is not null " +
            "group by bp.post.id " +
            "order by count(bp.post.id) desc ")
    Page<Post> getListOfPopularPostByBookmarkCount(Pageable pageable);

    @Query("select p " +
            "from Post p join Comment c on p.id = c.post.id " +
            "where p.published = true and p.account.id is not null " +
            "group by c.post.id " +
            "order by count(c.post.id) desc ")
    Page<Post> getListOfPopularPostByCommentCount(Pageable pageable);

    @Query("select p " +
            "from Post p join BookmarkedPost bp on p.id=bp.post.id " +
            "join Account a on a.id=bp.account.id " +
            "where a.email=?1 and p.published=true " +
            "order by bp.bookmarkedDate desc ")
    Page<Post> getBookmarkedPostByAccount_Email(String accountEmail, Pageable pageable);

    @Query("select count(bp) from BookmarkedPost bp where bp.post.id=?1")
    int countBookmarks(int postId);

    boolean existsByIdAndPublished(int postId, boolean isPublished);
}