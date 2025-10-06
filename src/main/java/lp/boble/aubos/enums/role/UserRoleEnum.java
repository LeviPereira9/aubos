package lp.boble.aubos.enums.role;

import lombok.Data;
import lombok.Getter;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;

@Getter
public enum UserRoleEnum {
    READER(1,"READER"),
    AUTHOR(2, "AUTHOR"),
    TRANSLATOR(3, "TRANSLATOR"),
    MOD(4, "MOD"),
    ADMIN(5, "ADMIN"),
    SUPER_ADMIN(6, "SUPER_ADMIN");

    private final int hierarchyLevel;
    private final String name;

    UserRoleEnum(int hierarchyLevel, String name) {
        this.hierarchyLevel = hierarchyLevel;
        this.name = name;
    }

    public boolean canModify(UserRoleEnum targetRole) {
        return this.hierarchyLevel > targetRole.getHierarchyLevel();
    }

    public static UserRoleEnum getRoleByName(String name) {
        try{
            return UserRoleEnum.valueOf(name.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e){
            throw CustomNotFoundException.role();
        }
    }
}
