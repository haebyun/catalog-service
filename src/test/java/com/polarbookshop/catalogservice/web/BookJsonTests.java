package com.polarbookshop.catalogservice.web;

import static org.assertj.core.api.Assertions.*;

import com.polarbookshop.catalogservice.domain.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

@JsonTest
public class BookJsonTests {

    @Autowired
    private JacksonTester<Book> json;

    @Test
    void testSerialize() throws Exception {
        Book book = new Book("1234567890", "Title", "Author", 9.90);
        JsonContent<Book> jsonContent = json.write(book);
        assertThat(jsonContent).hasJsonPathStringValue("@.isbn")
                .hasJsonPathStringValue("@.title")
                .hasJsonPathStringValue("@.author")
                .hasJsonPathNumberValue("@.price");
        assertThat(jsonContent).extractingJsonPathStringValue("@.isbn")
                .isEqualTo(book.isbn());
        assertThat(jsonContent).extractingJsonPathStringValue("@.title")
                .isEqualTo(book.title());
        assertThat(jsonContent).extractingJsonPathStringValue("@.author")
                .isEqualTo(book.author());
        assertThat(jsonContent).extractingJsonPathNumberValue("@.price")
                .isEqualTo(book.price());
    }

    @Test
    void TestDeserialize() throws Exception {
        String content = """
                {
                  "isbn": "1234567890",
                  "title": "Title",
                  "author": "Author",
                  "price": "9.90"
                }
                """;
        assertThat(json.parse(content))
                .usingRecursiveComparison()
                .isEqualTo(new Book("1234567890", "Title", "Author", 9.90));
    }
}
