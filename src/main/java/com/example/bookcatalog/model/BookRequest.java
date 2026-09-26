package com.example.bookcatalog.model;

import java.util.List;

public record BookRequest(String title,
                          Integer publicationYear,
                          Double rating,
                          String categoryName,
                          List<String>authorsName) {
}
