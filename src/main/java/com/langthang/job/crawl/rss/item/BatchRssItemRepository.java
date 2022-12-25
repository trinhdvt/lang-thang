package com.langthang.job.crawl.rss.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Repository
@Slf4j
public class BatchRssItemRepository implements RssItemRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BatchRssItemRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void saveAll(List<RssItemModel> items) {
        var rs = jdbcTemplate.batchUpdate("""
                        INSERT INTO "do-an-lang-thang".rss_item (title, description, link, pub_date)
                        VALUES (?, ?, ?, ?)
                        ON CONFLICT DO NOTHING
                        """,
                items,
                50,
                (ps, item) -> {
                    ps.setString(1, item.getTitle());
                    ps.setString(2, item.getDescription());
                    ps.setString(3, item.getLink());
                    ps.setTimestamp(4, Timestamp.from(item.getPubDate()));
                });

        var rowInserted = Arrays.stream(rs)
                .flatMapToInt(Arrays::stream)
                .reduce(Integer::sum)
                .orElse(0);
        log.info("Inserted {} / {} rows", rowInserted, items.size());
    }

    @Override
    @Transactional
    public void updateStatus(Set<Long> ids, Boolean status) {
        jdbcTemplate.batchUpdate("""
                        UPDATE "do-an-lang-thang".rss_item
                        SET is_processed = ?
                        WHERE id = ?
                        """,
                ids,
                50,
                (ps, id) -> {
                    ps.setBoolean(1, status);
                    ps.setLong(2, id);
                });
    }
}
