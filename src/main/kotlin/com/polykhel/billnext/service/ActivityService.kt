package com.polykhel.billnext.service

import com.polykhel.billnext.service.dto.ActivityDTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

/**
 * Service Interface for managing [com.polykhel.billnext.domain.Activity].
 */
interface ActivityService {

    /**
     * Save a activity.
     *
     * @param activityDTO the entity to save.
     * @return the persisted entity.
     */
    fun save(activityDTO: ActivityDTO): ActivityDTO

    /**
     * Partially updates a activity.
     *
     * @param activityDTO the entity to update partially.
     * @return the persisted entity.
     */
    fun partialUpdate(activityDTO: ActivityDTO): Optional<ActivityDTO>

    /**
     * Get all the activities.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    fun findAll(pageable: Pageable): Page<ActivityDTO>

    /**
     * Get all the activities by currentUser.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    fun findAllByCurrentUser(pageable: Pageable): Page<ActivityDTO>

    /**
     * Get the "id" activity.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    fun findOne(id: Long): Optional<ActivityDTO>

    /**
     * Delete the "id" activity.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long)
}
