package lp.boble.aubos.dto.book.relationships;

import java.util.UUID;

public record BookFamilyUpdateBatchRequest (
        UUID bookFamilyId,
        int order
){
}
