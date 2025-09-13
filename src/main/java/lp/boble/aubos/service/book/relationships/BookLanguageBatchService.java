package lp.boble.aubos.service.book.relationships;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookLanguageBatchService {
    // --- Batch
    // Adicionar diversas linguagens
    // Todas para um único livro, que deve existir -> BookService.findBook()
    // As linguagens devem existir tbm, Set -> FindAllById.
    // Se já não estão no livro -> FindAllBookLanguagesByBookId.

    // Update diversas linguagens
    // Pegas todas do livro -> FindAll...
    // Pelo List, a gente checa, pera não têm update nisso aqui não. É só remover e criar dnv. Não?

    // Para todos do batch: Distinct em cima das request, não quero tratar nenhuma duplicata igual no ReOrder, pq lá era infelizmente,
    // Necessário. :(
}
