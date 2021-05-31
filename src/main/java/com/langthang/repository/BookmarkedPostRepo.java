package com.langthang.repository;

import com.langthang.model.BookmarkedPost;
import com.langthang.model.BookmarkedPostKey;
import org.springframework.data.repository.CrudRepository;

public interface BookmarkedPostRepo extends CrudRepository<BookmarkedPost, BookmarkedPostKey> {

    BookmarkedPost findBookmarkedPostByPost_IdAndAccount_Email(int postId, String accountEmail);

}
