package com.polykhel.billnext.web.rest

import com.polykhel.billnext.repository.WalletRepository
import com.polykhel.billnext.security.ADMIN
import com.polykhel.billnext.service.WalletQueryService
import com.polykhel.billnext.service.WalletService
import com.polykhel.billnext.service.criteria.WalletCriteria
import com.polykhel.billnext.service.dto.WalletDTO
import com.polykhel.billnext.web.rest.errors.BadRequestAlertException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import tech.jhipster.web.util.HeaderUtil
import tech.jhipster.web.util.PaginationUtil
import tech.jhipster.web.util.PaginationUtil.*
import tech.jhipster.web.util.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotNull

private const val ENTITY_NAME = "wallet"

/**
 * REST controller for managing [com.polykhel.billnext.domain.Wallet].
 */
@RestController
@RequestMapping("/api")
class WalletResource(
    private val walletService: WalletService,
    private val walletRepository: WalletRepository,
    private val walletQueryService: WalletQueryService
) {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        const val ENTITY_NAME = "wallet"
    }

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /wallets` : Create a new wallet.
     *
     * @param walletDTO the walletDTO to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new walletDTO, or with status `400 (Bad Request)` if the wallet has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/wallets")
    fun createWallet(@Valid @RequestBody walletDTO: WalletDTO): ResponseEntity<WalletDTO> {
        log.debug("REST request to save Wallet : $walletDTO")
        if (walletDTO.id != null) {
            throw BadRequestAlertException(
                "A new wallet cannot already have an ID",
                ENTITY_NAME, "idexists"
            )
        }
        val result = walletService.save(walletDTO)
        return ResponseEntity.created(URI("/api/wallets/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * {@code PUT  /wallets/:id} : Updates an existing wallet.
     *
     * @param id the id of the walletDTO to save.
     * @param walletDTO the walletDTO to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated walletDTO,
     * or with status `400 (Bad Request)` if the walletDTO is not valid,
     * or with status `500 (Internal Server Error)` if the walletDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/wallets/{id}")
    fun updateWallet(
        @PathVariable(value = "id", required = false) id: Long,
        @Valid @RequestBody walletDTO: WalletDTO
    ): ResponseEntity<WalletDTO> {
        log.debug("REST request to update Wallet : {}, {}", id, walletDTO)
        if (walletDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }

        if (!Objects.equals(id, walletDTO.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        if (!walletRepository.existsById(id)) {
            throw BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound")
        }

        val result = walletService.save(walletDTO)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName, true, ENTITY_NAME,
                    walletDTO.id.toString()
                )
            )
            .body(result)
    }

    /**
     * {@code PATCH  /wallets/:id} : Partial updates given fields of an existing wallet, field will ignore if it is null
     *
     * @param id the id of the walletDTO to save.
     * @param walletDTO the walletDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated walletDTO,
     * or with status {@code 400 (Bad Request)} if the walletDTO is not valid,
     * or with status {@code 404 (Not Found)} if the walletDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the walletDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = ["/wallets/{id}"], consumes = ["application/merge-patch+json"])
    @Throws(URISyntaxException::class)
    fun partialUpdateWallet(
        @PathVariable(value = "id", required = false) id: Long,
        @NotNull @RequestBody walletDTO: WalletDTO
    ): ResponseEntity<WalletDTO> {
        log.debug("REST request to partial update Wallet partially : {}, {}", id, walletDTO)
        if (walletDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        if (!Objects.equals(id, walletDTO.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        if (!walletRepository.existsById(id)) {
            throw BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound")
        }

        val result = walletService.partialUpdate(walletDTO)

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, walletDTO.id.toString())
        )
    }

    /**
     * `GET  /wallets/criteria` : get all the wallets by criteria.
     *
     * @param pageable the pagination information.

     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of wallets in body.
     */
    @GetMapping("/wallets/criteria")
    @PreAuthorize("hasAuthority(\"$ADMIN\")")
    fun getAllWallets(
        criteria: WalletCriteria,
        pageable: Pageable
    ): ResponseEntity<MutableList<WalletDTO>> {
        log.debug("REST request to get Wallets by criteria: $criteria")
        val page = walletQueryService.findByCriteria(criteria, pageable)
        val headers =
            generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /wallets` : get all the wallets by currentUser.
     *
     * @param pageable the pagination information.
     * @return the [ResponseEntity] with status `200 (OK)` and the list of wallets in body.
     */
    @GetMapping("/wallets")
    fun getAllWalletsByCurrentUser(pageable: Pageable): ResponseEntity<List<WalletDTO>> {
        log.debug("REST request to get Wallets by currentUser")
        val page = walletService.findAllByCurrentUser(pageable)
        val headers = generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /wallets/count}` : count all the wallets.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the [ResponseEntity] with status `200 (OK)` and the count in body.
     */
    @GetMapping("/wallets/count")
    @PreAuthorize("hasAuthority(\"$ADMIN\")")
    fun countWallets(criteria: WalletCriteria): ResponseEntity<Long> {
        log.debug("REST request to count Wallets by criteria: $criteria")
        return ResponseEntity.ok().body(walletQueryService.countByCriteria(criteria))
    }

    /**
     * `GET  /wallets/:id` : get the "id" wallet.
     *
     * @param id the id of the walletDTO to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the walletDTO, or with status `404 (Not Found)`.
     */
    @GetMapping("/wallets/{id}")
    fun getWallet(@PathVariable id: Long): ResponseEntity<WalletDTO> {
        log.debug("REST request to get Wallet : $id")
        val walletDTO = walletService.findOne(id)
        return ResponseUtil.wrapOrNotFound(walletDTO)
    }

    /**
     *  `DELETE  /wallets/:id` : delete the "id" wallet.
     *
     * @param id the id of the walletDTO to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/wallets/{id}")
    fun deleteWallet(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Wallet : $id")

        walletService.delete(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
