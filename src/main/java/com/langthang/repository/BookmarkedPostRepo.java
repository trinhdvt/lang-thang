package com.langthang.repository;

import com.langthang.model.entity.BookmarkedPost;
import com.langthang.model.entity.BookmarkedPostKey;
import org.springframework.data.repository.CrudRepository;

public interface BookmarkedPostRepo extends CrudRepository<BookmarkedPost, BookmarkedPostKey> {

    BookmarkedPost findBookmarkedPostByPost_IdAndAccount_Email(int postId, String accountEmail);

}