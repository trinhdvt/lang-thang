package com.langthang.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface FullTextSearch<T> {

    @Query(value = """
            SELECT post.*, ts_rank_cd(fts_index, query, 1) as rank
            FROM "do-an-lang-thang".post, TO_TSQUERY('simple', ?1) as query
            WHERE fts_index @@ query
            ORDER BY rank DESC
            """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM "do-an-lang-thang".post, TO_TSQUERY('simple', ?1) as query
                    WHERE fts_index @@ query
                    """,
            nativeQuery = true)
    Stream<T> searchByKeyword(final String keyword, final Pageable pageable);

}
