package com.langthang.services.impl;

import com.langthang.dto.*;
import com.langthang.exception.CustomException;
import com.langthang.model.entity.Account;
import com.langthang.model.entity.Comment;
import com.langthang.model.entity.Post;
import com.langthang.model.entity.Tag;
import com.langthang.repository.AccountRepository;
import com.langthang.repository.PostRepository;
import com.langthang.services.IPostServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public Post addNewPostOrDraft(PostRequestDTO postRequestDTO, String authorEmail, boolean isDraft) {
        Post post = dtoToEntity(postRequestDTO);
        post.setAccount(accRepo.findByEmail(authorEmail));
        post.setStatus(!isDraft);
        post.setPublishedDate(new Date());
        post.setLastModified(new Date());

        return postRepo.save(post);
    }

    @Override
    public Post findPostById(int postId) {
        return null;
    }

    @Override
    public boolean isPostNotFound(int postId) {
        return !postRepo.existsById(postId);
    }

    @Override
    public PostResponseDTO getPostDetailById(int postId) {
        Post post = postRepo.findPostByIdAndStatus(postId, true);

        if (post == null) {
            return null;
        }

        return entityToDTO(post);
    }

    @Override
    public PostResponseDTO getPostDetailBySlug(String slug) {
        Post post = postRepo.findPostBySlugAndStatus(slug, true);

        if (post == null) {
            return null;
        }

        return entityToDTO(post);
    }

    @Override
    public PostResponseDTO getDraftById(int postId) {
        Post post = postRepo.findPostByIdAndStatus(postId, false);

        if (post == null)
            return null;

        return PostResponseDTO.builder()
                .postId(postId)
                .title(post.getTitle())
                .content(post.getContent())
                .postThumbnail(post.getPostThumbnail())
                .build();
    }

    @Override
    public List<PostResponseDTO> getPreviewPost(int page, int size) {
        Page<PostResponseDTO> postResp = postRepo.getPreviewPost(PageRequest.of(page, size
                , Sort.by(Sort.Direction.DESC, "publishedDate")));

        return pageOfPostToListOfPreviewPost(postResp);
    }


    @Override
    public List<PostResponseDTO> getPreviewPost(int page, int size, String keyword) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<PostResponseDTO> postResp = postRepo.getPreviewPostByKeyword(keyword, pageRequest);

        return pageOfPostToListOfPreviewPost(postResp);
    }

    @Override
    public List<PostResponseDTO> getPopularPostByProperty(String propertyName, int size) {
        Page<PostResponseDTO> responseList;
        PageRequest pageRequest = PageRequest.of(0, size);


        try {
            switch (SORT_TYPE.valueOf(propertyName.toUpperCase())) {
                case BOOKMARK:
                    responseList = postRepo.getPopularPostByBookmarkCount(pageRequest);
                    break;

                case COMMENT:
                    responseList = postRepo.getPopularPostByCommentCount(pageRequest);
                    break;

                default:
                    return Collections.emptyList();
            }

            return pageOfPostToListOfPreviewPost(responseList);
        } catch (IllegalArgumentException e) {
            throw new CustomException("Sort by " + propertyName + " is not support!"
                    , HttpStatus.UNPROCESSABLE_ENTITY);
        }

    }

    @Override
    public List<PostResponseDTO> getAllPreviewPostOfUser(int accountId, int page, int size) {
        Account account = accRepo.findById(accountId).orElse(null);
        if (account == null) {
            throw new CustomException("Account with id: " + accountId + " not found", HttpStatus.NOT_FOUND);
        }

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<PostResponseDTO> responseList = postRepo.getAllPreviewPostOfUser(accountId, pageRequest);

        return pageOfPostToListOfPreviewPost(responseList);
    }

    @Override
    public List<PostResponseDTO> getBookmarkedPostOfUser(String accEmail, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<PostResponseDTO> responseList = postRepo.getBookmarkedPostByAccount_Email(accEmail, pageRequest);

        return pageOfPostToListOfPreviewPost(responseList);
    }

    @Override
    public boolean checkResourceOwner(int postId, String ownerEmail) {
        return postRepo.existsByIdAndAccount_Email(postId, ownerEmail);
    }

    @Override
    public void deletePostById(int postId) {
        postRepo.deleteById(postId);
    }

    @Override
    public Post updatePostById(int postId, PostRequestDTO postRequestDTO) {
        Post oldPost = postRepo.findPostById(postId);
        oldPost.setLastModified(new Date());
        oldPost.setTitle(postRequestDTO.getTitle());
        oldPost.setContent(postRequestDTO.getContent());
        oldPost.setPostThumbnail(postRequestDTO.getPostThumbnail());

        return postRepo.save(oldPost);
    }

    @Override
    public Post updateAndPublicDraft(PostRequestDTO postRequestDTO, Integer postId) {
        Post existingPost = postRepo.findPostById(postId);
        existingPost.setTitle(postRequestDTO.getTitle());
        existingPost.setContent(postRequestDTO.getContent());
        existingPost.setPostThumbnail(postRequestDTO.getPostThumbnail());
        existingPost.setLastModified(new Date());
        existingPost.setPublishedDate(new Date());
        existingPost.setStatus(true);

        return postRepo.save(existingPost);
    }

    private List<PostResponseDTO> pageOfPostToListOfPreviewPost(Page<PostResponseDTO> postResp) {
        return postResp.stream()
                .peek(dto -> {
                    dto.setBookmarkedCount(postRepo.countBookmarks(dto.getPostId()));
                    dto.setCommentCount(postRepo.countComments(dto.getPostId()));
                }).collect(Collectors.toList());
    }

    private Post dtoToEntity(PostRequestDTO dto) {
        return Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .postThumbnail(dto.getPostThumbnail())
                .build();
    }

    private PostResponseDTO entityToDTO(Post post) {
        BasicAccountDTO author = toAccountDTO(post.getAccount());

        PostResponseDTO postResponse = toPostResponseDTO(post);
        postResponse.setAuthor(author);
        postResponse.setOwner(author.getEmail().equals(getCurrentAccEmail()));

        return postResponse;
    }

    private BasicAccountDTO toAccountDTO(Account account) {

        return BasicAccountDTO.builder()
                .accountId(account.getId())
                .email(account.getEmail())
                .name(account.getName())
                .avatarLink(account.getAvatarLink())
                .about(account.getAbout())
                .occupation(account.getOccupation())
                .postCount(postRepo.countByAccount_Id(account.getId()))
                .followCount(accRepo.countFollowing(account.getId()))
                .build();
    }

    private PostResponseDTO toPostResponseDTO(Post post) {

        return PostResponseDTO.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .slug(post.getSlug())
                .content(post.getContent())
                .postThumbnail(post.getPostThumbnail())
                .publishedDate(post.getPublishedDate())
                .isBookmarked(post.getBookmarkedPosts().stream().anyMatch(bp -> bp.getAccount().getEmail().equals(getCurrentAccEmail())))
                .bookmarkedCount(post.getBookmarkedPosts().size())
                .commentCount(post.getComments().size())
                .tags(post.getPostTag().stream().map(this::tagMapper).collect(Collectors.toSet()))
                .comments(post.getComments().stream().map(this::commentMapper).collect(Collectors.toList()))
                .build();
    }

    private TagDTO tagMapper(Tag tag) {
        return TagDTO.builder()
                .tagName(tag.getTagName())
                .tagId(tag.getId())
                .tagCount(tag.getPostTag().size())
                .build();
    }

    private CommentDTO commentMapper(Comment comment) {
        Account commenter = comment.getAccount();

        BasicAccountDTO commenterDTO = BasicAccountDTO.builder()
                .accountId(commenter.getId())
                .name(commenter.getName())
                .email(commenter.getEmail())
                .avatarLink(commenter.getAvatarLink())
                .build();

        return CommentDTO.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .commentDate(comment.getCommentDate())
                .commenter(commenterDTO)
                .isMyComment(commenter.getEmail().equals(getCurrentAccEmail()))
                .likeCount(comment.getLikedAccounts().size())
                .isLiked(comment.getLikedAccounts().stream().anyMatch(a -> a.getEmail().equals(getCurrentAccEmail())))
                .build();
    }

    private String getCurrentAccEmail() {
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        if (currentAuth instanceof AnonymousAuthenticationToken) {
            return null;
        } else {
            return currentAuth.getName();
        }
    }
}
