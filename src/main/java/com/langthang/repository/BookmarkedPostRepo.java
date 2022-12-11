package com.langthang.repository;

import com.langthang.model.entity.BookmarkedPost;
import com.langthang.model.entity.BookmarkedPostKey;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BookmarkedPostRepo extends CrudRepository<BookmarkedPost, BookmarkedPostKey> {

    Optional<BookmarkedPost> findBookmarkedPostByPost_IdAndAccount_Email(int postId, String accountEmail);

}