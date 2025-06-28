package com.library.book.model;

import com.library.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "categories")
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 256)
    private String name;

    @Column(name = "slug", nullable = false, length = 256)
    private String slug;

    @Column(name = "description")
    private String description;

    @ManyToMany(mappedBy = "categories")
    private List<Book> books;
}
