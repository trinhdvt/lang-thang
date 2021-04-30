package com.langthang.repository;

import com.langthang.model.entity.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface PostRepository extends CrudRepository<Post, Integer> {
    Set<Post> findAllByAccount_Email(String email);

    @Query("select p from Post p where p.account.id= ?1")
    Set<Post> findAllByAccount_Id(int id);

    Post findPostById(int id);
}
