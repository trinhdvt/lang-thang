package com.langthang.model.entity;

import com.langthang.utils.MyStringUtils;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", length = 500)
    private String slug;

    @ManyToMany(mappedBy = "categories")
    @LazyCollection(LazyCollectionOption.EXTRA)
    private List<Post> postCategories = new ArrayList<>();

    public Category(String name) {
        this.name = name;
    }

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