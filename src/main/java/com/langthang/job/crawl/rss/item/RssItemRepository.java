package com.langthang.job.crawl.rss.item;

import java.util.List;
import java.util.Set;


public interface RssItemRepository {

    void saveAll(List<RssItemModel> items);

    void updateStatus(Set<Long> ids, Boolean status);

}
