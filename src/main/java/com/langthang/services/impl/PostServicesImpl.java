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
import com.langthang.utils.Utils;
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

@Service
@Transactional
public class PostServicesImpl implements IPostServices {

    enum SORT_TYPE {
        COMMENT, BOOKMARK
    }

    @Autowired
    private PostRepository postRepo;

    @Autowired
    private AccountRepository accRepo;

    @Autowired
    private CategoryRepository categoryRepo;

    @Override
    public PostResponseDTO addNewPostOrDraft(PostRequestDTO postRequestDTO, String authorEmail, boolean isDraft) {
        Post post = new Post(postRequestDTO.getTitle(), postRequestDTO.getContent(), postRequestDTO.getPostThumbnail());
        post.setAccount(accRepo.findAccountByEmail(authorEmail));
        post.setStatus(!isDraft);

        post.setPostCategories(getCategories(postRequestDTO));

        Post savedPost = postRepo.saveAndFlush(post);
        return PostResponseDTO.builder()
                .postId(savedPost.getId())
                .slug(savedPost.getSlug())
                .build();
    }

    @Override
    public PostResponseDTO updateAndPublicDraft(PostRequestDTO postRequestDTO) {
        Post existingPost = postRepo.findPostById(postRequestDTO.getPostId());
        if (existingPost == null) {
            throw new CustomException("Draft with id: " + postRequestDTO.getPostId() + "not found",
                    HttpStatus.NOT_FOUND);
        }

        existingPost.setTitle(postRequestDTO.getTitle());
        existingPost.setContent(postRequestDTO.getContent());
        existingPost.setPostThumbnail(postRequestDTO.getPostThumbnail());
        existingPost.setStatus(true);

        existingPost.setPostCategories(getCategories(postRequestDTO));

        Post savedPost = postRepo.saveAndFlush(existingPost);
        return PostResponseDTO.builder()
                .postId(savedPost.getId())
                .slug(savedPost.getSlug())
                .build();
    }

    @Override
    public PostResponseDTO getPostDetailById(int postId) {
        Post post = postRepo.findPostByIdAndStatus(postId, true);

        if (post == null) {
            throw new CustomException("Post with id: " + postId + " not found", HttpStatus.NOT_FOUND);
        }

        return entityToDTO(post);
    }

    @Override
    public PostResponseDTO getPostDetailBySlug(String slug) {
        Post post = postRepo.findPostBySlugAndStatus(slug, true);

        if (post == null) {
            throw new CustomException("Post " + slug + " not found", HttpStatus.NOT_FOUND);
        }

        return entityToDTO(post);
    }

    @Override
    public PostResponseDTO getDraftById(int postId) {
        Post post = postRepo.findPostByIdAndStatus(postId, false);

        if (post == null) {
            throw new CustomException("Draft with id " + postId + " not found", HttpStatus.NOT_FOUND);
        }

        return PostResponseDTO.builder()
                .postId(postId)
                .title(post.getTitle())
                .content(post.getContent())
                .postThumbnail(post.getPostThumbnail())
                .build();
    }

    @Override
    public List<PostResponseDTO> getPreviewPost(Pageable pageable) {
        Page<Post> postResponse = postRepo.findByAccountNotNullAndStatusIsTrue(pageable);

        return postResponse.map(this::entityToDTO).getContent();
    }

    @Override
    public List<PostResponseDTO> findPostByKeyword(String keyword, Pageable pageable) {
        Page<Post> postResp = postRepo.findPostByKeyword(keyword, pageable);

        return postResp.map(this::entityToDTO).getContent();
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

        Page<Post> allPostOfUser = postRepo.findByAccount_IdAndStatusIsTrue(accountId, pageable);

        return allPostOfUser.map(this::entityToDTO).getContent();
    }

    @Override
    public List<PostResponseDTO> getAllPostOfUser(String accountEmail, Pageable pageable) {
        Account account = accRepo.findAccountByEmail(accountEmail);
        if (account == null) {
            throw new CustomException("Account with email: " + accountEmail + " not found", HttpStatus.NOT_FOUND);
        }

        Page<Post> allPostOfUser = postRepo.getAllByAccount_EmailAndStatusIsTrue(accountEmail, pageable);

        return allPostOfUser.map(this::entityToDTO).getContent();
    }

    @Override
    public List<PostResponseDTO> getAllDraftOfUser(String accountEmail, Pageable pageable) {
        Page<Post> allDraftOfUser = postRepo.getAllByAccount_EmailAndStatusIsFalse(accountEmail,pageable);

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
    public void checkResourceExistAnOwner(int postId, String ownerEmail) {

        if (!postRepo.existsById(postId)) {
            throw new CustomException("Post with id: " + postId + " not found", HttpStatus.NOT_FOUND);
        }

        if (!postRepo.existsByIdAndAccount_Email(postId, ownerEmail)) {
            throw new CustomException("Access Denied", HttpStatus.FORBIDDEN);
        }

    }

    @Override
    public void deletePostById(int postId) {
        postRepo.deleteById(postId);
    }

    @Override
    public void updatePostById(int postId, PostRequestDTO postRequestDTO) {
        Post oldPost = postRepo.findPostById(postId);
        oldPost.setTitle(postRequestDTO.getTitle());
        oldPost.setContent(postRequestDTO.getContent());
        oldPost.setPostThumbnail(postRequestDTO.getPostThumbnail());

        oldPost.setPostCategories(getCategories(postRequestDTO));

        postRepo.save(oldPost);
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


    private PostResponseDTO entityToDTO(Post post) {
        Account author = post.getAccount();

        AccountDTO authorDTO = AccountDTO.toBasicAccount(author);
        authorDTO.setPostCount(postRepo.countByAccount_Id(author.getId()));
        authorDTO.setFollowCount(accRepo.countFollowing(author.getId()));

        PostResponseDTO postResponse = PostResponseDTO.toPostResponseDTO(post);
        postResponse.setAuthor(authorDTO);
        postResponse.setOwner(author.getEmail().equals(Utils.getCurrentAccEmail()));

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
}
