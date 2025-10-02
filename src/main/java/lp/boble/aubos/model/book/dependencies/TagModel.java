package lp.boble.aubos.model.book.dependencies;

import jakarta.persistence.*;
import lombok.Data;
import lp.boble.aubos.model.book.relationships.BookTagModel;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_tag")
@Data
public class TagModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookTagModel> bookTags;
}
