package lp.boble.aubos.model.book;

import jakarta.persistence.*;
import lombok.Data;
import lp.boble.aubos.model.book.dependencies.*;
import lp.boble.aubos.model.book.relationships.BookContributor;
import lp.boble.aubos.model.book.relationships.BookLanguage;
import lp.boble.aubos.model.user.UserModel;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_book")
@Data
public class BookModel {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "title")
    private String title;
    @Column(name = "subtitle")
    private String subtitle;
    @Column(name = "synopsis")
    private String synopsis;
    @Column(name = "published_at")
    private LocalDate publishedAt;
    @Column(name = "finished_at")
    private LocalDate finishedAt;

    //Relações
    @ManyToOne
    @JoinColumn(name = "language_id")
    private LanguageModel language;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private Type type;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private Status status;

    @ManyToOne
    @JoinColumn(name = "restriction_id")
    private Restriction restriction;

    @ManyToOne
    @JoinColumn(name = "license_id")
    private LicenseModel licenseModel;

    // Controle
    @ManyToOne
    @JoinColumn(name = "created_by")
    private UserModel createdBy;

    @Column(name = "last_update")
    private Instant lastUpdated;

    @ManyToOne
    @JoinColumn(name = "updated_by")
    private UserModel updatedBy;

    // Relações N:N
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<BookLanguage> availableLanguages;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<BookContributor> contributors;

}
