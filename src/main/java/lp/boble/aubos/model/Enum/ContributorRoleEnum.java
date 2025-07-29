package lp.boble.aubos.model.Enum;

public enum ContributorRoleEnum {
    AUTHOR(1),
    ILLUSTRATOR(2),
    EDITOR(3),
    PUBLISHER(4);

    private final int id;

    ContributorRoleEnum(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
