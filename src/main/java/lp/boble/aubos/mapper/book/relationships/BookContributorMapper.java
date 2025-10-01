package lp.boble.aubos.mapper.book.relationships;

import lp.boble.aubos.dto.book.parts.BookContributorPartResponse;
import lp.boble.aubos.dto.book.relationships.BookContributor.BookContributorResponse;
import lp.boble.aubos.dto.book.relationships.BookContributor.BookContributorUpdateBatchRequest;
import lp.boble.aubos.dto.book.relationships.BookContributor.BookContributorUpdateRequest;
import lp.boble.aubos.dto.book.relationships.BookContributor.BookContributorsResponse;
import lp.boble.aubos.model.book.dependencies.ContributorModel;
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

    @Mapping(target = "startDate", source = "startDate")
    @Mapping(target = "endDate", source = "endDate")
    void updateBookContributor(
            @MappingTarget BookContributorModel target,
            BookContributorUpdateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "startDate", source = "startDate")
    @Mapping(target = "endDate", source = "endDate")
    void updateBookContributor(
            @MappingTarget BookContributorModel target,
            BookContributorUpdateBatchRequest request);

    // Mapeia um único BookContributorModel para a parte do response
    // Mapeia um ContributorModel direto para o PartResponse
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    BookContributorPartResponse toPartResponse(ContributorModel contributor);

    // Método principal
    default BookContributorsResponse toResponse(List<BookContributorModel> models) {
        BookContributorsResponse response = new BookContributorsResponse();

        Map<String, List<ContributorModel>> groupedByRole = models.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getContributorRole().getName().toLowerCase(),
                        Collectors.mapping(
                                BookContributorModel::getContributor, // Extrai o Contributor
                                Collectors.toList()
                        )
                ));

        // Preenche os campos no response - agora com List<ContributorModel>
        response.setAuthors(toPartResponseList(groupedByRole.getOrDefault("autor", Collections.emptyList())));
        response.setEditors(toPartResponseList(groupedByRole.getOrDefault("editor", Collections.emptyList())));
        response.setIllustrators(toPartResponseList(groupedByRole.getOrDefault("ilustrador", Collections.emptyList())));
        response.setPublishers(toPartResponseList(groupedByRole.getOrDefault("publicadora", Collections.emptyList())));

        return response;
    }

    // Helper para mapear uma lista de ContributorModel (AGORA CORRETO!)
    default List<BookContributorPartResponse> toPartResponseList(List<ContributorModel> contributors) {
        return contributors.stream()
                .map(this::toPartResponse) // Mapeia ContributorModel → BookContributorPartResponse
                .toList();
    }

}
