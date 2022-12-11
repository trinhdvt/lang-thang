package com.langthang.specification;

import com.langthang.model.entity.Account;
import com.langthang.model.entity.Account_;
import com.langthang.model.entity.Post;
import com.langthang.model.entity.Post_;
import jakarta.persistence.criteria.Join;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostSpec {

    public static Specification<Post> hasSlug(String slug) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Post_.SLUG), slug);
    }

    public static Specification<Post> publishStatusIs(boolean status) {
        return (root, query, cb) -> cb.equal(root.get(Post_.PUBLISHED), status);
    }

    public static Specification<Post> isPublished() {
        return publishStatusIs(true);
    }

    public static Specification<Post> hasId(Integer id) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Post_.ID), id);
    }

    public static Specification<Post> isPublished(Integer id) {
        return isPublished().and(hasId(id));
    }

    public static Specification<Post> hasAuthorId(Integer authorId) {
        return (root, query, criteriaBuilder) -> {
            Join<Account, Post> authorBooks = root.join(Post_.ACCOUNT);
            return criteriaBuilder.equal(authorBooks.get(Account_.ID), authorId);
        };
    }


}
