package lp.boble.aubos.service.book.relationships;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.relationships.BookFamilyCreateRequest;
import lp.boble.aubos.dto.book.relationships.BookFamilyUpdateRequest;
import lp.boble.aubos.exception.custom.auth.CustomForbiddenActionException;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
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

import java.time.Instant;
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

    public void addBookToFamily(UUID familyId, BookFamilyCreateRequest request) {

        BookFamilyModel bookToAdd = new BookFamilyModel();
        bookToAdd.setBook(bookService.findBookOrThrow(request.bookId()));
        bookToAdd.setFamily(familyService.findFamilyOrThrow(familyId));
        bookToAdd.setNote(request.note());
        bookToAdd.setOrderInFamily(request.order());
        bookToAdd.setCreatedBy(authUtil.getRequester());

        bookFamilyRepository.save(bookToAdd);
    }

    /*
    * public BatchTransporter<UUID> addBooksToFamily(UUID familyId, List<BookFamilyCreateRequest> requests) {

        List<BatchContent<UUID>> successes = new ArrayList<>();
        List<BatchContent<UUID>> failures = new ArrayList<>();

        for (BookFamilyCreateRequest request : requests) {
            try{
                this.addBookToFamily(familyId, request);
                successes.add(BatchContent.success(request.bookId(), "Livro adicionado com sucesso."));
            } catch (CustomNotFoundException e){
                String message = e.getMessage().contains("Livro") ? "Livro não encontrado": "Coleção não encontrada.";
                failures.add(BatchContent.failure(request.bookId(), message));
            } catch (DataIntegrityViolationException e){
                failures.add(BatchContent.failure(request.bookId(), "Ordem do livro duplicada."));
            }
        }

        return new BatchTransporter<>(successes, failures);
    }*/

    public BatchTransporter<UUID> addBooksToFamily(UUID familyId, List<BookFamilyCreateRequest> requests) {

        List<BatchContent<UUID>> successes = new ArrayList<>();
        List<BatchContent<UUID>> failures = new ArrayList<>();

        int bigOrder = 0;

        FamilyModel family = familyService.findFamilyOrThrow(familyId);

        List<BookFamilyModel> currentFamily = this.findAllBooksInFamily(familyId);
        List<BookFamilyModel> toAdd = new ArrayList<>();

        // TODO: Quando a ordem estiver errada, talvez seja melhor adicionar no final
        // NOTE: Ex.: Se tiver adicionando e já tiver coisas na coleção, ele simplesmente adiciona no final.
        // NOTE: Agora, se estiver errada e você errou na primeira requisição, não vai ser adicionado mesmo.

        // Ordens em uso
        Set<Integer> ordersInUse = currentFamily.stream()
                .map(BookFamilyModel::getOrderInFamily)
                .collect(Collectors.toSet());

        // Livros que já estão na coleção
        Set<UUID> booksOnFamily = currentFamily.stream()
                .map( c -> c.getBook().getId())
                .collect(Collectors.toSet());

        for (BookFamilyCreateRequest request : requests) {
            UUID bookId = request.bookId();
            int order = request.order();

            boolean orderConflict = ordersInUse.contains(order);
            boolean bookConflict = booksOnFamily.contains(bookId);
            boolean bookDontExist = !bookRepository.existsById(bookId);

            if(!bookConflict && !bookDontExist) {

                if(orderConflict){
                    order = Collections.max(ordersInUse) + 1;
                    ordersInUse.add(order);
                }

                BookFamilyModel bookToAdd = new BookFamilyModel();
                bookToAdd.setBook(bookService.findBookOrThrow(bookId));
                bookToAdd.setFamily(family);
                bookToAdd.setNote(request.note());
                bookToAdd.setOrderInFamily(order);
                bookToAdd.setCreatedBy(authUtil.getRequester());
                toAdd.add(bookToAdd);
                successes.add(BatchContent.success(bookId, "Livro adicionado com sucesso."));
            } else {
                StringBuilder errorMessage = new StringBuilder();
                if(bookDontExist) errorMessage.append("Livro não encontrado. ");
                if(bookConflict) errorMessage.append("Este livro já está na coleção.");
                failures.add(BatchContent.failure(bookId, errorMessage.toString().trim()));
            }
        }

        bookFamilyRepository.saveAll(toAdd);

        return new BatchTransporter<>(successes, failures);
    }



    public void updateBookFamily(UUID bookFamilyId, BookFamilyUpdateRequest request) {
        BookFamilyModel bookToUpdate = this.findBookFamilyOrThrow(bookFamilyId);

        bookToUpdate.setOrderInFamily(request.order());
        bookToUpdate.setNote(request.note());
        bookToUpdate.setUpdatedBy(authUtil.getRequester());
        bookToUpdate.setLastUpdate(Instant.now());

        bookFamilyRepository.save(bookToUpdate);
    }

    @Transactional
    public BatchTransporter<UUID> updateBookFamilies(UUID familyId, List<BookFamilyUpdateRequest> requests) {
        List<BatchContent<UUID>> successes = new ArrayList<>();
        List<BatchContent<UUID>> failures = new ArrayList<>();

        List<BookFamilyModel> currentFamily = this.findAllBooksInFamily(familyId);

        this.validateUpdateBookFamily(currentFamily, requests);
        this.prepareToBatchUpdate(currentFamily);

        List<BookFamilyModel> toUpdate = new ArrayList<>();

        Map<UUID, BookFamilyModel> currentFamilyMap = currentFamily.stream()
                .collect(Collectors.toMap(b -> b.getBook().getId(), Function.identity()));

        for(BookFamilyUpdateRequest request : requests) {
            UUID bookId = request.bookId();

            BookFamilyModel bookToUpdate = currentFamilyMap.get(bookId);

            if(bookToUpdate == null) {
                throw new RuntimeException("Coleção de livros inválida.");
            }

            bookToUpdate.setNote(request.note());
            bookToUpdate.setOrderInFamily(request.order());
            bookToUpdate.setUpdatedBy(authUtil.getRequester());
            bookToUpdate.setLastUpdate(Instant.now());
            toUpdate.add(bookToUpdate);
            successes.add(BatchContent.success(request.bookId(), "Livro atualizado com sucesso."));
        }

        bookFamilyRepository.saveAll(toUpdate);

        return new BatchTransporter<>(successes, failures);
    }

    private void prepareToBatchUpdate(List<BookFamilyModel> currentFamily) {
        // Como temos constraints para garantir a integridade do BD, precisamos mudar os valores para que caso haja troca de posições entre os livros, elas ocorram sem que o BD fique triste.
        currentFamily.forEach(b ->
                b.setOrderInFamily(-1 * b.getOrderInFamily()));

        bookFamilyRepository.saveAll(currentFamily);
        bookRepository.flush();
    }

    private void validateUpdateBookFamily(List<BookFamilyModel> currentFamily, List<BookFamilyUpdateRequest> requests) {
        Map<UUID, Integer> currentBookOrder = currentFamily.stream()
                .collect(Collectors.toMap(
                        c -> c.getBook().getId(),
                        BookFamilyModel::getOrderInFamily));

        // Sets para detectar requisições com duplicatas
        Set<UUID> toUpdateBooks = new HashSet<>();
        Set<Integer> toUpdateOrders = new HashSet<>();

        for(BookFamilyUpdateRequest request : requests) {
            UUID bookId = request.bookId();

            // Livro não pertence a coleção.
            if (!currentBookOrder.containsKey(bookId)) {
                throw new RuntimeException("Um livro não pertence a essa coleção.");
            }

            // Livro duplicado
            if (!toUpdateBooks.add(bookId)) {
                throw new RuntimeException("Livro duplicado na requisição");
            }

            // Ordem duplicado
            if (!toUpdateOrders.add(request.order())) {
                throw new RuntimeException("Posição duplicada na requisição");
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



    public void removeBookFromFamily(UUID bookFamilyId) {
        bookFamilyRepository.deleteById(bookFamilyId);
    }

    public void removeBooksFromFamily(List<UUID> bookFamiliesId) {
        for (UUID bookFamilyId : bookFamiliesId) {

        }
    }

    public BookFamilyModel findBookFamilyOrThrow(UUID bookFamilyId) {
        return bookFamilyRepository.findById(bookFamilyId).orElseThrow();
    }

    public List<BookFamilyModel> findAllBooksInFamily(UUID familyId){
        return bookFamilyRepository.findAllByFamilyId(familyId);
    }
}
