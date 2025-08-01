package lp.boble.aubos.model.book.relationships;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lp.boble.aubos.dto.book.parts.BookContributorResponse;
import lp.boble.aubos.model.book.BookModel;
import lp.boble.aubos.model.book.dependencies.ContributorModel;
import lp.boble.aubos.model.book.dependencies.ContributorRole;

import java.util.*;

@Entity
@Table(name = "tb_book_contributor")
@Data
public class BookContributor {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    @JsonIgnore
    private BookModel book;

    @ManyToOne
    @JoinColumn(name = "contributor_id")
    @JsonIgnore
    private ContributorModel contributor;

    @ManyToOne
    @JoinColumn(name = "contributor_role_id")
    private ContributorRole contributorRole;

    public BookContributor(
            BookModel book,
            ContributorModel contributor,
            ContributorRole contributorRole) {
        this.book = book;
        this.contributor = contributor;
        this.contributorRole = contributorRole;
    }

    public BookContributor(){}

    public boolean isAuthor(){
        return contributorRole != null && "autor".equals(contributorRole.getName());
    }

    public boolean isEditor(){
        return contributorRole != null && "editor".equals(contributorRole.getName());
    }

    public boolean isIllustrator(){
        return contributorRole != null && "ilustrador".equals(contributorRole.getName());
    }

    public boolean isPublisher(){
        return contributorRole != null && "publicadora".equals(contributorRole.getName());
    }

    public static Map<String, List<BookContributorResponse>> arrangeContributors(List<BookContributor> rawContributors){
        Map<String, List<BookContributorResponse>> contributors = new HashMap<>();
        contributors.put("autor", new ArrayList<>());
        contributors.put("editor", new ArrayList<>());
        contributors.put("ilustrador", new ArrayList<>());
        contributors.put("publicadora", new ArrayList<>());

        rawContributors.forEach(c ->
                contributors.get(c.getContributorRole().getName())
                        .add(new BookContributorResponse(
                                c.getContributor().getId(),
                                c.getContributor().getName()
                        ))
        );

        return contributors;
    }

    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        BookContributor that = (BookContributor) o;
        return Objects.equals(contributor, that.contributor) && Objects.equals(contributorRole, that.contributorRole);
    }
}
