package com.langthang.controller;

import com.langthang.services.IPostServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sitemap")
public class SitemapController {

    private final IPostServices postServices;

    public SitemapController(IPostServices postServices) {
        this.postServices = postServices;
    }

    @GetMapping(produces = "application/xml")
    public ResponseEntity<XmlUrlSet> getSitemap() {
        return ResponseEntity.ok(postServices.genSiteMap());
    }

    @GetMapping("/hello-sitemap")
    public ResponseEntity<?> helloFromSitemap() {
        return ResponseEntity.ok("Hello from Sitemap 2");
    }

}