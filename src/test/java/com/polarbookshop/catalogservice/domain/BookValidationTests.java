package com.polarbookshop.catalogservice.domain;

import static org.assertj.core.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class BookValidationTests {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenFieldsCorrectThenValidationSucceeds() {
        Book book = Book.of("1234567890", "Title", "Author", 9.90);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).isEmpty();
    }

    @Test
    void whenIsbnDefinedButIncorrectThenValidationFails() {
        Book book = Book.of("a234567890", "Title", "Author", 9.90);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .isEqualTo("The ISBN format must be valid.");
    }

    @ParameterizedTest
    @CsvSource({
            // 유효하지 않은 ISBN (잘못된 형식)
            "a234567890, Title, Author, 9.90, 1, 'The ISBN format must be valid.'",
            "12345, Title, Author, 9.90, 1, 'The ISBN format must be valid.'",
            "12345678901234, Title, Author, 9.90, 1, 'The ISBN format must be valid.'",

            // ISBN이 비어 있는 경우
            ", Title, Author, 9.90, 1, 'The Book ISBN must be defined.'",

            // 타이틀이 비어 있는 경우
            "1234567890, , Author, 9.90, 1, 'The book title must be defined.'",

            // 작가가 정의되지 않은 경우
            "1234567890, Title, , 9.90, 1, 'The book author must be defined.'",

            // 가격이 유효하지 않은 경우 (음수)
            "1234567890, Title, Author, -10.00, 1, 'The book price must be greater than zero.'",

            // 가격이 비어 있는 경우
            "1234567890, Title, Author, , 1, 'The book price must be defined.'"
    })
    void whenFieldIsIncorrectThenValidationFails(String isbn, String title, String author, Double price, int expectedViolationCount, String expectedMessage) {
        Book book = Book.of(isbn, title, author, price);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        assertThat(violations).hasSize(expectedViolationCount);

        if (expectedViolationCount > 0) {
            assertThat(violations.iterator().next().getMessage()).isEqualTo(expectedMessage);
        }
    }

    @ParameterizedTest
    @CsvSource({
            "123, , , -1.0, 4",  // ISBN, 타이틀, 작가, 가격 모두 유효하지 않은 경우
            ", , , , 4",         // 모든 필드가 비어 있는 경우
            "1234567890, , , -5.0, 3",  // 타이틀과 작가가 비어있고, 가격이 음수인 경우
            ", Title, , 0.0, 3",        // ISBN과 작가가 비어있고, 가격이 0인 경우
            ", , Author, -0.01, 3"      // ISBN과 타이틀이 비어있고, 가격이 0보다 작은 경우
    })
    void whenMultipleFieldsAreIncorrectThenValidationFails(String isbn, String title, String author, Double price, int expectedViolationCount) {
        Book book = Book.of(isbn, title, author, price);
        Set<ConstraintViolation<Book>> violations = validator.validate(book);

        // 위반 개수만 검증
        assertThat(violations).hasSize(expectedViolationCount);
    }

}
