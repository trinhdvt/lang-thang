package com.langthang.specification;

import com.langthang.model.entity.Account;
import com.langthang.model.entity.Account_;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountSpec {

    public static Specification<Account> hasEmail(String email) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Account_.EMAIL), email);
    }

    public static Specification<Account> isEnabled() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Account_.ENABLED), true);
    }

    public static Specification<Account> hasId(Integer id) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Account_.ID), id);
    }

    public static Specification<Account> isEnabled(Integer id) {
        return hasId(id).and(isEnabled());
    }

    public static Specification<Account> hasSlug(String slug) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Account_.SLUG), slug);
    }

    public static Specification<Account> hasRegisterToken(String token) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Account_.REGISTER_TOKEN), token);
    }

}
