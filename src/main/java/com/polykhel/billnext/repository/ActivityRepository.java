package com.polykhel.billnext.repository;

import com.polykhel.billnext.domain.Activity;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Activity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    @Query("select activity from Activity activity where activity.user.login = ?#{principal.preferredUsername}")
    List<Activity> findByUserIsCurrentUser();
}
