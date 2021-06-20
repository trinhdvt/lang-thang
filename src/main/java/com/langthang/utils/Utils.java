package com.langthang.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.HtmlUtils;

import java.text.Normalizer;
import java.util.Base64;
import java.util.Random;
import java.util.regex.Pattern;

public class Utils extends StringUtils {

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

    static final String[] htmlCharacter = new String[]{"&", "<", ">", "\"", "'", "/"};
    static final String[] escapedCharacter = new String[]{"&amp;", "&lt;", "&gt;", "&quot;", "&#x27;", "&#x2F;"};

    public static String escapeHtml(String html) {
        if (html == null)
            return "";
        return StringUtils.replaceEach(HtmlUtils.htmlUnescape(html), htmlCharacter, escapedCharacter);
    }

    static final Random random = new Random();

    public static String randomString(int length) {
        if (length < 1)
            throw new RuntimeException("Length cannot be negative");

        byte[] arr = new byte[length];
        random.nextBytes(arr);
        return Base64.getEncoder().encodeToString(arr);
    }

    public static String getAppUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
    }
}
