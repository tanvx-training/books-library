package com.library.book.domain.model;

import com.library.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "books")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Book extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "isbn", nullable = false, unique = true, length = 256)
    private String isbn;

    @Column(name = "title", nullable = false, length = 1000)
    private String title;

    @Column(name = "author", nullable = false, length = 256)
    private String author;

    @Column(name = "publication_year")
    private Integer publicationYear;

    @Column(name = "publisher", length = 256)
    private String publisher;

    @Column(name = "image_url_s")
    private String imageUrlS;

    @Column(name = "image_url-m")
    private String imageUrlM;

    @Column(name = "image-url-l")
    private String imageUrlL;

    @Column(name = "available_copies")
    private Integer availableCopies;

    @Column(name = "total_copies")
    private Integer totalCopies;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
