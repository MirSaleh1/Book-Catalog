package com.example.bookcatalog.service;

import com.example.bookcatalog.entity.Book;
import com.example.bookcatalog.mapper.BookSpecificationMapper;
import com.example.bookcatalog.model.BookFilterResponse;
import com.example.bookcatalog.model.BookRequest;

import com.example.bookcatalog.model.BookResponse;
import com.example.bookcatalog.model.PagesRequest;
import com.example.bookcatalog.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;

import java.util.List;


@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public BookFilterResponse getBooks(BookRequest bookRequest, PagesRequest pagesRequest) {

        Specification<Book> specification= BookSpecificationMapper.toSpecification(bookRequest);
        Pageable pageable= PageRequest.of(pagesRequest.page(), pagesRequest.size());
        Page<Book>bookPage=bookRepository.findAll(specification,pageable);

        if(bookPage.isEmpty()){
            return BookFilterResponse.mapToBookFilterResponse(List.of(),bookPage) ;
        }
        List<Long> bookIds = bookPage.getContent().stream()
                .map(Book::getId)
                .toList();

        List<Book> booksWithDetails = bookRepository.findAllWithAuthorsAndCategoriesByIds(bookIds);
        return BookFilterResponse.mapToBookFilterResponse(booksWithDetails, bookPage);

    }

}
