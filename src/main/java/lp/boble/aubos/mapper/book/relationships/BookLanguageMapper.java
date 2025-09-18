package lp.boble.aubos.mapper.book.relationships;

import lp.boble.aubos.dto.book.relationships.BookLanguage.BookLanguageCreatedResponse;
import lp.boble.aubos.dto.book.relationships.BookLanguage.BookLanguageResponse;
import lp.boble.aubos.model.book.relationships.BookLanguage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookLanguageMapper {

    @Mapping(target = "language", source = "language.value")
    BookLanguageResponse toResponse(BookLanguage bookLanguage);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "languageId", source = "languageId")
    @Mapping(target = "bookId", source = "bookId")
    BookLanguageCreatedResponse toCreatedResponse(BookLanguage bookLanguage);
}
