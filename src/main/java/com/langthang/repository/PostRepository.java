package com.langthang.repository;

import com.langthang.model.entity.Category;
import com.langthang.model.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.stream.Stream;

public interface PostRepository extends JpaRepository<Post, Integer>, FullTextSearch<Post> {

    Post findPostBySlug(String slug);

    Post findPostByIdAndPublished(int id, boolean isPublished);

    Post findPostBySlugAndPublished(String slug, boolean isPublished);

    @Query("select count(p) from Post p where p.account.id=?1")
    int countByAccount_Id(int accountId);

    Page<Post> findByAccountNotNullAndPublishedIsTrue(Pageable pageable);

    Page<Post> findByAccount_IdAndPublishedIsTrue(int accountId, Pageable pageable);

    Page<Post> getAllByAccount_EmailAndPublishedIsTrue(String accountEmail, Pageable pageable);

    Page<Post> getAllByAccount_EmailAndPublishedIsFalse(String accountEmail, Pageable pageable);

    @Query("select p from Post p join p.postCategories pc" +
           " where pc=?1 and p.published = true and p.account.id is not null ")
    Page<Post> findPostByCategories(Category category, Pageable pageable);

    @Query("""
            select p from Post p, (select bp.post.id as pid, count (bp.post.id) as total
             from Post p2 join BookmarkedPost bp on p2.id = bp.post.id
             group by (bp.post.id)) as subSelect
             where p.id = subSelect.pid and p.published = true
             order by subSelect.total desc
            """)
    Page<Post> getListOfPopularPostByBookmarkCount(Pageable pageable);

    @Query("""
            select p from Post p, (select cmt.post.id as pid, count(cmt.post.id) as total
             from Post p2 join Comment cmt on p2.id = cmt.post.id
             group by (cmt.post.id)) as subSelect
             where p.id = subSelect.pid and p.published = true
             order by subSelect.total desc
            """)
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

    Stream<Post> findAllByPublishedIsTrue();
}