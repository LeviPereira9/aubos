package lp.boble.aubos.service.book.relationships;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.parts.BookAddContributor;
import lp.boble.aubos.dto.book.relationships.BookContributor.BookContributorKey;
import lp.boble.aubos.dto.book.relationships.BookContributor.BookContributorUpdateBatchRequest;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.dependencies.ContributorModel;
import lp.boble.aubos.model.book.dependencies.ContributorRole;
import lp.boble.aubos.model.book.relationships.BookContributorModel;
import lp.boble.aubos.repository.book.relationships.BookContributorRepository;
import lp.boble.aubos.response.batch.BatchTransporter;
import lp.boble.aubos.service.book.BookService;
import lp.boble.aubos.service.book.dependencies.ContributorRoleService;
import lp.boble.aubos.service.book.dependencies.ContributorService;
import lp.boble.aubos.util.ValidationResult;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BookContributorBatchService {

    private final BookContributorRepository bookContributorRepository;
    private final BookContributorService bookContributorService;
    private final ContributorService contributorService;
    private final ContributorRoleService contributorRoleService;
    private final BookService bookService;

    //ADD
    public BatchTransporter<UUID> addContributorsToBook(UUID bookId, List<BookAddContributor> requests){
        //Validate
        ValidationResult<BookAddContributor> validationResult = this.validateContributor(bookId, requests);

        //Arrange
        List<BookContributorModel> contributorsToAdd = this.generateContributorModel(bookId, validationResult);

        //Persist
        bookContributorRepository.saveAll(contributorsToAdd);

        return validationResult.getSuccessesAndFailures();
    }

    private ValidationResult<BookAddContributor> validateContributor(UUID bookId, List<BookAddContributor> requests) {
        ValidationResult<BookAddContributor> validationResult = new ValidationResult<>();

        //Vou ignorar as requisições duplicadas, como tentar adicionar 10x o mesmo autor. >:D
        List<BookAddContributor> uniqueRequests = requests.stream().distinct().toList();

        List<BookContributorModel> bookContributors = bookContributorService.findContributorsByBookOrThrow(bookId);

        Map<UUID, List<Integer>> mapByContributor = bookContributors.stream()
                .collect(Collectors.groupingBy(BookContributorModel::getContributorId, Collectors.mapping(BookContributorModel::getContributorRoleId, Collectors.toList())));

        Set<UUID> existingContributors = contributorService.getAllContributorsId(
                uniqueRequests.stream().map(BookAddContributor::contributorId).toList());

        Set<Integer> existingRoles = contributorRoleService.getAllRolesId(
                uniqueRequests.stream().map(BookAddContributor::contributorRoleId).toList());

        for(BookAddContributor request : uniqueRequests){
            UUID contributorId = request.contributorId();
            int roleId = request.contributorRoleId();

            List<Integer> roles = mapByContributor.getOrDefault(contributorId, Collections.emptyList());

            boolean contributorExists = existingContributors.contains(contributorId);
            boolean roleExists = existingRoles.contains(roleId);

            if(!contributorExists){
                validationResult.addFailure(contributorId, "Contribuidor não encontrado.");
                continue;
            }

            if(!roleExists){
                validationResult.addFailure(contributorId, "Role de  não encontrada.");
                continue;
            }

            if(roles.contains(roleId)){
                validationResult.addFailure(contributorId, "O contribuidor já possui essa role.");
                continue;
            }

            validationResult.addValid(request);

        }

        return validationResult;
    }

    public List<BookContributorModel> generateContributorModel(
        UUID bookId,
        ValidationResult<BookAddContributor> validationResult) {

        List<BookContributorModel> toAdd = new ArrayList<>();

        List<BookAddContributor> requests = validationResult.getValidRequests();

        BookModel book = bookService.findBookOrThrow(bookId);

        Set<UUID> contributorsId = requests.stream().map(BookAddContributor::contributorId).collect(Collectors.toSet());
        Set<Integer> rolesId = requests.stream().map(BookAddContributor::contributorRoleId).collect(Collectors.toSet());

        List<ContributorModel> contributors = contributorService.findAllContributorsById(contributorsId);
        List<ContributorRole> roles = contributorRoleService.findAllContributorRoles(rolesId);

        Map<UUID, ContributorModel> mapContributors = contributors.stream()
                .collect(Collectors.toMap(ContributorModel::getId, Function.identity()));

        Map<Integer, ContributorRole> mapRoles = roles.stream()
                .collect(Collectors.toMap(ContributorRole::getId, Function.identity()));

        for(BookAddContributor request : requests){
            BookContributorModel bookContributor = new BookContributorModel();
            bookContributor.setBook(book);
            bookContributor.setContributor(mapContributors.get(request.contributorId()));
            bookContributor.setContributorRole(mapRoles.get(request.contributorRoleId()));

            toAdd.add(bookContributor);
            validationResult.addSuccess(request.contributorId(), "Contribuidor adicionado ao livro com sucesso.");
        }

        return toAdd;
    }


    //UPDATE
    public BatchTransporter<UUID> updateBatch(UUID bookId, List<BookContributorUpdateBatchRequest> requests) {

        List<BookContributorModel> currentBookContributors = bookContributorService.findContributorsByBookOrThrow(bookId);

        ValidationResult<BookContributorUpdateBatchRequest> validationResult = this.processBatchUpdate(requests, currentBookContributors);

        bookContributorRepository.saveAll(currentBookContributors);

        return validationResult.getSuccessesAndFailures();
    }


    private ValidationResult<BookContributorUpdateBatchRequest> processBatchUpdate(
            List<BookContributorUpdateBatchRequest> requests, List<BookContributorModel> currentBookContributors
    ){

        List<BookContributorUpdateBatchRequest> uniqueRequests = requests.stream().distinct().toList();


        Set<UUID> requestContributorIds = uniqueRequests.stream().map(BookContributorUpdateBatchRequest::contributorId).collect(Collectors.toSet());
        Set<Integer> requestRoleIds = uniqueRequests.stream().flatMap(r -> Stream.of(r.toRoleId(), r.fromRoleId())).collect(Collectors.toSet());

        List<ContributorModel> contributors = contributorService.findAllContributorsById(requestContributorIds);
        List<ContributorRole> roles = contributorRoleService.findAllContributorRoles(requestRoleIds);

        List<Integer> existingRoles = roles.stream().map(ContributorRole::getId).toList();
        List<UUID> existingContributors = contributors.stream().map(ContributorModel::getId).toList();

        Map<Integer, ContributorRole> mapRoles = roles.stream().collect(Collectors.toMap(ContributorRole::getId, Function.identity()));

        Map<BookContributorKey, BookContributorModel> currentAssociations = currentBookContributors.stream().collect(
                Collectors.toMap(c -> new BookContributorKey(c.getContributorId(), c.getContributorRoleId()), Function.identity())
        );

        ValidationResult<BookContributorUpdateBatchRequest> validationResult = validateRequests(uniqueRequests, existingContributors, existingRoles, currentAssociations);

        this.applyUpdates(validationResult, currentAssociations, mapRoles);

        return validationResult;
    }

    private ValidationResult<BookContributorUpdateBatchRequest> validateRequests(
            List<BookContributorUpdateBatchRequest> uniqueRequests,
            List<UUID> existingContributors,
            List<Integer> existingRoles,
            Map<BookContributorKey, BookContributorModel> currentAssociations) {
        ValidationResult<BookContributorUpdateBatchRequest> validationResult = new ValidationResult<>();

        for(BookContributorUpdateBatchRequest request : uniqueRequests){
            UUID contributorId = request.contributorId();
            int fromRoleId = request.fromRoleId();
            int toRoleId = request.toRoleId();

            if(fromRoleId == toRoleId){
                validationResult.addSuccess(contributorId, "Role não alterada.");
                continue;
            }

            if(!existingContributors.contains(contributorId)){
                validationResult.addFailure(contributorId, "Contribuidor não encontrado.");
                continue;
            }

            if(!existingRoles.contains(toRoleId)){
                validationResult.addFailure(contributorId, "Role de destino não encontrada.");
                continue;
            }

            if(!existingRoles.contains(fromRoleId)){
                validationResult.addFailure(contributorId, "Role de origem não encontrada.");
                continue;
            }

            if(!currentAssociations.containsKey(new BookContributorKey(contributorId, fromRoleId))){
                validationResult.addFailure(contributorId, "Esse contribuidor não está associado a essa role.");
                continue;
            }

            if(currentAssociations.containsKey(new BookContributorKey(contributorId, toRoleId))){
                validationResult.addFailure(contributorId, "Esse contribuidor já possui esta role.");
                continue;
            }

            validationResult.addValid(request);
        }

        return validationResult;
    }

    private void applyUpdates(
            ValidationResult<BookContributorUpdateBatchRequest> validationResult,
            Map<BookContributorKey, BookContributorModel> currentAssociations,
            Map<Integer, ContributorRole> mapRoles) {
        for(BookContributorUpdateBatchRequest request: validationResult.getValidRequests()){
            BookContributorKey contributorKey = new BookContributorKey(request.contributorId(), request.fromRoleId());

            BookContributorModel original = currentAssociations.get(contributorKey);

            if(original != null){
                original.setContributorRole(mapRoles.get(request.toRoleId()));
                validationResult.addSuccess(request.contributorId(), "Atualização realizada com sucesso.");
            } else {
                validationResult.addFailure(request.contributorId(), "Falha em localizar o contribuidor.");
            }

        }

    }

    //DELETE
    public BatchTransporter<UUID> deleteBatch(UUID bookId, List<UUID> bookContributorIds) {

        ValidationResult<UUID> validationResult = new ValidationResult<>();

        List<UUID> uniqueRequests = bookContributorIds.stream().distinct().collect(Collectors.toList());

        List<BookContributorModel> bookContributorsToDelete = this.findAllContributorsOnBook(bookId, uniqueRequests);

        Set<UUID> existingIds = bookContributorsToDelete.stream().map(BookContributorModel::getId).collect(Collectors.toSet());

        for(UUID request: uniqueRequests){
            if(!existingIds.contains(request)){
                validationResult.addFailure(request, "Esse contribuidor não está associado a este livro.");
                continue;
            }

            validationResult.addValid(request);
        }

        if(!validationResult.getValidRequests().isEmpty()){
            bookContributorRepository.deleteAll(bookContributorsToDelete);

            for (UUID validId: validationResult.getValidRequests()){
                validationResult.addSuccess(validId, "Contribuidor removido com sucesso.");
            }
        }

        return validationResult.getSuccessesAndFailures();
    }

    private List<BookContributorModel> findAllContributorsOnBook(UUID bookId, List<UUID> uniqueRequests) {
        List<BookContributorModel> contributors = bookContributorRepository.findAllByIdIn(bookId, uniqueRequests);

        if(contributors.isEmpty()){
            throw CustomNotFoundException.bookContributor();
        }

        return contributors;
    }


}
