package lp.boble.aubos.service.book.relationships.contributor;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.parts.BookAddContributor;
import lp.boble.aubos.dto.book.relationships.BookContributor.BookContributorDeleteRequest;
import lp.boble.aubos.dto.book.relationships.BookContributor.BookContributorUpdateBatchRequest;
import lp.boble.aubos.mapper.book.relationships.BookContributorMapper;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.dependencies.ContributorModel;
import lp.boble.aubos.model.book.dependencies.ContributorRole;
import lp.boble.aubos.model.book.relationships.BookContributorModel;
import lp.boble.aubos.repository.book.relationships.BookContributorRepository;
import lp.boble.aubos.response.batch.BatchTransporter;
import lp.boble.aubos.service.book.BookService;
import lp.boble.aubos.service.book.dependencies.contributor.ContributorRoleService;
import lp.boble.aubos.service.book.dependencies.contributor.ContributorService;
import lp.boble.aubos.util.ValidationResult;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookContributorBatchService {

    private final BookContributorRepository bookContributorRepository;
    private final BookContributorMapper bookContributorMapper;
    private final BookContributorService bookContributorService;
    private final ContributorService contributorService;
    private final ContributorRoleService contributorRoleService;
    private final BookService bookService;

    //ADD
    public BatchTransporter<String> addContributorsToBook(UUID bookId, List<BookAddContributor> requests){
        //Validate
        ValidationResult<String, BookContributorModel> validationResult = this.validateCreateBatch(bookId, requests);

        //Persist
        this.persistBatch(validationResult.getValidRequests());

        return validationResult.getSuccessesAndFailures();
    }

    private ValidationResult<String, BookContributorModel> validateCreateBatch(UUID bookId, List<BookAddContributor> requests) {
        ValidationResult<String, BookContributorModel> validationResult = new ValidationResult<>();

        //Vou ignorar as requisições duplicadas, como tentar adicionar 10x o mesmo autor.
        Set<BookAddContributor> uniqueRequests = new HashSet<>(requests);
        //Comentei, n teve como, e se alguém voltar e sair no mesmo? É um cenário que dificilmente ocorrerá
        //Mas que ainda pode acontecer. :(

        List<UUID> requestContributorIds = uniqueRequests.stream().distinct().map(BookAddContributor::contributorId).toList();
        List<Integer> requestRoleIds = uniqueRequests.stream().distinct().map(BookAddContributor::contributorRoleId).toList();

        BookModel book = bookService.findBookOrThrow(bookId);

        Map<UUID, ContributorModel> mapRequestedContributors = contributorService.getRequestedContributors(requestContributorIds);
        Map<Integer, ContributorRole> mapRequestedRoles = contributorRoleService.getRequestedRoles(requestRoleIds);

        for(BookAddContributor request : uniqueRequests){
            UUID contributorId = request.contributorId();
            int roleId = request.contributorRoleId();

            String requestId = contributorId + ":" + request.hashCode();

            boolean contributorExists = mapRequestedContributors.containsKey(contributorId);
            boolean roleExists = mapRequestedRoles.containsKey(roleId);

            if(!contributorExists){
                validationResult.addFailure(requestId, "Contribuidor não encontrado.");
                continue;
            }

            if(!roleExists){
                validationResult.addFailure(requestId, "Role de  não encontrada.");
                continue;
            }

            BookContributorModel contributorToAdd = this.generateContributor(
                    book,
                    mapRequestedContributors.get(contributorId),
                    mapRequestedRoles.get(roleId), request);

            validationResult.addSuccess(requestId, "Contribuidor "+ mapRequestedRoles.get(roleId).getName() +" adicionado ao livro com sucesso.");
            validationResult.addValid(contributorToAdd);

        }

        return validationResult;
    }

    private BookContributorModel generateContributor(BookModel book, ContributorModel contributorModel, ContributorRole contributorRole, BookAddContributor request) {
        BookContributorModel contributor = new BookContributorModel();

        contributor.setBook(book);
        contributor.setContributor(contributorModel);
        contributor.setContributorRole(contributorRole);
        contributor.setStartDate(request.startDate());
        contributor.setEndDate(request.endDate());

        return contributor;
    }

    private void persistBatch(List<BookContributorModel> validRequests) {

        if(!validRequests.isEmpty()){
            bookContributorRepository.saveAll(validRequests);
        }
    }

    //UPDATE
    public BatchTransporter<UUID> updateBatch(UUID bookId, List<BookContributorUpdateBatchRequest> requests) {

        ValidationResult<UUID, BookContributorModel> validationResult = this.validateBatchUpdate(bookId, requests);

        this.persistBatch(validationResult.getValidRequests());

        return validationResult.getSuccessesAndFailures();
    }

    private ValidationResult<UUID, BookContributorModel> validateBatchUpdate(
            UUID bookId, List<BookContributorUpdateBatchRequest> requests) {

        ValidationResult<UUID, BookContributorModel> validationResult = new ValidationResult<>();

        Set<BookContributorUpdateBatchRequest> uniqueRequests = new HashSet<>(requests);

        List<UUID> bookContributorsId = uniqueRequests.stream().map(BookContributorUpdateBatchRequest::bookContributorId).toList();

        Map<UUID, BookContributorModel> currentAssociations = bookContributorService.getCurrentContributorsFromBook(bookId, bookContributorsId);

        for(BookContributorUpdateBatchRequest request: uniqueRequests){
            UUID associationId = request.bookContributorId();
            boolean hasAssociation = currentAssociations.containsKey(associationId);

            if(!hasAssociation){
                validationResult.addFailure(associationId, "Associação não encontrada.");
                continue;
            }

            BookContributorModel associated = currentAssociations.get(associationId);

            bookContributorMapper.updateBookContributor(associated, request);

            validationResult.addSuccess(associationId, "Associação atualizada com sucesso.");
            validationResult.addValid(associated);
        }

        return validationResult;
    }

    //DELETE
    public BatchTransporter<UUID> deleteBatch(UUID bookId, List<BookContributorDeleteRequest> requests) {

        ValidationResult<UUID, BookContributorModel> validationResult = this.validateBatchDelete(bookId, requests);

        this.persistBatchDelete(validationResult.getValidRequests());

        return validationResult.getSuccessesAndFailures();
    }

    private ValidationResult<UUID, BookContributorModel> validateBatchDelete(UUID bookId, List<BookContributorDeleteRequest> requests) {
        ValidationResult<UUID, BookContributorModel> validationResult = new ValidationResult<>();

        List<UUID> requestedAssociationIds = requests.stream().distinct().map(BookContributorDeleteRequest::bookContributorId).collect(Collectors.toList());

        Map<UUID, BookContributorModel> mapRequestedAssociation = bookContributorService.getCurrentContributorsFromBook(bookId, requestedAssociationIds);

        for(BookContributorDeleteRequest request: requests){
            UUID associationId = request.bookContributorId();
            boolean hasAssociation = mapRequestedAssociation.containsKey(associationId);

            if(!hasAssociation){
                validationResult.addFailure(associationId, "Associação não encontrada.");
                continue;
            }

            validationResult.addSuccess(associationId, "Associação removida com sucesso.");
            validationResult.addValid(mapRequestedAssociation.get(associationId));
        }

        return validationResult;
    }

    private void persistBatchDelete(List<BookContributorModel> validRequests) {
        if(!validRequests.isEmpty()){
            bookContributorRepository.deleteAll(validRequests);
        }
    }

}
