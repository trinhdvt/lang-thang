package com.langthang.services;

import com.langthang.dto.PostRequestDTO;
import com.langthang.dto.PostResponseDTO;
import com.langthang.model.entity.Post;

import java.util.List;

public interface IPostServices {
    Post addNewPostOrDraft(PostRequestDTO postRequestDTO, String authorEmail, boolean isDraft);

    Post findPostById(int postId);

    boolean isPostNotFound(int postId);

    PostResponseDTO getPostDetailById(int postId);

    PostResponseDTO getPostDetailBySlug(String slug);

    PostResponseDTO getDraftById(int postId);

    List<PostResponseDTO> getPreviewPost(int page, int size);

    List<PostResponseDTO> getPreviewPost(int page, int size, String keyword);

    List<PostResponseDTO> getPopularPostByProperty(String propertyName, int size);

    List<PostResponseDTO> getAllPreviewPostOfUser(int accountId, int page, int size);

    List<PostResponseDTO> getBookmarkedPostOfUser(String accEmail, int page, int size);

    boolean checkResourceOwner(int postId, String ownerEmail);

    void deletePostById(int postId);

    Post updatePostById(int postId, PostRequestDTO postRequestDTO);

    Post updateAndPublicDraft(PostRequestDTO postRequestDTO, Integer postId);

}
