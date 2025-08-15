package lp.boble.aubos.dto.book.family;

import lp.boble.aubos.model.book.family.FamilyType;
import lp.boble.aubos.model.book.family.Visibility;

public record FamilyData(
        FamilyType type,
        Visibility visibility
) {}
