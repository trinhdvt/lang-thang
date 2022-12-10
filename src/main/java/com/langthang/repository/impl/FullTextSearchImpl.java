package com.langthang.repository.impl;

import com.langthang.model.entity.Post;
import com.langthang.repository.FullTextSearch;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FullTextSearchImpl implements FullTextSearch<Post> {


    public FullTextSearchImpl() {

    }

    @Override
    public List<Post> searchByKeyword(String keyword, Pageable pageable) {
        return List.of();
    }
}