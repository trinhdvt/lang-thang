package com.langthang.repository;

import com.langthang.model.entity.Account;
import com.langthang.model.entity.BookmarkedPost;
import com.langthang.model.entity.BookmarkedPostKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkedPostRepo extends JpaRepository<BookmarkedPost, BookmarkedPostKey> {

    boolean existsByAccountAndPost_Id(Account account, int postId);

}