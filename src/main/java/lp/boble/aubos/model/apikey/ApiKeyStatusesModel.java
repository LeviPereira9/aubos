package lp.boble.aubos.model.apikey;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "tb_api_key_statuses")
@Data
public class ApiKeyStatusesModel {
    @Id
    @Column(name = "id", columnDefinition = "tinyint UNSIGNED not null")
    private Short id;

    @Size(max = 20)
    @NotNull
    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @OneToMany(mappedBy = "status")
    @JsonIgnore
    private Set<ApiKeyModel> apiKeyModels = new LinkedHashSet<>();

}
