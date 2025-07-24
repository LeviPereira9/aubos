package lp.boble.aubos.mapper.book;

import lp.boble.aubos.dto.book.BookCreateRequest;
import lp.boble.aubos.dto.book.dependencies.DependencyData;
import lp.boble.aubos.model.book.BookModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring"
)
public interface BookMapper {

    @Mapping(target = "language", source = "dp.language")
    @Mapping(target = "type", source = "dp.type")
    @Mapping(target = "status", source = "dp.status")
    @Mapping(target = "restriction", source = "dp.restriction")
    @Mapping(target = "license", source = "dp.license")
    BookModel fromCreateRequestToModel(
            BookCreateRequest bookCreateRequest,
            DependencyData dp);

}
