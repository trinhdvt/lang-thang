package com.langthang.services.impl;

import com.langthang.controller.XmlUrlSet;
import com.langthang.exception.HttpError;
import com.langthang.exception.NotFoundError;
import com.langthang.exception.UnauthorizedError;
import com.langthang.model.dto.request.PostRequestDTO;
import com.langthang.model.dto.response.AccountDTO;
import com.langthang.model.dto.response.PostResponseDTO;
import com.langthang.model.entity.Account;
import com.langthang.model.entity.Category;
import com.langthang.model.entity.Post;
import com.langthang.repository.AccountRepository;
import com.langthang.repository.CategoryRepository;
import com.langthang.repository.PostRepository;
import com.langthang.services.IPostServices;
import com.langthang.specification.PostSpec;
import com.langthang.utils.AssertUtils;
import com.langthang.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.langthang.specification.AccountSpec.hasEmail;
import static com.langthang.specification.PostSpec.*;

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
        post.setAccount(accRepo.getByEmail(authorEmail));
        post.setPublished(isPublish);
        post.setPostCategories(getCategories(postRequestDTO));

        Post savedPost = postRepo.saveAndFlush(post);
        if (isPublish) {
            notificationServices.sendFollowersNotification(savedPost);
        }

        return new PostResponseDTO(savedPost.getId(), savedPost.getSlug());
    }

    @Override
    public PostResponseDTO getPostDetailBySlug(String slug) {
        return postRepo.findOne(hasSlug(slug).and(isPublished()))
                .map(this::entityToDTO)
                .orElseThrow(() -> NotFoundError.build(Post.class));
    }

    @Override
    public PostResponseDTO getDraftById(int postId, String authorEmail) {
        Post draft = verifyResourceOwner(postId, authorEmail);
        AssertUtils.isTrue(!draft.isPublished(), new NotFoundError("Draft not found!"));

        return PostResponseDTO.toPostResponseDTO(draft);
    }

    @Override
    public List<PostResponseDTO> getPreviewPost(Pageable pageable) {
        return postRepo.findAll(PostSpec.isPublished(), pageable)
                .map(this::entityToDTO)
                .getContent();
    }

    @Override
    public List<PostResponseDTO> findPostByKeyword(String keyword, Pageable pageable) {
        keyword = StringUtils.join(StringUtils.split(keyword, " "), " | ");

        return postRepo.searchByKeyword(keyword, pageable)
                .map(this::entityToDTO)
                .toList();
    }

    @Override
    public List<PostResponseDTO> getPopularPostByProperty(String propertyName, Pageable pageable) {
        Page<Post> responseList;

        try {
            switch (SORT_TYPE.valueOf(propertyName.toUpperCase())) {
                case BOOKMARK -> responseList = postRepo.getListOfPopularPostByBookmarkCount(pageable);
                case COMMENT -> responseList = postRepo.getListOfPopularPostByCommentCount(pageable);
                default -> {
                    return Collections.emptyList();
                }
            }

            return responseList.map(this::entityToDTO).getContent();
        } catch (IllegalArgumentException e) {
            throw new HttpError("Sort by " + propertyName + " is not support!", HttpStatus.UNPROCESSABLE_ENTITY);
        }

    }

    @Override
    public List<PostResponseDTO> getAllPostOfUser(int accountId, Pageable pageable) {
        Account account = accRepo.findById(accountId)
                .orElseThrow(() -> new NotFoundError(Account.class));

        return postRepo.findAll(hasAuthorId(account.getId()).and(isPublished()), pageable)
                .map(this::entityToDTO)
                .getContent();
    }

    @Override
    public List<PostResponseDTO> getAllPostOfUser(String accountEmail, Pageable pageable, boolean isPublished) {
        Account account = accRepo.findOne(hasEmail(accountEmail))
                .orElseThrow(() -> new NotFoundError(Account.class));

        return postRepo.findAll(hasAuthorId(account.getId()).and(publishStatusIs(isPublished)), pageable)
                .map(this::entityToDTO)
                .getContent();
    }

    @Override
    public List<PostResponseDTO> getBookmarkedPostOfUser(String accEmail, Pageable pageable) {
        return postRepo.getBookmarkedPostByAccount_Email(accEmail, pageable).map(p -> {
            PostResponseDTO dto = entityToDTO(p);
            dto.setBookmarked(true);
            return dto;
        }).getContent();
    }

    @Override
    public PostResponseDTO getPostOrDraftContent(String slug, String authorEmail) {
        return postRepo.findOne(hasSlug(slug))
                .map(p -> this.verifyResourceOwner(p, authorEmail))
                .map(PostResponseDTO::toPostResponseDTO)
                .orElseThrow(() -> new NotFoundError(Post.class));
    }

    @Override
    public void deletePostById(int postId, String authorEmail) {
        postRepo.findById(postId)
                .ifPresent(post -> {
                    verifyResourceOwner(post, authorEmail);
                    postRepo.delete(post);
                });
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
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new NotFoundError(Category.class));

        return postRepo.getAllByPostCategoriesIn(Set.of(category), pageable)
                .map(this::entityToDTO).getContent();
    }

    @Override
    public List<PostResponseDTO> getAllPostOfCategory(String slug, Pageable pageable) {
        Category category = categoryRepo.findBySlug(slug)
                .orElseThrow(() -> new NotFoundError(Category.class));

        return postRepo.getAllByPostCategoriesIn(Set.of(category), pageable)
                .map(this::entityToDTO).getContent();
    }

    @Override
    public void updateDraftById(int postId, String authorEmail, PostRequestDTO requestDTO) {
        Post existingPost = verifyResourceOwner(postId, authorEmail);
        updatePostContent(existingPost, requestDTO);
        existingPost.setPublished(false);

        postRepo.saveAndFlush(existingPost);
    }

    @Override
    public XmlUrlSet genSiteMap() {
        final String HOST = "https://langthang.trinhdvt.tech";
        var sitemap = new XmlUrlSet();
        postRepo.findAll(isPublished())
                .parallelStream()
                .map(p -> XmlUrlSet.XmlUrl.builder()
                        .loc(HOST + "/" + p.getAccount().getSlug() + "/" + p.getSlug())
                        .lastmod(DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.format(p.getPublishedDate()))
                        .build())
                .forEach(sitemap::addUrl);

        return sitemap;
    }

    @Override
    public void deleteReportedPost(int postId, String adminEmail) {
        postRepo.findById(postId).ifPresent(post -> {
            try {
                verifyResourceOwner(post, adminEmail);
                postRepo.delete(post);
            } catch (HttpError ex) {
                if (post.isPublished()) {
                    boolean isReportPost = post.getPostReports().stream().anyMatch(rp -> !rp.isSolved());
                    if (isReportPost) {
                        postRepo.delete(post);
                        return;
                    }
                }
                throw new UnauthorizedError("Permission denied");
            }
        });
    }

    private PostResponseDTO entityToDTO(Post post) {
        Account author = post.getAccount();

        AccountDTO authorDTO = AccountDTO.toBasicAccount(author);
        authorDTO.setPostCount(postRepo.count(hasAuthorId(author.getId())));
        authorDTO.setFollowCount(accRepo.countFollowing(author.getId()));

        PostResponseDTO postResponse = PostResponseDTO.toPostResponseDTO(post);
        postResponse.setAuthor(authorDTO);
        postResponse.setOwner(author.getEmail().equals(SecurityUtils.getLoggedInEmail()));

        return postResponse;
    }

    private Set<Category> getCategories(PostRequestDTO postDTO) {
        List<String> categoriesId = postDTO.getCategories();
        if (CollectionUtils.isEmpty(categoriesId))
            return Set.of();

        try {
            List<Integer> cateIdInt = categoriesId.stream().map(Integer::valueOf).toList();

            List<Category> categoryList = categoryRepo.findAllById(cateIdInt);
            return new HashSet<>(categoryList);

        } catch (NumberFormatException e) {
            throw new HttpError("Bad request", HttpStatus.BAD_REQUEST);
        }
    }

    public Post verifyResourceOwner(int postId, String authorEmail) {
        Post post = postRepo.getReferenceById(postId);
        verifyResourceOwner(post, authorEmail);
        return post;
    }

    private Post verifyResourceOwner(Post post, String authorEmail) {
        AssertUtils.notNull(post, new NotFoundError("Post not found"));
        AssertUtils.isTrue(post.getAccount().getEmail().equals(authorEmail), new UnauthorizedError("Post not found"));
        return post;
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
