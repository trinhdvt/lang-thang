package com.langthang.model;

import com.langthang.utils.Utils;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
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

    @ManyToMany(mappedBy = "postCategories")
    @LazyCollection(LazyCollectionOption.EXTRA)
    private List<Post> postCategories = new ArrayList<>();

    @PrePersist
    @PreUpdate
    public void escapeHtml() {
        this.name = Utils.escapeHtml(name);
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
