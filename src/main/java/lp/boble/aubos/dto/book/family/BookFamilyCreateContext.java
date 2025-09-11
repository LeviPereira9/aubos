package lp.boble.aubos.dto.book.family;

import lp.boble.aubos.dto.book.relationships.BookFamily.BookFamilyCreateRequest;

import java.util.UUID;

public record BookFamilyCreateContext(
        UUID familyId,
        BookFamilyCreateRequest request
) {}
