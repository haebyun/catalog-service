package com.polarbookshop.catalogservice;

import static org.assertj.core.api.Assertions.*;

import com.polarbookshop.catalogservice.domain.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(
        webEnvironment = WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("integration")
class CatalogServiceApplicationTests {

   @Autowired
    private WebTestClient webTestClient;

   @Test
    void whenPostRequestThenBookCreated() {
       Book expectedBook = Book.of("1231231231", "Title", "Author", 9.90, "Polarsophia");

       webTestClient
               .post()
               .uri("/books")
               .bodyValue(expectedBook)
               .exchange()
               .expectStatus().isCreated()
               .expectBody(Book.class).value(actualBook -> {
                   assertThat(actualBook).isNotNull();
                   assertThat(actualBook.isbn())
                           .isEqualTo(expectedBook.isbn());
               });
   }


}
