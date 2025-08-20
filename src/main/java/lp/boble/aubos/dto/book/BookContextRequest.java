package lp.boble.aubos.dto.book;

public record BookContextRequest (
        int languageId,
        int typeId,
        int statusId,
        int restrictionId,
        int licenseId
){}
