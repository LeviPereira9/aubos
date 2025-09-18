package lp.boble.aubos.mapper.book.family;

import lp.boble.aubos.dto.book.relationships.BookFamily.BookFamilyCreateRequest;
import lp.boble.aubos.dto.book.relationships.BookFamily.BookFamilyResponse;
import lp.boble.aubos.dto.book.relationships.BookFamily.BookFamilyUpdateRequest;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.family.FamilyModel;
import lp.boble.aubos.model.book.relationships.BookFamilyModel;
import lp.boble.aubos.model.user.UserModel;
import lp.boble.aubos.util.AuthUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookFamilyMapper {

    @Mapping(target = "id", source = "book.id")
    @Mapping(target = "order", source = "orderInFamily")
    @Mapping(target = "title", source = "book.title")
    BookFamilyResponse fromModelToResponse(BookFamilyModel model);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", expression = "java(requester())")
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "book", source = "book")
    @Mapping(target = "family", source = "family")
    @Mapping(target = "orderInFamily", source = "request.order")
    @Mapping(target = "note", source = "request.note")
    BookFamilyModel fromCreateRequestToModel(
            BookFamilyCreateRequest request,
            BookModel book,
            FamilyModel family);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", expression = "java(requester())")
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "book", ignore = true)
    @Mapping(target = "family", ignore = true)
    @Mapping(target = "orderInFamily", source = "order")
    @Mapping(target = "note", source = "note")
    BookFamilyModel toModelWithoutDependencies(BookFamilyCreateRequest request);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", expression = "java(requester())")
    @Mapping(target = "orderInFamily", source = "order")
    @Mapping(target = "note", source = "note")
    @Mapping(target = "lastUpdate", expression = "java(java.time.Instant.now())")
    void toUpdateFromRequest(@MappingTarget BookFamilyModel model, BookFamilyUpdateRequest request);

    default UserModel requester(){
        return AuthUtil.requester();
    }

}
