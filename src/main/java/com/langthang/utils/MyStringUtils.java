package com.langthang.utils;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import java.text.Normalizer;
import java.util.UUID;
import java.util.regex.Pattern;

@Component
public class MyStringUtils extends StringUtils {

    static final String[] htmlCharacter = new String[]{"&", "<", ">", "\"", "'", "/"};
    static final String[] escapedCharacter = new String[]{"&amp;", "&lt;", "&gt;", "&quot;", "&#x27;", "&#x2F;"};
    static final Pattern vietnameseNormPattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    public static String createSlug(String input) {
        String temp = Normalizer.normalize(input, Normalizer.Form.NFD);
        return vietnameseNormPattern.matcher(temp)
                .replaceAll("")
                .toLowerCase()
                .replace("đ", "d")
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

    public static String randomID() {
        return NanoIdUtils.randomNanoId();
    }

    public static String randomID(int size) {
        return NanoIdUtils.randomNanoId(NanoIdUtils.DEFAULT_NUMBER_GENERATOR, NanoIdUtils.DEFAULT_ALPHABET, size);
    }

}