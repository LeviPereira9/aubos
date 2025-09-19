package lp.boble.aubos.mapper.book.relationships;

import lp.boble.aubos.dto.book.parts.BookContributorPartResponse;
import lp.boble.aubos.dto.book.relationships.BookContributor.BookContributorResponse;
import lp.boble.aubos.dto.book.relationships.BookContributor.BookContributorsResponse;
import lp.boble.aubos.model.book.dependencies.ContributorRole;
import lp.boble.aubos.model.book.relationships.BookContributorModel;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface BookContributorMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "contributor.name")
    @Mapping(target = "role", source = "contributorRole.name")
    BookContributorResponse fromModelToResponse(BookContributorModel model);

    /*@Mapping(target = "contributor", ignore = true)
    @Mapping(target = "contributorRole", source = "role")
    void updateBookContributor(@MappingTarget BookContributorModel model, ContributorRole role);*/

    /*@Mapping(target = "authors", ignore = true )
    @Mapping(target = "editors", ignore = true)
    @Mapping(target = "illustrators", ignore = true)
    @Mapping(target = "publishers", ignore = true)
    BookContributorsResponse toResponse(List<BookContributorModel> models);*/

    // Mapeia um único BookContributorModel para a parte do response
    BookContributorPartResponse toPartResponse(BookContributorModel model);

    // Default method para converter uma lista de BookContributorModel para BookContributorsResponse
    default BookContributorsResponse toResponse(List<BookContributorModel> models) {
        BookContributorsResponse response = new BookContributorsResponse();

        // Agrupa contributors por role (case insensitive)
        Map<String, List<BookContributorModel>> groupedByRole = models.stream()
                .collect(Collectors.groupingBy(c -> c.getContributorRole().getName().toLowerCase()));

        // Preenche os campos no response
        response.setAuthors(toPartResponseList(groupedByRole.getOrDefault("autor", Collections.emptyList())));
        response.setEditors(toPartResponseList(groupedByRole.getOrDefault("editor", Collections.emptyList())));
        response.setIllustrators(toPartResponseList(groupedByRole.getOrDefault("ilustrador", Collections.emptyList())));
        response.setPublishers(toPartResponseList(groupedByRole.getOrDefault("publicadora", Collections.emptyList())));

        return response;
    }

    // Helper para mapear uma lista de models para lista de response
    default List<BookContributorPartResponse> toPartResponseList(List<BookContributorModel> models) {
        return models.stream()
                .map(this::toPartResponse)
                .toList();
    }

}
