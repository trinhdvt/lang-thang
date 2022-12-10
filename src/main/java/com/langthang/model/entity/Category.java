package com.langthang.model.entity;

import com.langthang.utils.MyStringUtils;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NonNull
    private String name;

    private String slug;

    @ManyToMany(mappedBy = "postCategories")
    @LazyCollection(LazyCollectionOption.EXTRA)
    private List<Post> postCategories = new ArrayList<>();

    @PrePersist
    @PreUpdate
    public void escapeHtml() {
        this.name = MyStringUtils.escapeHtml(name);
        this.slug = MyStringUtils.createSlug(slug);
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}