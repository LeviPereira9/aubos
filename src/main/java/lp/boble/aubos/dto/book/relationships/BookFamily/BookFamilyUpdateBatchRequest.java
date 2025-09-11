package lp.boble.aubos.dto.book.relationships.BookFamily;

import java.util.UUID;

public record BookFamilyUpdateBatchRequest (
        UUID bookFamilyId,
        int order
){
}
