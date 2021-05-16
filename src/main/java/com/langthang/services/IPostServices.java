package com.langthang.services;

import com.langthang.dto.PostRequestDTO;
import com.langthang.dto.PostResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IPostServices {
    PostResponseDTO addNewPostOrDraft(PostRequestDTO postRequestDTO, String authorEmail, boolean isDraft);

    PostResponseDTO getPostDetailById(int postId);

    PostResponseDTO getPostDetailBySlug(String slug);

    PostResponseDTO getDraftById(int postId);

    List<PostResponseDTO> getPreviewPost(Pageable pageable);

    List<PostResponseDTO> getPreviewPostByKeyword(String keyword, Pageable pageable);

    List<PostResponseDTO> getPopularPostByProperty(String propertyName, int size);

    List<PostResponseDTO> getAllPreviewPostOfUser(int accountId, Pageable pageable);

    List<PostResponseDTO> getBookmarkedPostOfUser(String accEmail, Pageable pageable);

    void checkResourceExistAnOwner(int postId, String ownerEmail);

    void deletePostById(int postId);

    void updatePostById(int postId, PostRequestDTO postRequestDTO);

    PostResponseDTO updateAndPublicDraft(PostRequestDTO postRequestDTO);

    List<PostResponseDTO> getAllPreviewPostOfCategory(int categoryId, Pageable pageable);
}
