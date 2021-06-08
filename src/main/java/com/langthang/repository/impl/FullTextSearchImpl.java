package com.langthang.repository.impl;

import com.langthang.config.HibernateIndexService;
import com.langthang.model.Post;
import com.langthang.repository.FullTextSearch;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class FullTextSearchImpl implements FullTextSearch<Post> {

    private final EntityManager entityManager;

    public FullTextSearchImpl(EntityManager entityManager, HibernateIndexService indexService) throws InterruptedException {
        this.entityManager = entityManager;
        indexService.initIndexes(entityManager);
    }

    @Override
    public List<Post> searchByKeyword(String keyword, Pageable pageable) {
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);

        QueryBuilder qb = fullTextEntityManager
                .getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Post.class)
                .get();

        Query query = qb.keyword()
                .onFields("title", "content")
                .matching(keyword)
                .createQuery();

        FullTextQuery fullTextQuery = fullTextEntityManager
                .createFullTextQuery(query, Post.class);
        fullTextQuery.setFirstResult(pageable.getPageNumber());
        fullTextQuery.setMaxResults(pageable.getPageSize());

        return (List<Post>) fullTextQuery.getResultList();
    }
}
