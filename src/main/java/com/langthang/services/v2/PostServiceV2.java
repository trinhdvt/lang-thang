package com.langthang.services.v2;

import com.langthang.exception.NotFoundError;
import com.langthang.exception.UnprocessableEntity;
import com.langthang.mapper.PostMapper;
import com.langthang.model.constraints.PostPopularType;
import com.langthang.model.dto.v2.request.PostCreateDto;
import com.langthang.model.dto.v2.response.PostDtoV2;
import com.langthang.model.entity.Account;
import com.langthang.model.entity.Post;
import com.langthang.repository.PostRepository;
import com.langthang.specification.PostSpec;
import com.langthang.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.langthang.specification.PostSpec.*;

@Service
@Transactional
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class PostServiceV2 {

    private final PostRepository postRepo;

    private final PostMapper postMapper;


    public PostDtoV2 getBySlug(String slug) {
        return getPost(
                -1,
                slug,
                Post::isPublished,
                postMapper::toDto
        );
    }

    public PostDtoV2 getById(Integer postId) {
        return getPost(
                postId,
                StringUtils.EMPTY,
                Post::isPublished,
                postMapper::toDto
        );
    }

    public List<PostDtoV2> getListOfPopularPosts(PostPopularType popularType, @NonNull Pageable pageable) {
        Page<Post> response;

        switch (popularType) {
            case COMMENT -> response = postRepo.getListOfPopularPostByCommentCount(pageable);
            case BOOKMARK -> response = postRepo.getListOfPopularPostByBookmarkCount(pageable);
            default -> {
                return Collections.emptyList();
            }
        }

        return response.map(postMapper::toDto).getContent();
    }

    public List<PostDtoV2> getLatestPost(Pageable pageable) {
        return postRepo.findAll(isPublished(), pageable)
                .map(postMapper::toDto)
                .getContent();
    }

    public String createNewPost(Account author, PostCreateDto payload, boolean isPublish) {
        return Optional.ofNullable(postMapper.createFromDto(payload, author, isPublish))
                .map(post -> postRepo.save(post).getSlug())
                .orElseThrow(() -> new UnprocessableEntity("Can't save post"));
    }

    public PostDtoV2 getEditableContentBySlug(String slug) {
        return getPost(-1,
                slug,
                authorCheck,
                postMapper::toReadOnlyDto);
    }

    public String updatePost(Integer postId, PostCreateDto payload) {
        var post = getPost(postId,
                StringUtils.EMPTY,
                authorCheck,
                Function.identity());

        post = postMapper.updateFromDto(post, payload);
        post = postRepo.save(post);
        return post.getSlug();
    }

    /**
     * @param id     Post's id
     * @param slug   Post's slug
     * @param filter Filter apply on result
     * @param mapper Result consumer
     * @return Post
     */
    public <T> T getPost(@NonNull Integer id,
                         @NonNull String slug,
                         @NonNull Predicate<Post> filter,
                         @NonNull Function<Post, T> mapper) {
        return postRepo.findOne(hasId(id).or(hasSlug(slug))
                        .and(PostSpec.eagerLoad()))
                .filter(filter)
                .map(mapper)
                .orElseThrow(() -> new NotFoundError(Post.class));
    }

    private final Predicate<Post> authorCheck = post -> SecurityUtils.currentUser()
            .map(currentUser -> Objects.equals(currentUser.getUserId(), post.getAuthor().getId()))
            .orElseThrow(() -> new NotFoundError(Post.class));

}
