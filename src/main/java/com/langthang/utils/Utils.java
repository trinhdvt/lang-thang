package com.langthang.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import java.text.Normalizer;
import java.util.UUID;
import java.util.regex.Pattern;

@Component
public class Utils extends StringUtils {

    static final String[] htmlCharacter = new String[]{"&", "<", ">", "\"", "'", "/"};
    static final String[] escapedCharacter = new String[]{"&amp;", "&lt;", "&gt;", "&quot;", "&#x27;", "&#x2F;"};
    static Pattern vietnameseNormPattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    private static String appUrl;

    public static String createSlug(String input) {
        String temp = Normalizer.normalize(input, Normalizer.Form.NFD);
        return vietnameseNormPattern.matcher(temp)
                .replaceAll("")
                .toLowerCase()
                .replaceAll("đ", "d")
                .replaceAll("(?!-)\\W", "-")
                .replaceAll("^-*|-*$", "")
                .replaceAll("-{2,}", "-");
    }

    public static String escapeHtml(String html) {
        if (html == null)
            return "";
        return StringUtils.replaceEach(HtmlUtils.htmlUnescape(html), htmlCharacter, escapedCharacter);
    }

    public static String randomUUID() {
        return UUID.randomUUID().toString();
    }

    public static String getAppUrl() {
        return appUrl;
    }

    @Value("${application.server.url}")
    public void setAppUrl(String appUrl) {
        Utils.appUrl = appUrl;
    }
}