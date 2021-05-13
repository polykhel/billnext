package com.polykhel.billnext.web.rest

import com.polykhel.billnext.repository.WalletGroupRepository
import com.polykhel.billnext.security.ADMIN
import com.polykhel.billnext.service.WalletGroupQueryService
import com.polykhel.billnext.service.WalletGroupService
import com.polykhel.billnext.service.criteria.WalletGroupCriteria
import com.polykhel.billnext.service.dto.WalletGroupDTO
import com.polykhel.billnext.web.rest.errors.BadRequestAlertException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest
import tech.jhipster.web.util.HeaderUtil
import tech.jhipster.web.util.PaginationUtil.generatePaginationHttpHeaders
import tech.jhipster.web.util.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotNull

private const val ENTITY_NAME = "walletGroup"

/**
 * REST controller for managing [com.polykhel.billnext.domain.WalletGroup].
 */
@RestController
@RequestMapping("/api")
class WalletGroupResource(
    private val walletGroupService: WalletGroupService,
    private val walletGroupRepository: WalletGroupRepository,
    private val walletGroupQueryService: WalletGroupQueryService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        const val ENTITY_NAME = "walletGroup"
    }

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /wallet-groups` : Create a new walletGroup.
     *
     * @param walletGroupDTO the walletGroupDTO to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new walletGroupDTO, or with status `400 (Bad Request)` if the walletGroup has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/wallet-groups")
    fun createWalletGroup(@Valid @RequestBody walletGroupDTO: WalletGroupDTO): ResponseEntity<WalletGroupDTO> {
        log.debug("REST request to save WalletGroup : $walletGroupDTO")
        if (walletGroupDTO.id != null) {
            throw BadRequestAlertException(
                "A new walletGroup cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }

        val result = walletGroupService.save(walletGroupDTO)
        return ResponseEntity.created(URI("/api/wallet-groups/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * {@code PUT  /wallet-groups/:id} : Updates an existing walletGroup.
     *
     * @param id the id of the walletGroupDTO to save.
     * @param walletGroupDTO the walletGroupDTO to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated walletGroupDTO,
     * or with status `400 (Bad Request)` if the walletGroupDTO is not valid,
     * or with status `500 (Internal Server Error)` if the walletGroupDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/wallet-groups/{id}")
    fun updateWalletGroup(
        @PathVariable(value = "id", required = false) id: Long,
        @Valid @RequestBody walletGroupDTO: WalletGroupDTO
    ): ResponseEntity<WalletGroupDTO> {
        log.debug("REST request to update WalletGroup : {}, {}", id, walletGroupDTO)
        if (walletGroupDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }

        if (!Objects.equals(id, walletGroupDTO.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        if (!walletGroupRepository.existsById(id)) {
            throw BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound")
        }

        val result = walletGroupService.save(walletGroupDTO)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                    walletGroupDTO.id.toString()
                )
            )
            .body(result)
    }

    /**
     * {@code PATCH  /wallet-groups/:id} : Partial updates given fields of an existing walletGroup, field will ignore if it is null
     *
     * @param id the id of the walletGroupDTO to save.
     * @param walletGroupDTO the walletGroupDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated walletGroupDTO,
     * or with status {@code 400 (Bad Request)} if the walletGroupDTO is not valid,
     * or with status {@code 404 (Not Found)} if the walletGroupDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the walletGroupDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = ["/wallet-groups/{id}"], consumes = ["application/merge-patch+json"])
    @Throws(URISyntaxException::class)
    fun partialUpdateWalletGroup(
        @PathVariable(value = "id", required = false) id: Long,
        @NotNull @RequestBody walletGroupDTO: WalletGroupDTO
    ): ResponseEntity<WalletGroupDTO> {
        log.debug("REST request to partial update WalletGroup partially : {}, {}", id, walletGroupDTO)
        if (walletGroupDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        if (!Objects.equals(id, walletGroupDTO.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        if (!walletGroupRepository.existsById(id)) {
            throw BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound")
        }

        val result = walletGroupService.partialUpdate(walletGroupDTO)

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, walletGroupDTO.id.toString())
        )
    }

    /**
     * `GET  /wallet-groups/criteria` : get all the walletGroups.
     *
     * @param pageable the pagination information.

     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of walletGroups in body.
     */
    @GetMapping("/wallet-groups/criteria")
    @PreAuthorize("hasAuthority(\"$ADMIN\")")
    fun getAllWalletGroups(
        criteria: WalletGroupCriteria,
        pageable: Pageable
    ): ResponseEntity<MutableList<WalletGroupDTO>> {
        log.debug("REST request to get WalletGroups by criteria: $criteria")
        val page = walletGroupQueryService.findByCriteria(criteria, pageable)
        val headers = generatePaginationHttpHeaders(fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /wallet-groups` : get all the walletGroups by currentUser.
     *
     * @param pageable the pagination information.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of walletGroups in body.
     */
    @GetMapping("/wallet-groups")
    fun getAllWalletGroupsByCurrentUser(pageable: Pageable): ResponseEntity<List<WalletGroupDTO>> {
        log.debug("REST request to get WalletGroups by currentUser")
        val page = walletGroupService.findAllByCurrentUser(pageable)
        val headers = generatePaginationHttpHeaders(fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /wallet-groups/count}` : count all the walletGroups.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the count in body.
     */
    @GetMapping("/wallet-groups/count")
    @PreAuthorize("hasAuthority(\"$ADMIN\")")
    fun countWalletGroups(criteria: WalletGroupCriteria): ResponseEntity<Long> {
        log.debug("REST request to count WalletGroups by criteria: $criteria")
        return ResponseEntity.ok().body(walletGroupQueryService.countByCriteria(criteria))
    }

    /**
     * `GET  /wallet-groups/:id` : get the "id" walletGroup.
     *
     * @param id the id of the walletGroupDTO to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the walletGroupDTO, or with status `404 (Not Found)`.
     */
    @GetMapping("/wallet-groups/{id}")
    fun getWalletGroup(@PathVariable id: Long): ResponseEntity<WalletGroupDTO> {
        log.debug("REST request to get WalletGroup : $id")
        val walletGroupDTO = walletGroupService.findOne(id)
        return ResponseUtil.wrapOrNotFound(walletGroupDTO)
    }

    /**
     *  `DELETE  /wallet-groups/:id` : delete the "id" walletGroup.
     *
     * @param id the id of the walletGroupDTO to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/wallet-groups/{id}")
    fun deleteWalletGroup(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete WalletGroup : $id")

        walletGroupService.delete(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
