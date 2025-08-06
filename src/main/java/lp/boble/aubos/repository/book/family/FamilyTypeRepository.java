package lp.boble.aubos.repository.book.family;

import lp.boble.aubos.model.book.family.FamilyType;
import lp.boble.aubos.model.user.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface FamilyTypeRepository extends JpaRepository<FamilyType, Integer> {

    @Query("""
    SELECT EXISTS (FROM FamilyModel f
    WHERE f.id = :id AND f.createdBy = :requester)
""")
    boolean isOwner(UUID id, UserModel requester);

}
