package com.library.book.domain.model;

import com.library.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "publishers")
public class Publisher extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 256)
    private String name;

    @Lob
    @Column(name = "address")
    private String address;

    @OneToMany(mappedBy = "publisher")
    private List<Book> books;
}
