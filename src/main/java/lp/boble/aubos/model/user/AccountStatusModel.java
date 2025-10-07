package lp.boble.aubos.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "tb_account_status")
@Data
public class AccountStatusModel {
    @Id
    @Column(name = "id")
    private Byte id;

    @Column(name = "name", length = 20)
    private String name;

    @OneToMany(mappedBy = "status")
    @JsonIgnore
    private Set<UserModel> users = new LinkedHashSet<>();

}