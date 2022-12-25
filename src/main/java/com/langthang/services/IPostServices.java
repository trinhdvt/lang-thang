package com.langthang.services;

import com.langthang.controller.v1.XmlUrlSet;
import com.langthang.model.dto.request.PostRequestDTO;
import com.langthang.model.dto.response.PostResponseDTO;
import com.langthang.model.dto.v2.response.PostDtoV2;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IPostServices {
    PostResponseDTO addNewPostOrDraft(PostRequestDTO postRequestDTO, String authorEmail, boolean isPublish);

    PostResponseDTO getPostDetailBySlug(String slug);

    PostResponseDTO getDraftById(int postId, String authorEmail);

    List<PostResponseDTO> getPreviewPost(Pageable pageable);

    List<PostDtoV2> findPostByKeyword(String keyword, Pageable pageable);

    List<PostResponseDTO> getAllPostOfCategory(int categoryId, Pageable pageable);

    List<PostResponseDTO> getAllPostOfCategory(String slug, Pageable pageable);

    List<PostResponseDTO> getPopularPostByProperty(String propertyName, Pageable pageable);

    List<PostResponseDTO> getAllPostOfUser(int accountId, Pageable pageable);

    List<PostResponseDTO> getAllPostOfUser(String accountEmail, Pageable pageable, boolean isPublished);

    List<PostResponseDTO> getBookmarkedPostOfUser(String accEmail, Pageable pageable);

    PostResponseDTO getPostOrDraftContent(String slug, String authorEmail);

    void deletePostById(int postId, String authorEmail);

    void deleteReportedPost(int postId, String adminEmail);

    String updatePostById(int postId, String authorEmail, PostRequestDTO postRequestDTO);

    void updateDraftById(int postId, String authorEmail, PostRequestDTO postRequestDTO);

    XmlUrlSet genSiteMap();
}