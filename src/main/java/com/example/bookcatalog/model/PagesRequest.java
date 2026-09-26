package com.example.bookcatalog.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.RequestParam;

public record PagesRequest (
       @RequestParam(required = false,defaultValue = "0")@Min(value = 0, message = "Səhifə nömrəsi 0-dan kiçik ola bilməz.")
       Integer page,
       @RequestParam(required = false,defaultValue = "0")@Max(value = 100, message = "Səhifə ölçüsü 100-dən çox ola bilməz.")
       Integer size) {
}
