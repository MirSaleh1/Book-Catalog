package com.example.bookcatalog.model;

import com.example.bookcatalog.entity.Author;
import com.example.bookcatalog.entity.Book;
import org.springframework.data.domain.Page;

import java.util.List;


public record BookFilterResponse(List<BookResponse> bookResponse,
                                 Long totalCount,
                                 Integer totalPages,
                                 Integer currentPage,
                                 Integer pageSize,
                                 Boolean hasNextPage) {

    public static BookFilterResponse mapToBookFilterResponse(List<Book> fullBooks, Page<Book> pageInfo) {

        List<BookResponse> mappedBooks = fullBooks.stream()
                .map(b -> new BookResponse(
                        new BookRequest(
                                b.getTitle(),
                                b.getPublicationYear(),
                                b.getRating(),
                                b.getCategory().getName(),
                                b.getAuthors().stream().map(Author::getName).toList()
                        )
                )).toList();
        return new BookFilterResponse(
                mappedBooks,
                pageInfo.getTotalElements(),
                pageInfo.getTotalPages(),
                pageInfo.getNumber(),
                pageInfo.getSize(),
                pageInfo.hasNext()
        );
    }}