package lp.boble.aubos.mapper.user;

import lp.boble.aubos.dto.auth.AuthRegisterRequest;
import lp.boble.aubos.dto.user.*;
import lp.boble.aubos.model.user.UserModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
        componentModel = "spring"
)
public interface UserMapper {

    @Mapping(target = "passwordHash", ignore = true)
    UserModel fromRegisterToModel(AuthRegisterRequest authRegisterRequest);

    @Mapping(target = "role", source = "role.name")
    @Mapping(target = "status", source = "status.name")
    UserResponse fromModelToResponse(UserModel userModel);

    @Mapping(target = "role", source = "role.name")
    @Mapping(target = "status", source = "status.name")
    UserShortResponse fromModelToShortResponse(UserModel userModel);

    void fromUpdateToModel(UserUpdateRequest request, @MappingTarget UserModel target);

    UserAutocompletePageResponse fromAutocompleteProjectionToResponse(UserAutocompletePageProjection userAutocompletePageProjection);

    UserSuggestionPageResponse fromSuggestionProjectionToResponse(UserSuggestionPageProjection userSuggestionPageProjection);

}
