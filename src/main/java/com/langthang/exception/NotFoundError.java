package com.langthang.exception;

import com.langthang.model.entity.Account;
import com.langthang.model.entity.Post;
import org.springframework.http.HttpStatus;

public class NotFoundError extends HttpError {

    public NotFoundError(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public NotFoundError(Class clazz) {
        super(clazz.getSimpleName() + " not found", HttpStatus.NOT_FOUND);
    }

    public static NotFoundError build(Class clazz) {
        if (clazz.equals(Post.class)) {
            return new NotFoundError("Post not found!");
        }

        if (clazz.equals(Account.class)) {
            return new NotFoundError("Account not found!");
        }

        return new NotFoundError("Not found!");
    }

}
