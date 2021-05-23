package com.langthang.utils;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.util.HtmlUtils;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class Utils {

    static Pattern vietnameseNormPattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

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
        return HtmlUtils.htmlEscape(HtmlUtils.htmlUnescape(html));
    }


    public static String getCurrentAccEmail() {
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        if (currentAuth instanceof AnonymousAuthenticationToken) {
            return null;
        } else {
            return currentAuth.getName();
        }
    }
}
