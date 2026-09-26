package com.example.bookcatalog.repository;

import com.example.bookcatalog.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer>, JpaSpecificationExecutor<Book> {

    @Query("SELECT DISTINCT b FROM Book b " +
            "JOIN FETCH b.category " +
            "JOIN FETCH b.authors " +
            "WHERE b.id IN :bookIds")
    List<Book> findAllWithAuthorsAndCategoriesByIds(@Param("bookIds") List<Long> bookIds);
}
