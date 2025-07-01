package lp.boble.aubos.model.auth;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "tb_token_type")
public class TokenTypeModel {
    @Id
    private Long id;
    private String type;

}
