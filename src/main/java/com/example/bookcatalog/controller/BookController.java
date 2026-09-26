package com.example.bookcatalog.controller;

import com.example.bookcatalog.model.BookFilterResponse;
import com.example.bookcatalog.model.BookRequest;
import com.example.bookcatalog.model.PagesRequest;
import com.example.bookcatalog.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public BookFilterResponse getBooks(@ModelAttribute @ParameterObject BookRequest bookRequest,
                                       @ModelAttribute @ParameterObject @Valid PagesRequest pagesRequest) {
        BookFilterResponse response = bookService.getBooks(bookRequest, pagesRequest);
        return response;
    }
}