package com.langthang.repository;

import com.langthang.dto.PostResponseDTO;
import com.langthang.model.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.util.Set;

public interface PostRepository extends PagingAndSortingRepository<Post, Integer> {
    Set<Post> findAllByAccount_Email(String email);

    @Query("select p from Post p where p.account.id= ?1")
    Set<Post> findAllByAccount_Id(int id);

    Post findPostByIdAndStatus(int id, boolean status);

    Post findPostBySlugAndStatus(String slug, boolean status);

    Post findPostById(int postId);

    @Query("select count(p) from Post p where p.account.id=?1")
    int countByAccount_Id(int accountId);

    @Query("select new com.langthang.dto.PostResponseDTO(p.id,p.title,p.slug,p.publishedDate,p.postThumbnail) " +
            "from Post p where p.status=true")
    Page<PostResponseDTO> getPreviewPost(Pageable pageable);

    @Query("select new com.langthang.dto.PostResponseDTO(p.id,p.title,p.slug,p.publishedDate,p.postThumbnail) " +
            "from Post p where p.status=true and p.account.id=?1")
    Page<PostResponseDTO> getAllPreviewPostOfUser(int accountId, Pageable pageable);

    @Query("select new com.langthang.dto.PostResponseDTO(p.id,p.title,p.slug,p.publishedDate,p.postThumbnail) " +
            "from Post p join BookmarkedPost bp on p.id = bp.post.id " +
            "where p.status = true " +
            "group by bp.post.id " +
            "order by count(bp.post.id) desc ")
    Page<PostResponseDTO> getPopularPostByBookmarkCount(Pageable pageable);


    @Query("select new com.langthang.dto.PostResponseDTO(p.id,p.title,p.slug,p.publishedDate,p.postThumbnail) " +
            "from Post p join Comment c on p.id = c.post.id " +
            "where p.status = true " +
            "group by c.post.id " +
            "order by count(c.post.id) desc ")
    Page<PostResponseDTO> getPopularPostByCommentCount(Pageable pageable);

    @Query("select new com.langthang.dto.PostResponseDTO(p.id,p.title,p.slug,p.publishedDate,p.postThumbnail) " +
            "from Post p join BookmarkedPost bp on p.id=bp.post.id " +
            "join Account a on a.id=bp.account.id " +
            "where a.email=?1 and p.status=true " +
            "order by bp.bookmarkedDate desc ")
    Page<PostResponseDTO> getBookmarkedPostByAccount_Email(String accountEmail, Pageable pageable);

    /**
     * SQL query for this method is defined at {@link Post}
     *
     * @param keyword  Keyword in title or content of a post
     * @param pageable Size of return results
     * @return return all post that contains {@code keyword }in title or content
     */
    @Query(name = "Post.getPreviewPostByKeyword",
            countName = "Post.getPreviewPostByKeyword.count",
            nativeQuery = true)
    Page<PostResponseDTO> getPreviewPostByKeyword(String keyword, Pageable pageable);

    @Query("select count(bp) from BookmarkedPost bp where bp.post.id=?1")
    int countBookmarks(int postId);

    @Query("select count(c) from Comment c where c.post.id=?1")
    int countComments(int postId);

    boolean existsByIdAndAccount_Email(int postId, String email);

    boolean existsById(int postId);

    @Transactional
    @Modifying
    @Query("update Post p set p.status=false where p.id=?1")
    void deleteById(int postId);
}
