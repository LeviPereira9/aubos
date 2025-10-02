package lp.boble.aubos.repository.book.depedencies.tag;

import lp.boble.aubos.model.book.dependencies.TagModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TagRepository extends JpaRepository<TagModel, Integer> {

    boolean existsByName(String name);

    Page<TagModel> findAllByOrderByNameAsc(Pageable pageable);

    @Query("""
    SELECT t FROM TagModel t
     WHERE t.name LIKE CONCAT('%',:query,'%')
     ORDER BY t.name ASC
""")
    Page<TagModel> findTagsBySearch(String query, Pageable pageRequest);
}
