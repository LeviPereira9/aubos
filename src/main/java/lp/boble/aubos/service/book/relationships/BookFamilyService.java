package lp.boble.aubos.service.book.relationships;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.relationships.BookFamilyCreateRequest;
import lp.boble.aubos.dto.book.relationships.BookFamilyDeleteRequest;
import lp.boble.aubos.dto.book.relationships.BookFamilyResponse;
import lp.boble.aubos.dto.book.relationships.BookFamilyUpdateRequest;
import lp.boble.aubos.exception.custom.global.CustomDuplicateFieldException;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.mapper.book.family.BookFamilyMapper;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.family.FamilyModel;
import lp.boble.aubos.model.book.relationships.BookFamilyModel;
import lp.boble.aubos.repository.book.BookRepository;
import lp.boble.aubos.repository.book.relationships.BookFamilyRepository;
import lp.boble.aubos.response.batch.BatchContent;
import lp.boble.aubos.response.batch.BatchTransporter;
import lp.boble.aubos.service.book.BookService;
import lp.boble.aubos.service.book.family.FamilyService;
import lp.boble.aubos.util.AuthUtil;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookFamilyService {
    private final BookFamilyRepository bookFamilyRepository;
    private final BookService bookService;
    private final FamilyService familyService;
    private final AuthUtil authUtil;
    private final BookRepository bookRepository;
    private final BookFamilyMapper bookFamilyMapper;

    @Transactional
    public BookFamilyResponse addBookToFamily(UUID familyId, BookFamilyCreateRequest request) {

        boolean bookConflict = bookFamilyRepository.existsByFamilyIdAndBookId(familyId, request.bookId());
        boolean orderConflict = bookFamilyRepository.existsByFamilyIdAndOrderInFamily(familyId, request.order());

        if(bookConflict) throw CustomDuplicateFieldException.bookFamily();
        if(orderConflict) throw CustomDuplicateFieldException.orderFamily();

        BookModel bookToAdd = bookService.findBookOrThrow(request.bookId());
        FamilyModel destinationFamily = familyService.findFamilyOrThrow(familyId);

        BookFamilyModel bookFamilyToCreate = bookFamilyMapper.fromCreateRequestToModel(request, bookToAdd, destinationFamily);
        bookFamilyToCreate.setCreatedBy(authUtil.getRequester());

        return bookFamilyMapper.fromModelToResponse(bookFamilyRepository.save(bookFamilyToCreate));
    }

    @Transactional
    public BatchTransporter<UUID> addBooksToFamily(UUID familyId, List<BookFamilyCreateRequest> requests) {

        List<BatchContent<UUID>> successes = new ArrayList<>();
        List<BatchContent<UUID>> failures = new ArrayList<>();

        FamilyModel destinationFamily = familyService.findFamilyOrThrow(familyId);

        List<BookFamilyModel> currentBooksInFamily = this.findAllBooksInFamily(familyId);
        List<BookFamilyModel> bookFamiliesToAdd = new ArrayList<>();

        // Ordens em uso
        Set<Integer> ordersInUse = currentBooksInFamily.stream()
                .map(BookFamilyModel::getOrderInFamily)
                .collect(Collectors.toSet());

        // Livros que já estão na coleção
        Set<UUID> booksIdOnCurrentFamily = currentBooksInFamily.stream()
                .map( c -> c.getBook().getId())
                .collect(Collectors.toSet());

        for (BookFamilyCreateRequest request : requests) {
            UUID bookId = request.bookId();
            int order = request.order();

            boolean orderConflict = ordersInUse.contains(order);
            boolean bookConflict = booksIdOnCurrentFamily.contains(bookId);
            boolean bookDontExist = !bookRepository.existsById(bookId);

            if(!bookConflict && !bookDontExist) {

                if(orderConflict){
                    order = Collections.max(ordersInUse) + 1;
                    ordersInUse.add(order);
                }
                BookModel bookToAdd = bookService.findBookOrThrow(bookId);

                BookFamilyModel bookFamilyToAdd = bookFamilyMapper.fromCreateRequestToModel(request, bookToAdd, destinationFamily);
                bookFamilyToAdd.setCreatedBy(authUtil.getRequester());

                bookFamiliesToAdd.add(bookFamilyToAdd);

                successes.add(BatchContent.success(bookId, "Livro adicionado com sucesso."));
            } else {
                StringBuilder errorMessage = new StringBuilder();
                if(bookDontExist) errorMessage.append("Livro não encontrado. ");
                if(bookConflict) errorMessage.append("Este livro já está na coleção.");
                failures.add(BatchContent.failure(bookId, errorMessage.toString().trim()));
            }
        }

        bookFamilyRepository.saveAll(bookFamiliesToAdd);

        return new BatchTransporter<>(successes, failures);
    }


    @Transactional
    public BookFamilyResponse updateBookFamily(UUID familyId, BookFamilyUpdateRequest request) {
        boolean bookConflict = !bookFamilyRepository.existsByFamilyIdAndBookId(familyId, request.bookId());
        boolean orderConflict = bookFamilyRepository.existsByFamilyIdAndOrderInFamily(familyId, request.order());

        if(orderConflict) throw CustomDuplicateFieldException.orderFamily();
        if(bookConflict) throw CustomNotFoundException.bookFamily();

        BookFamilyModel bookFamilyToUpdate = this.findByFamilyAndBookOrThrow(familyId, request.bookId());

        bookFamilyMapper.toUpdateFromRequest(bookFamilyToUpdate, request);
        bookFamilyToUpdate.setUpdatedBy(authUtil.getRequester());

        return bookFamilyMapper.fromModelToResponse(bookFamilyRepository.save(bookFamilyToUpdate));
    }

    @Transactional
    public BatchTransporter<UUID> updateBookFamilies(UUID familyId, List<BookFamilyUpdateRequest> requests) {
        List<BatchContent<UUID>> successes = new ArrayList<>();
        List<BatchContent<UUID>> failures = new ArrayList<>();

        List<BookFamilyModel> currentBooksInFamily = this.findAllBooksInFamily(familyId);

        this.validateUpdateBookFamily(currentBooksInFamily, requests);
        this.prepareOrdersToBatchUpdate(currentBooksInFamily);

        List<BookFamilyModel> booksInFamilyToUpdate = new ArrayList<>();

        Map<UUID, BookFamilyModel> currentFamilyMembersMap = currentBooksInFamily.stream()
                .collect(Collectors.toMap(b -> b.getBook().getId(), Function.identity()));

        for(BookFamilyUpdateRequest request : requests) {
            UUID bookId = request.bookId();

            BookFamilyModel bookFamilyToUpdate = currentFamilyMembersMap.get(bookId);

            if(bookFamilyToUpdate == null) {
                throw CustomNotFoundException.bookFamily();
            }

            bookFamilyMapper.toUpdateFromRequest(bookFamilyToUpdate, request);

            bookFamilyToUpdate.setUpdatedBy(authUtil.getRequester());
            booksInFamilyToUpdate.add(bookFamilyToUpdate);
            successes.add(BatchContent.success(request.bookId(), "Livro atualizado com sucesso."));
        }

        bookFamilyRepository.saveAll(booksInFamilyToUpdate);

        return new BatchTransporter<>(successes, failures);
    }

    private void prepareOrdersToBatchUpdate(List<BookFamilyModel> currentBookFamilyMembers) {
        // Como temos constraints para garantir a integridade do BD, precisamos mudar os valores para que caso haja troca de posições entre os livros, elas ocorram sem que o BD fique triste.
        currentBookFamilyMembers.forEach(b ->
                b.setOrderInFamily(-1 * b.getOrderInFamily()));

        bookFamilyRepository.saveAll(currentBookFamilyMembers);
        bookRepository.flush();
    }

    private void validateUpdateBookFamily(List<BookFamilyModel> currentBookFamilyMembers, List<BookFamilyUpdateRequest> requests) {
        Map<UUID, Integer> currentBookOrderByBookId = currentBookFamilyMembers.stream()
                .collect(Collectors.toMap(
                        c -> c.getBook().getId(),
                        BookFamilyModel::getOrderInFamily));

        // Sets para detectar requisições com duplicatas
        Set<UUID> toUpdateBooks = new HashSet<>();
        Set<Integer> toUpdateOrders = new HashSet<>();

        for(BookFamilyUpdateRequest request : requests) {
            UUID bookId = request.bookId();

            // Livro não pertence a coleção.
            if (!currentBookOrderByBookId.containsKey(bookId)) {
                throw CustomNotFoundException.bookFamily();
            }

            // Livro duplicado
            if (!toUpdateBooks.add(bookId)) {
                throw CustomDuplicateFieldException.bookFamily();
            }

            // Ordem duplicado
            if (!toUpdateOrders.add(request.order())) {
                throw CustomDuplicateFieldException.orderFamily();
            }
        }
    }

    /*public BatchTransporter<UUID> updateBookFamilyOrders(UUID familyId, List<BookFamilyUpdateRequest> requests) {
        List<BatchContent<UUID>> successes = new ArrayList<>();
        List<BatchContent<UUID>> failures = new ArrayList<>();

        // NOTE: Validar os swaps, pois oq pode ocorrer:
        // NOTE: A = 1, B = 2, C = 3
        // NOTE: Req = A > 2, B > 3
        // NOTE: A vai conseguir, pq B tbm está na req de troca, mas o B quer ir pro C, só que o C n quer trocar
        // NOTE: Ai o B vai falhar em trocar pq o C n quer e então o A vai dar um erro lá no MySql
        List<BookFamilyModel> toUpdate = new ArrayList<>();

        Map<UUID, Integer> requestMap = requests.stream()
                .collect(Collectors.toMap(BookFamilyUpdateRequest::bookId, BookFamilyUpdateRequest::order));



        // Coleção atual
        List<BookFamilyModel> currentFamily = this.findAllBooksInFamily(familyId);

        //Map para validações
        // Livro > Ordem
        Map<UUID, Integer> currentBookOrder = currentFamily.stream()
                .collect(Collectors.toMap(b -> b.getBook().getId(), BookFamilyModel::getOrderInFamily));
        // Ordem > Livro
        Map<Integer, UUID> currentOrderBook = currentFamily.stream()
                .collect(Collectors.toMap(BookFamilyModel::getOrderInFamily, b -> b.getBook().getId()));
        // Livro > Registro na coleção.
        Map<UUID, BookFamilyModel> currentFamilyMap = currentFamily.stream()
                .collect(Collectors.toMap(b -> b.getBook().getId(), Function.identity()));

        // Sets para detectar requisições com duplicatas
        Set<UUID> toUpdateBooks = new HashSet<>();
        Set<Integer> toUpdateOrders = new HashSet<>();


        for(BookFamilyUpdateRequest request : requests) {
            UUID bookId = request.bookId();
            int newOrder = request.order();

            // Livro não pertence a coleção.
            if(!currentBookOrder.containsKey(bookId)) {
                failures.add(BatchContent.failure(bookId,"Livro não pertence a essa coleção."));
                continue;
            }

            // Livro duplicado
            if(!toUpdateBooks.add(bookId)) {
                failures.add(BatchContent.failure(bookId, "Livro duplicado na requisição"));
                continue;
            }

            // Ordem duplicado
            if(!toUpdateOrders.add(request.order())) {
                failures.add(BatchContent.failure(bookId, "Posição duplicada na requisição"));
                continue;
            }

            // Posição antiga (atual)
            int oldOrder = currentBookOrder.get(bookId);

            // Verifica se teve mudança.
            // newOrder = requisição.order
            if(oldOrder != newOrder) {
                // Pega o livro na coleção atual que está ocupando a nova posição desejada
                UUID bookTarget = currentOrderBook.get(newOrder);

                // Não pode ser estar vázio.
                if(bookTarget == null) {
                    failures.add(BatchContent.failure(bookId,"Posição " + newOrder + " é inválida."));
                    continue;
                }

                // Verifica se o livro que queremos trocar de posição está na requisição também.
                boolean otherInRequest = requests.stream()
                        .anyMatch( r -> r.bookId().equals(bookTarget));

                if(!bookTarget.equals(request.bookId()) && !otherInRequest) {
                    failures.add(BatchContent.failure(bookId, "Posição " + newOrder + " já pertence a outro livro e ele não está na requisição para troca."));
                    continue;
                }
            }

            // Atualiza

            BookFamilyModel bookToUpdate = currentFamilyMap.get(bookId);

            if(bookToUpdate == null) {
                failures.add(BatchContent.failure(bookId, "Livro não encontrado para atualização."));
                continue;
            }
            bookToUpdate.setOrderInFamily(request.order());
            bookToUpdate.setNote(request.note());
            bookToUpdate.setUpdatedBy(authUtil.getRequester());
            bookToUpdate.setLastUpdate(Instant.now());
            toUpdate.add(bookToUpdate);
            successes.add(BatchContent.success(request.bookId(), "Livro atualizado com sucesso."));
        }

        if(!toUpdate.isEmpty()){
            bookFamilyRepository.saveAll(toUpdate);
        }

        return new BatchTransporter<>(successes, failures);
    }*/



    @Transactional
    public void removeBookFromFamily(UUID familyId, BookFamilyDeleteRequest deleteRequest) {

        UUID bookId = deleteRequest.bookId();

        boolean bookFamilyExists = bookFamilyRepository.existsByFamilyIdAndBookId(familyId, bookId);

        if(!bookFamilyExists){
            throw CustomNotFoundException.book();
        }

        bookFamilyRepository.deleteByFamilyIdAndBookId(familyId, bookId);
    }

    @Transactional
    public BatchTransporter<UUID> removeBooksFromFamily(UUID familyId, List<BookFamilyDeleteRequest> deleteRequests) {
        List<BatchContent<UUID>> successes = new ArrayList<>();
        List<BatchContent<UUID>> failures = new ArrayList<>();

        List<BookFamilyModel> currenFamilyMembers = this.findAllBooksInFamily(familyId);
        List<BookFamilyModel> bookFamiliesToRemove = new ArrayList<>();

        Map<UUID, BookFamilyModel> currentFamilyMembersByBookId = currenFamilyMembers.stream()
                .collect(Collectors.toMap(b -> b.getBook().getId(), Function.identity()));

        Set<UUID> booksToRemoveFromFamily = new HashSet<>();

        for(BookFamilyDeleteRequest deleteRequest : deleteRequests) {
            UUID bookId = deleteRequest.bookId();

            if(!booksToRemoveFromFamily.add(bookId)) {
                failures.add(BatchContent.failure(bookId, "Livro duplicado na requisição"));
                continue;
            }

            if(!currentFamilyMembersByBookId.containsKey(bookId)) {
                failures.add(BatchContent.failure(bookId, "Livro não pertence a essa coleção"));
                continue;
            }

            bookFamiliesToRemove.add(currentFamilyMembersByBookId.get(bookId));
            successes.add(BatchContent.success(bookId, "Livro removido com sucesso."));
        }

        bookFamilyRepository.deleteAll(bookFamiliesToRemove);

        return new BatchTransporter<>(successes, failures);
    }

    public BookFamilyModel findByFamilyAndBookOrThrow(UUID familyId, UUID bookId){

        return bookFamilyRepository.findByFamilyIdAndBookId(familyId, bookId)
                .orElseThrow(CustomNotFoundException::bookFamily);
    }

    public BookFamilyModel findBookFamilyOrThrow(UUID bookFamilyId) {
        return bookFamilyRepository.findById(bookFamilyId).orElseThrow();
    }

    public List<BookFamilyModel> findAllBooksInFamily(UUID familyId){
        return bookFamilyRepository.findAllByFamilyId(familyId);
    }

}
