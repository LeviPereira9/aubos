package lp.boble.aubos.model.Enum;

import lombok.Getter;

@Getter
public enum VisiblityEnum {
    PUBLIC(1),
    LINK_ONLY(3);

    private final int id;

    VisiblityEnum(int id) {
        this.id = id;
    }
}
