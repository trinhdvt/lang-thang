package com.langthang.controller;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.*;


import java.util.ArrayList;
import java.util.Collection;


@XmlRootElement(name = "urlset", namespace = "http://www.sitemaps.org/schemas/sitemap/0.9")
@XmlAccessorType(XmlAccessType.NONE)
@Getter
public class XmlUrlSet {

    @XmlElement(name = "url", type = XmlUrl.class)
    private final Collection<XmlUrl> xmlUrls = new ArrayList<>();

    public void addUrl(XmlUrl url) {
        this.xmlUrls.add(url);
    }


    @XmlRootElement(name = "url")
    @XmlAccessorType(XmlAccessType.NONE)
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class XmlUrl {
        @XmlElement
        private String loc;
        @XmlElement
        private String lastmod;
    }
}