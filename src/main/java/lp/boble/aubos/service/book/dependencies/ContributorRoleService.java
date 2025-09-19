package lp.boble.aubos.service.book.dependencies;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.model.book.dependencies.ContributorRole;
import lp.boble.aubos.repository.book.depedencies.ContributorRoleRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContributorRoleService {

    private final ContributorRoleRepository contributorRoleRepository;

    public ContributorRole getContributorRoleOrThrow(Integer roleId){
        return contributorRoleRepository.findById(roleId)
                .orElseThrow(CustomNotFoundException::user);
    }

    public Set<Integer> getAllRolesId(List<Integer> rolesId){
        return new HashSet<>(contributorRoleRepository.findAllByIdIn(rolesId));
    }

    public List<ContributorRole> findAllContributorRoles(Set<Integer> rolesId){
        return contributorRoleRepository.findAllById(rolesId);
    }

    public Map<Integer, ContributorRole> getRequestedRoles(List<Integer> requestRoleIds) {
        List<ContributorRole> roles = contributorRoleRepository.findAllById(requestRoleIds);

        return roles.stream().collect(Collectors.toMap(ContributorRole::getId, Function.identity()));
    }
}
