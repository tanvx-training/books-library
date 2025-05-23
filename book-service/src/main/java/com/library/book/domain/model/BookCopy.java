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
@Table(
        name = "book_copies",
        uniqueConstraints = @UniqueConstraint(columnNames = {"book_id", "copy_number"})
)
public class BookCopy extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(name = "copy_number", nullable = false, length = 20)
    private String copyNumber;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "condition", length = 20)
    private String condition;

    @Column(name = "location", length = 50)
    private String location;

    @OneToMany(mappedBy = "bookCopy")
    private List<Borrowing> borrowings;
}