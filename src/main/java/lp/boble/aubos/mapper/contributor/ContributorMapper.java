package lp.boble.aubos.mapper.contributor;

import lp.boble.aubos.dto.contributor.ContributorRequest;
import lp.boble.aubos.dto.contributor.ContributorResponse;
import lp.boble.aubos.model.book.dependencies.ContributorModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ContributorMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "lastUpdate", ignore = true)
    @Mapping(target = "books", ignore = true)
    @Mapping(target = "softDeleted", ignore = true)
    @Mapping(target = "name", source = "name")
    ContributorModel fromRequestToModel(ContributorRequest contributorRequest);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ContributorResponse fromModelToResponse(ContributorModel contributorModel);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "lastUpdate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "books", ignore = true)
    @Mapping(target = "softDeleted", ignore = true)
    @Mapping(target = "name", source = "name")
    void updateModelFromRequest(@MappingTarget ContributorModel contributorModel, ContributorRequest cr);
}
