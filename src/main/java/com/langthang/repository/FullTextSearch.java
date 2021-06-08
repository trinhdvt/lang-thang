package com.langthang.repository;

import org.springframework.data.domain.Pageable;

import java.util.List;


public interface FullTextSearch<T> {

    List<T> searchByKeyword(String keyword, Pageable pageable);

}
