package com.polykhel.billies.repository;

import com.polykhel.billies.domain.Category;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Category entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("select category from Category category where category.user.login = ?#{principal.preferredUsername}")
    List<Category> findByUserIsCurrentUser();
}
