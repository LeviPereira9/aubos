package lp.boble.aubos.service.book.dependencies;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lp.boble.aubos.dto.book.dependencies.BookAddContributor;
import lp.boble.aubos.exception.custom.global.CustomNotFoundException;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.dependencies.ContributorModel;
import lp.boble.aubos.model.book.dependencies.ContributorRole;
import lp.boble.aubos.model.book.relationships.BookContributor;
import lp.boble.aubos.repository.book.depedencies.ContributorRepository;
import lp.boble.aubos.repository.book.depedencies.ContributorRoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContributorService {
    private final ContributorRepository contributorRepository;
    private final ContributorRoleRepository contributorRoleRepository;

    public ContributorModel getContributor(UUID id){
        return contributorRepository.findById(id)
                .orElseThrow(CustomNotFoundException::user);
    }

    public ContributorRole getRole(Integer id){
        return contributorRoleRepository.findById(id)
                .orElseThrow(CustomNotFoundException::user);
    }

    @Transactional
    public List<BookContributor> getContributors(
            BookModel book,
            List<BookAddContributor> contributors){
        return contributors.stream()
                .map(c -> new BookContributor(
                        book,
                        this.getContributor(c.contributorId()),
                        this.getRole(c.contributorRoleId())
                ))
                .collect(Collectors.toList());
    }
}
