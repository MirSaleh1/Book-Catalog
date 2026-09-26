package com.example.bookcatalog.mapper;

import com.example.bookcatalog.entity.Book;
import com.example.bookcatalog.model.BookRequest;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;


public interface BookSpecificationMapper {

    static Specification<Book> toSpecification(BookRequest bookrequest) {
        return (root, query, cb) ->
            {
                List<Predicate> predicates = new ArrayList<>();
                if (bookrequest.title() != null && !bookrequest.title().isBlank()) {
                    predicates.add(cb.like(cb.lower(root.get("title")), "%" + bookrequest.title().toLowerCase() + "%"));
                }
                if (bookrequest.publicationYear() != null) {
                    predicates.add(cb.equal(root.get("publicationYear"), bookrequest.publicationYear()));
                }
                if (bookrequest.rating() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("rating"), bookrequest.rating()));
                }
                if (bookrequest.categoryName() != null && !bookrequest.categoryName().isBlank()) {
                    predicates.add(cb.like(
                            cb.lower(root.join("category").get("name")),
                            "%" + bookrequest.categoryName().toLowerCase() + "%"
                    ));
                }
                if (bookrequest.authorsName() != null && !bookrequest.authorsName().isEmpty()) {
                    Join<Object, Object> authorJoin = root.join("authors");
                    List<Predicate> authorPredicates = new ArrayList<>();

                    for (String authorName : bookrequest.authorsName()) {
                        if (authorName != null && !authorName.isBlank()) {
                            authorPredicates.add(cb.like(
                                    cb.lower(authorJoin.get("name")),
                                    "%" + authorName.toLowerCase() + "%"
                            ));
                        }
                    }

                    if (!authorPredicates.isEmpty()) {
                        predicates.add(cb.or(authorPredicates.toArray(new Predicate[0])));
                        query.distinct(true);
                    }
                }
                return cb.and(predicates.toArray(new Predicate[0]));
            };
    }
}
