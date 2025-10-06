package lp.boble.aubos.service.user;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.user.role.RoleResponse;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.mapper.user.role.RoleMapper;
import lp.boble.aubos.model.user.RoleModel;
import lp.boble.aubos.repository.user.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public List<RoleResponse> getAllRoles(){
        List<RoleModel> roles = roleRepository.findAll();

        return roles.stream().map(roleMapper::toResponse).toList();
    }

    public RoleModel findRoleByNameOrThrow(String roleName){
        return roleRepository.findByName(roleName)
                .orElseThrow(CustomNotFoundException::role);
    }
}
