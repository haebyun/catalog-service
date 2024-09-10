package com.polarbookshop.catalogservice.domain;


import java.util.Iterator;
import java.util.Optional;

public interface BookRepository {
    Iterator<Book> findAll();

    Optional<Book> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    Book save(Book book);

    void deleteByIsbn(String isbn);
}
