package com.langthang.job.crawl.parser;

import com.langthang.job.crawl.rss.item.RssItemModel;
import com.langthang.model.entity.Post;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Set;

public class DuLichVietNameParser implements IArticleParser {

    private final RssItemModel item;
    private final Document document;

    public DuLichVietNameParser(RssItemModel item) throws IOException {
        this.item = item;
        this.document = Jsoup.connect(item.getLink()).get();
        cleanImgLink();
    }

    public Post parse() {
        var contentHtmlElement = parseArticleContent();
        var firstImgTag = parseArticleContent().select("img").first();

        var post = new Post();
        post.setContent(contentHtmlElement.html());
        post.setTitle(item.getTitle());
        post.setPublishedDate(item.getPubDate());
        if (firstImgTag != null) {
            var imgLink = firstImgTag.attr("src");
            post.setPostThumbnail(imgLink);
        }

        return post;
    }

    public Element parseArticleContent() {
        var elements = document.select("div.the-content");
        var firstEle = elements.first();

        var removeElementClasses = Set.of("div.div_adv", "div.the-reference");
        for (String aClass : removeElementClasses) {
            elements.select(aClass).remove();
        }

        if (firstEle == null)
            throw new RuntimeException("Can't parse content of article: " + item.getLink());

        return firstEle;
    }

    private void cleanImgLink() {
        String imgPrefix = "https://dulichvietnam.com.vn";

        for (var ele : document.select("img")) {
            String imgSrc = ele.attr("src");
            if (!imgSrc.startsWith(imgPrefix)) {
                ele.attr("src", imgPrefix + imgSrc);
            }
        }
    }
}
