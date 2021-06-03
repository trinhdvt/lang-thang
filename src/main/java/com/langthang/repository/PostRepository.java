package com.langthang.repository;

import com.langthang.model.Category;
import com.langthang.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface PostRepository extends JpaRepository<Post, Integer> {

    Post findPostByIdAndStatus(int id, boolean status);

    Post findPostBySlugAndStatus(String slug, boolean status);

    Post findPostById(int postId);

    @Query("select count(p) from Post p where p.account.id=?1")
    int countByAccount_Id(int accountId);

    Page<Post> findByAccountNotNullAndStatusIsTrue(Pageable pageable);

    Page<Post> findByAccount_IdAndStatusIsTrue(int accountId, Pageable pageable);

    Page<Post> getAllByAccount_EmailAndStatusIsTrue(String accountEmail, Pageable pageable);

    Page<Post> getAllByAccount_EmailAndStatusIsFalse(String accountEmail, Pageable pageable);

    @Query("select p from Post p join p.postCategories pc" +
            " where pc=?1 and p.status = true and p.account.id is not null ")
    Page<Post> findPostByCategories(Category category, Pageable pageable);

    @Query("select p " +
            "from Post p join BookmarkedPost bp on p.id = bp.post.id " +
            "where p.status = true and p.account.id is not null " +
            "group by bp.post.id " +
            "order by count(bp.post.id) desc ")
    Page<Post> getListOfPopularPostByBookmarkCount(Pageable pageable);

    @Query("select p " +
            "from Post p join Comment c on p.id = c.post.id " +
            "where p.status = true and p.account.id is not null " +
            "group by c.post.id " +
            "order by count(c.post.id) desc ")
    Page<Post> getListOfPopularPostByCommentCount(Pageable pageable);

    @Query("select p " +
            "from Post p join BookmarkedPost bp on p.id=bp.post.id " +
            "join Account a on a.id=bp.account.id " +
            "where a.email=?1 and p.status=true " +
            "order by bp.bookmarkedDate desc ")
    Page<Post> getBookmarkedPostByAccount_Email(String accountEmail, Pageable pageable);


    @Query(value = "select id, title, content, published_date, post_thumbnail, slug, status, account_id " +
            "from post where match(title, content) against(?1 in boolean mode) " +
            "order by match(title, content) against(?1 in boolean mode) DESC",
            countQuery = "select count(id) " +
                    "from post where match(title, content) against(?1 in boolean mode) ",
            nativeQuery = true)
    Page<Post> findPostByKeyword(String keyword, Pageable pageable);

    @Query("select count(bp) from BookmarkedPost bp where bp.post.id=?1")
    int countBookmarks(int postId);

    boolean existsByIdAndAccount_Email(int postId, String email);

    boolean existsById(int postId);

    boolean existsByIdAndStatus(int postId, boolean status);

    @Transactional
    @Modifying
    @Query("update Post p set p.status=false where p.id=?1")
    void deleteById(int postId);
}
