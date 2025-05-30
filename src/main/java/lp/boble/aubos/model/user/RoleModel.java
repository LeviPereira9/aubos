package lp.boble.aubos.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "tb_roles")
@Data
public class RoleModel {
    @Id
    @Column(name = "id")
    private Short id;

    @Size(max = 20)
    @Column(name = "name", length = 20)
    private String name;

    @OneToMany(mappedBy = "role")
    @JsonIgnore
    private Set<UserModel> users = new LinkedHashSet<>();


}
