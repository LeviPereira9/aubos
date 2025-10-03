package lp.boble.aubos.dto.book.family;

import lp.boble.aubos.dto.book.relationships.bookFamily.BookFamilyCreateRequest;

import java.util.UUID;

public record BookFamilyCreateContext(
        UUID familyId,
        BookFamilyCreateRequest request
) {}
