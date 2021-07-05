package com.langthang.services.impl;

import com.langthang.dto.AccountDTO;
import com.langthang.dto.PostRequestDTO;
import com.langthang.dto.PostResponseDTO;
import com.langthang.exception.CustomException;
import com.langthang.model.Account;
import com.langthang.model.Category;
import com.langthang.model.Post;
import com.langthang.repository.AccountRepository;
import com.langthang.repository.CategoryRepository;
import com.langthang.repository.PostRepository;
import com.langthang.services.IPostServices;
import com.langthang.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Service
@Transactional
public class PostServicesImpl implements IPostServices {

    enum SORT_TYPE {
        COMMENT, BOOKMARK
    }

    private final PostRepository postRepo;

    private final AccountRepository accRepo;

    private final CategoryRepository categoryRepo;

    private final NotificationServicesImpl notificationServices;

    @Override
    public PostResponseDTO addNewPostOrDraft(PostRequestDTO postRequestDTO, String authorEmail, boolean isPublish) {
        Post post = new Post(postRequestDTO.getTitle(), postRequestDTO.getContent(), postRequestDTO.getPostThumbnail());
        post.setAccount(accRepo.findAccountByEmail(authorEmail));
        post.setPublished(isPublish);
        post.setPostCategories(getCategories(postRequestDTO));

        Post savedPost = postRepo.saveAndFlush(post);
        if (isPublish) {
            notificationServices.sendFollowersNotification(savedPost);
        }

        return new PostResponseDTO(savedPost.getId(), savedPost.getSlug());
    }

    @Override
    public PostResponseDTO getPostDetailById(int postId) {
        Post post = postRepo.findPostByIdAndPublished(postId, true);

        if (post == null) {
            throw new CustomException("Post with id: " + postId + " not found", HttpStatus.NOT_FOUND);
        }

        return entityToDTO(post);
    }

    @Override
    public PostResponseDTO getPostDetailBySlug(String slug) {
        Post post = postRepo.findPostBySlugAndPublished(slug, true);

        if (post == null) {
            throw new CustomException("Post " + slug + " not found", HttpStatus.NOT_FOUND);
        }

        return entityToDTO(post);
    }

    @Override
    public PostResponseDTO getDraftById(int postId, String authorEmail) {
        Post draft = verifyResourceOwner(postId, authorEmail);
        if (!draft.isPublished()) {
            throw new CustomException("Not found", HttpStatus.NOT_FOUND);
        }

        return PostResponseDTO.toPostResponseDTO(draft);
    }

    @Override
    public List<PostResponseDTO> getPreviewPost(Pageable pageable) {
        Page<Post> postResponse = postRepo.findByAccountNotNullAndPublishedIsTrue(pageable);

        return postResponse.map(this::entityToDTO).getContent();
    }

    @Override
    public List<PostResponseDTO> findPostByKeyword(String keyword, Pageable pageable) {
        List<Post> posts = postRepo.searchByKeyword(keyword, pageable);

        return posts.stream().map(this::entityToDTO).collect(Collectors.toList());
    }


    @Override
    public List<PostResponseDTO> getPopularPostByProperty(String propertyName, int size) {
        Page<Post> responseList;
        PageRequest pageRequest = PageRequest.of(0, size);


        try {
            switch (SORT_TYPE.valueOf(propertyName.toUpperCase())) {
                case BOOKMARK:
                    responseList = postRepo.getListOfPopularPostByBookmarkCount(pageRequest);
                    break;

                case COMMENT:
                    responseList = postRepo.getListOfPopularPostByCommentCount(pageRequest);
                    break;

                default:
                    return Collections.emptyList();
            }

            return responseList.map(this::entityToDTO).getContent();
        } catch (IllegalArgumentException e) {
            throw new CustomException("Sort by " + propertyName + " is not support!"
                    , HttpStatus.UNPROCESSABLE_ENTITY);
        }

    }

    @Override
    public List<PostResponseDTO> getAllPostOfUser(int accountId, Pageable pageable) {
        Account account = accRepo.findById(accountId).orElse(null);
        if (account == null) {
            throw new CustomException("Account with id: " + accountId + " not found", HttpStatus.NOT_FOUND);
        }

        Page<Post> allPostOfUser = postRepo.findByAccount_IdAndPublishedIsTrue(accountId, pageable);

        return allPostOfUser.map(this::entityToDTO).getContent();
    }

    @Override
    public List<PostResponseDTO> getAllPostOfUser(String accountEmail, Pageable pageable) {
        Account account = accRepo.findAccountByEmail(accountEmail);
        if (account == null) {
            throw new CustomException("Account with email: " + accountEmail + " not found", HttpStatus.NOT_FOUND);
        }

        Page<Post> allPostOfUser = postRepo.getAllByAccount_EmailAndPublishedIsTrue(accountEmail, pageable);

        return allPostOfUser.map(this::entityToDTO).getContent();
    }

    @Override
    public List<PostResponseDTO> getAllDraftOfUser(String accountEmail, Pageable pageable) {
        Page<Post> allDraftOfUser = postRepo.getAllByAccount_EmailAndPublishedIsFalse(accountEmail, pageable);

        return allDraftOfUser.map(this::entityToDTO).getContent();
    }

    @Override
    public List<PostResponseDTO> getBookmarkedPostOfUser(String accEmail, Pageable pageable) {
        Page<Post> responseList = postRepo.getBookmarkedPostByAccount_Email(accEmail, pageable);

        return responseList.map(p -> {
            PostResponseDTO dto = entityToDTO(p);
            dto.setBookmarked(true);
            return dto;
        }).getContent();
    }

    @Override
    public PostResponseDTO getPostOrDraftContent(String slug, String authorEmail) {
        Post post = postRepo.findPostBySlug(slug);

        verifyResourceOwner(post, authorEmail);

        return PostResponseDTO.toPostResponseDTO(post);
    }

    @Override
    public void deletePostById(int postId, String authorEmail) {
        Post post = postRepo.findPostById(postId);
        verifyResourceOwner(post, authorEmail);
        postRepo.delete(post);
    }

    @Override
    public String updatePostById(int postId, String authorEmail, PostRequestDTO requestDTO) {
        Post existingPost = verifyResourceOwner(postId, authorEmail);
        updatePostContent(existingPost, requestDTO);
        existingPost.setPublished(true);

        boolean isFirstTimePublished = existingPost.getPublishedDate() == null;
        Post updatedPost = postRepo.saveAndFlush(existingPost);

        if (isFirstTimePublished) {
            notificationServices.sendFollowersNotification(updatedPost);
        }

        return updatedPost.getSlug();
    }

    @Override
    public List<PostResponseDTO> getAllPostOfCategory(int categoryId, Pageable pageable) {
        Category category = categoryRepo.findById(categoryId).orElse(null);

        if (category == null) {
            throw new CustomException("Category with id: " + categoryId + " not found", HttpStatus.NOT_FOUND);
        }

        Page<Post> responseList = postRepo.findPostByCategories(category, pageable);

        return responseList.map(this::entityToDTO).getContent();
    }

    @Override
    public void updateDraftById(int postId, String authorEmail, PostRequestDTO requestDTO) {
        Post existingPost = verifyResourceOwner(postId, authorEmail);
        updatePostContent(existingPost, requestDTO);
        existingPost.setPublished(false);

        postRepo.saveAndFlush(existingPost);
    }

    @Override
    public void deleteReportedPost(int postId, String adminEmail) {
        Post post = postRepo.findPostById(postId);

        try {
            verifyResourceOwner(post, adminEmail);
            postRepo.delete(post);

        } catch (CustomException ex) {
            if (post != null && post.isPublished()) {
                boolean isReportPost = post.getPostReports().stream().anyMatch(rp -> !rp.isSolved());
                if (isReportPost) {
                    postRepo.delete(post);
                    return;
                }
            }
            throw new CustomException("Cannot delete this post", HttpStatus.UNAUTHORIZED);
        }
    }

    private PostResponseDTO entityToDTO(Post post) {
        Account author = post.getAccount();

        AccountDTO authorDTO = AccountDTO.toBasicAccount(author);
        authorDTO.setPostCount(postRepo.countByAccount_Id(author.getId()));
        authorDTO.setFollowCount(accRepo.countFollowing(author.getId()));

        PostResponseDTO postResponse = PostResponseDTO.toPostResponseDTO(post);
        postResponse.setAuthor(authorDTO);
        postResponse.setOwner(author.getEmail().equals(SecurityUtils.getLoggedInEmail()));

        return postResponse;
    }

    private Set<Category> getCategories(PostRequestDTO postDTO) {
        Set<Category> categories = new HashSet<>();

        String[] categoriesId = postDTO.getCategories();
        if (categoriesId == null || categoriesId.length == 0) {
            return categories;
        }

        try {
            for (String categoryId : categoriesId) {
                Category category = categoryRepo.findById(Integer.valueOf(categoryId)).orElse(null);
                if (category == null) {
                    throw new CustomException("Category with id: " + categoryId + " not found!",
                            HttpStatus.UNPROCESSABLE_ENTITY);
                } else {
                    categories.add(category);
                }
            }
        } catch (Exception e) {
            throw new CustomException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return categories;
    }

    public Post verifyResourceOwner(int postId, String authorEmail) {
        Post post = postRepo.findPostById(postId);
        verifyResourceOwner(post, authorEmail);
        return post;
    }

    private void verifyResourceOwner(Post post, String authorEmail) {
        if (post == null) {
            throw new CustomException("Not found!", HttpStatus.NOT_FOUND);
        }
        if (!post.getAccount().getEmail().equals(authorEmail)) {
            throw new CustomException("Access denied!", HttpStatus.UNAUTHORIZED);
        }
    }

    private void updatePostContent(Post existingPost, PostRequestDTO requestDTO) {
        existingPost.setTitle(requestDTO.getTitle());
        existingPost.setContent(requestDTO.getContent());
        existingPost.setPostThumbnail(requestDTO.getPostThumbnail());
        existingPost.setPostCategories(getCategories(requestDTO));
    }
}
