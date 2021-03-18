package com.polykhel.billnext.repository;

import com.polykhel.billnext.domain.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Activity entity.
 */
@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    @Query("select activity from Activity activity where activity.user.login = ?#{principal.claims['preferred_username']}")
    Page<Activity> findByUserIsCurrentUser(Pageable pageable);
}
