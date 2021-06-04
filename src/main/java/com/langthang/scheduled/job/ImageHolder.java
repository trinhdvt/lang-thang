package com.langthang.scheduled.job;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class ImageHolder {
    private Set<String> imageUrlContainer = null;

    public void init() {
        if (imageUrlContainer == null) {
            imageUrlContainer = Collections.synchronizedSet(new HashSet<>());
        }
    }

    public void addItem(String item) {
        if (imageUrlContainer == null) {
            throw new RuntimeException("Holder not init yet");
        }

        if (item != null)
            imageUrlContainer.add(item);
    }

    public Set<String> getContent() {
        if (imageUrlContainer == null) {
            throw new RuntimeException("Holder not init yet");
        }
        return imageUrlContainer;
    }

    public void destroy() {
        imageUrlContainer = null;
    }
}
