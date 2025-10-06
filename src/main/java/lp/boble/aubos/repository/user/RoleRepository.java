package lp.boble.aubos.repository.user;

import lp.boble.aubos.model.user.RoleModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleModel, Short> {

    Optional<RoleModel> findByName(String roleName);

}
