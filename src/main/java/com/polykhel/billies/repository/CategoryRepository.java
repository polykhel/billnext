package com.polykhel.billies.repository;

import com.polykhel.billies.domain.Category;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Category entity.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("select category from Category category where category.user.login = ?#{principal.claims['preferred_username']}")
    Page<Category> findByUserIsCurrentUser(Pageable pageable);
}
