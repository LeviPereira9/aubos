package lp.boble.aubos.service.book.dependencies;

import lombok.RequiredArgsConstructor;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.model.book.dependencies.ContributorRole;
import lp.boble.aubos.repository.book.depedencies.ContributorRoleRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
}
