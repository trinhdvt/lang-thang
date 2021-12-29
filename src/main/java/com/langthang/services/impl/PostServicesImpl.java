package com.langthang.services.impl;

import com.langthang.model.dto.response.AccountDTO;
import com.langthang.model.dto.request.PostRequestDTO;
import com.langthang.model.dto.response.PostResponseDTO;
import com.langthang.exception.HttpError;
import com.langthang.exception.NotFoundError;
import com.langthang.exception.UnauthorizedError;
import com.langthang.model.entity.Account;
import com.langthang.model.entity.Category;
import com.langthang.model.entity.Post;
import com.langthang.repository.AccountRepository;
import com.langthang.repository.CategoryRepository;
import com.langthang.repository.PostRepository;
import com.langthang.services.IPostServices;
import com.langthang.utils.AssertUtils;
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
        AssertUtils.notNull(post, new NotFoundError("Post not found!"));

        return entityToDTO(post);
    }

    @Override
    public PostResponseDTO getPostDetailBySlug(String slug) {
        Post post = postRepo.findPostBySlugAndPublished(slug, true);
        AssertUtils.notNull(post, new NotFoundError("Post not found!"));

        return entityToDTO(post);
    }

    @Override
    public PostResponseDTO getDraftById(int postId, String authorEmail) {
        Post draft = verifyResourceOwner(postId, authorEmail);
        AssertUtils.isTrue(!draft.isPublished(), new NotFoundError("Draft not found!"));

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
            throw new HttpError("Sort by " + propertyName + " is not support!"
                    , HttpStatus.UNPROCESSABLE_ENTITY);
        }

    }

    @Override
    public List<PostResponseDTO> getAllPostOfUser(int accountId, Pageable pageable) {
        accRepo.findById(accountId).orElseThrow(() -> new NotFoundError("Account not found"));

        Page<Post> allPostOfUser = postRepo.findByAccount_IdAndPublishedIsTrue(accountId, pageable);

        return allPostOfUser.map(this::entityToDTO).getContent();
    }

    @Override
    public List<PostResponseDTO> getAllPostOfUser(String accountEmail, Pageable pageable) {
        Account account = accRepo.findAccountByEmail(accountEmail);
        AssertUtils.notNull(account, new NotFoundError("Account not found"));

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
        AssertUtils.notNull(category, new NotFoundError("Category not found"));

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

        } catch (HttpError ex) {
            if (post != null && post.isPublished()) {
                boolean isReportPost = post.getPostReports().stream().anyMatch(rp -> !rp.isSolved());
                if (isReportPost) {
                    postRepo.delete(post);
                    return;
                }
            }
            throw new UnauthorizedError("Permission denied");
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
        List<String> categoriesId = postDTO.getCategories();
        if (categoriesId == null || categoriesId.size() == 0) {
            return Collections.emptySet();
        }

        try {
            List<Integer> cateIdInt = categoriesId.stream().map(Integer::valueOf).collect(Collectors.toList());

            List<Category> categoryList = categoryRepo.findAllById(cateIdInt);
            return new HashSet<>(categoryList);

        } catch (NumberFormatException e) {
            throw new HttpError("Bad request", HttpStatus.BAD_REQUEST);
        }
    }

    public Post verifyResourceOwner(int postId, String authorEmail) {
        Post post = postRepo.findPostById(postId);
        verifyResourceOwner(post, authorEmail);
        return post;
    }

    private void verifyResourceOwner(Post post, String authorEmail) {
        AssertUtils.notNull(post, new NotFoundError("Post not found"));
        AssertUtils.isTrue(post.getAccount().getEmail().equals(authorEmail), new UnauthorizedError("Post not found"));
    }

    private void updatePostContent(Post existingPost, PostRequestDTO requestDTO) {
        existingPost.setTitle(requestDTO.getTitle());
        existingPost.setContent(requestDTO.getContent());
        existingPost.setPostThumbnail(requestDTO.getPostThumbnail());
        existingPost.setPostCategories(getCategories(requestDTO));
    }

    enum SORT_TYPE {
        COMMENT, BOOKMARK
    }
}