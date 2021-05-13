package com.polykhel.billnext.web.rest

import com.polykhel.billnext.repository.ActivityRepository
import com.polykhel.billnext.service.ActivityQueryService
import com.polykhel.billnext.service.ActivityService
import com.polykhel.billnext.service.criteria.ActivityCriteria
import com.polykhel.billnext.service.dto.ActivityDTO
import com.polykhel.billnext.web.rest.errors.BadRequestAlertException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import tech.jhipster.web.util.HeaderUtil
import tech.jhipster.web.util.PaginationUtil
import tech.jhipster.web.util.PaginationUtil.generatePaginationHttpHeaders
import tech.jhipster.web.util.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotNull

private const val ENTITY_NAME = "activity"

/**
 * REST controller for managing [com.polykhel.billnext.domain.Activity].
 */
@RestController
@RequestMapping("/api")
class ActivityResource(
    private val activityService: ActivityService,
    private val activityRepository: ActivityRepository,
    private val activityQueryService: ActivityQueryService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        const val ENTITY_NAME = "activity"
    }

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /activities` : Create a new activity.
     *
     * @param activityDTO the activityDTO to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new activityDTO, or with status `400 (Bad Request)` if the activity has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/activities")
    fun createActivity(@Valid @RequestBody activityDTO: ActivityDTO): ResponseEntity<ActivityDTO> {
        log.debug("REST request to save Activity : $activityDTO")
        if (activityDTO.id != null) {
            throw BadRequestAlertException(
                "A new activity cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = activityService.save(activityDTO)
        return ResponseEntity.created(URI("/api/activities/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * {@code PUT  /activities/:id} : Updates an existing activity.
     *
     * @param id the id of the activityDTO to save.
     * @param activityDTO the activityDTO to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated activityDTO,
     * or with status `400 (Bad Request)` if the activityDTO is not valid,
     * or with status `500 (Internal Server Error)` if the activityDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/activities/{id}")
    fun updateActivity(
        @PathVariable(value = "id", required = false) id: Long,
        @Valid @RequestBody activityDTO: ActivityDTO
    ): ResponseEntity<ActivityDTO> {
        log.debug("REST request to update Activity : {}, {}", id, activityDTO)
        if (activityDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }

        if (!Objects.equals(id, activityDTO.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        if (!activityRepository.existsById(id)) {
            throw BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound")
        }

        val result = activityService.save(activityDTO)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                    activityDTO.id.toString()
                )
            )
            .body(result)
    }

    /**
     * {@code PATCH  /activities/:id} : Partial updates given fields of an existing activity, field will ignore if it is null
     *
     * @param id the id of the activityDTO to save.
     * @param activityDTO the activityDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated activityDTO,
     * or with status {@code 400 (Bad Request)} if the activityDTO is not valid,
     * or with status {@code 404 (Not Found)} if the activityDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the activityDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = ["/activities/{id}"], consumes = ["application/merge-patch+json"])
    @Throws(URISyntaxException::class)
    fun partialUpdateActivity(
        @PathVariable(value = "id", required = false) id: Long,
        @NotNull @RequestBody activityDTO: ActivityDTO
    ): ResponseEntity<ActivityDTO> {
        log.debug("REST request to partial update Activity partially : {}, {}", id, activityDTO)
        if (activityDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        if (!Objects.equals(id, activityDTO.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        if (!activityRepository.existsById(id)) {
            throw BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound")
        }

        val result = activityService.partialUpdate(activityDTO)

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, activityDTO.id.toString())
        )
    }

    /**
     * `GET  /activities/criteria` : get all the activities by criteria.
     *
     * @param pageable the pagination information.

     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of activities in body.
     */
    @GetMapping("/activities/criteria")
    fun getAllActivities(
        criteria: ActivityCriteria,
        pageable: Pageable
    ): ResponseEntity<MutableList<ActivityDTO>> {
        log.debug("REST request to get Activities by criteria: $criteria")
        val page = activityQueryService.findByCriteria(criteria, pageable)
        val headers = generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /activities` : get all the activities by category.
     *
     * @param pageable the pagination information.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of activities in body.
     */
    @GetMapping("/activities")
    fun getAllActivitiesByCurrentUser(pageable: Pageable): ResponseEntity<List<ActivityDTO>> {
        log.debug("REST request to get Activities by currentUser")
        val page = activityService.findAllByCurrentUser(pageable)
        val headers = generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /activities/count}` : count all the activities.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the count in body.
     */
    @GetMapping("/activities/count")
    fun countActivities(criteria: ActivityCriteria): ResponseEntity<Long> {
        log.debug("REST request to count Activities by criteria: $criteria")
        return ResponseEntity.ok().body(activityQueryService.countByCriteria(criteria))
    }

    /**
     * `GET  /activities/:id` : get the "id" activity.
     *
     * @param id the id of the activityDTO to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the activityDTO, or with status `404 (Not Found)`.
     */
    @GetMapping("/activities/{id}")
    fun getActivity(@PathVariable id: Long): ResponseEntity<ActivityDTO> {
        log.debug("REST request to get Activity : $id")
        val activityDTO = activityService.findOne(id)
        return ResponseUtil.wrapOrNotFound(activityDTO)
    }

    /**
     *  `DELETE  /activities/:id` : delete the "id" activity.
     *
     * @param id the id of the activityDTO to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/activities/{id}")
    fun deleteActivity(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Activity : $id")

        activityService.delete(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
