package com.polykhel.billnext.web.rest

import com.polykhel.billnext.IntegrationTest
import com.polykhel.billnext.domain.WalletGroup
import com.polykhel.billnext.repository.WalletGroupRepository
import com.polykhel.billnext.service.mapper.WalletGroupMapper
import com.polykhel.billnext.web.rest.errors.ExceptionTranslator
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.Validator
import java.util.Random
import java.util.concurrent.atomic.AtomicLong
import javax.persistence.EntityManager
import kotlin.test.assertNotNull

/**
 * Integration tests for the [WalletGroupResource] REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class WalletGroupResourceIT {
    @Autowired
    private lateinit var walletGroupRepository: WalletGroupRepository

    @Autowired
    private lateinit var walletGroupMapper: WalletGroupMapper

    @Autowired
    private lateinit var jacksonMessageConverter: MappingJackson2HttpMessageConverter

    @Autowired
    private lateinit var pageableArgumentResolver: PageableHandlerMethodArgumentResolver

    @Autowired
    private lateinit var exceptionTranslator: ExceptionTranslator

    @Autowired
    private lateinit var validator: Validator

    @Autowired
    private lateinit var em: EntityManager

    @Autowired
    private lateinit var restWalletGroupMockMvc: MockMvc

    private lateinit var walletGroup: WalletGroup

    @BeforeEach
    fun initTest() {
        walletGroup = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createWalletGroup() {
        val databaseSizeBeforeCreate = walletGroupRepository.findAll().size

        // Create the WalletGroup
        val walletGroupDTO = walletGroupMapper.toDto(walletGroup)
        restWalletGroupMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(walletGroupDTO))
        ).andExpect(status().isCreated)

        // Validate the WalletGroup in the database
        val walletGroupList = walletGroupRepository.findAll()
        assertThat(walletGroupList).hasSize(databaseSizeBeforeCreate + 1)
        val testWalletGroup = walletGroupList[walletGroupList.size - 1]

        assertThat(testWalletGroup.name).isEqualTo(DEFAULT_NAME)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createWalletGroupWithExistingId() {
        // Create the WalletGroup with an existing ID
        walletGroup.id = 1L
        val walletGroupDTO = walletGroupMapper.toDto(walletGroup)

        val databaseSizeBeforeCreate = walletGroupRepository.findAll().size

        // An entity with an existing ID cannot be created, so this API call must fail
        restWalletGroupMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(walletGroupDTO))
        ).andExpect(status().isBadRequest)

        // Validate the WalletGroup in the database
        val walletGroupList = walletGroupRepository.findAll()
        assertThat(walletGroupList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun checkNameIsRequired() {
        val databaseSizeBeforeTest = walletGroupRepository.findAll().size
        // set the field null
        walletGroup.name = null

        // Create the WalletGroup, which fails.
        val walletGroupDTO = walletGroupMapper.toDto(walletGroup)

        restWalletGroupMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(walletGroupDTO))
        ).andExpect(status().isBadRequest)

        val walletGroupList = walletGroupRepository.findAll()
        assertThat(walletGroupList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllWalletGroups() {
        // Initialize the database
        walletGroupRepository.saveAndFlush(walletGroup)

        // Get all the walletGroupList
        restWalletGroupMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(walletGroup.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getWalletGroup() {
        // Initialize the database
        walletGroupRepository.saveAndFlush(walletGroup)

        val id = walletGroup.id
        assertNotNull(id)

        // Get the walletGroup
        restWalletGroupMockMvc.perform(get(ENTITY_API_URL_ID, walletGroup.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(walletGroup.id?.toInt()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getWalletGroupsByIdFiltering() {
        // Initialize the database
        walletGroupRepository.saveAndFlush(walletGroup)
        val id = walletGroup.id

        defaultWalletGroupShouldBeFound("id.equals=" + id)
        defaultWalletGroupShouldNotBeFound("id.notEquals=" + id)
        defaultWalletGroupShouldBeFound("id.greaterThanOrEqual=" + id)
        defaultWalletGroupShouldNotBeFound("id.greaterThan=" + id)

        defaultWalletGroupShouldBeFound("id.lessThanOrEqual=" + id)
        defaultWalletGroupShouldNotBeFound("id.lessThan=" + id)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllWalletGroupsByNameIsEqualToSomething() {
        // Initialize the database
        walletGroupRepository.saveAndFlush(walletGroup)

        // Get all the walletGroupList where name equals to DEFAULT_NAME
        defaultWalletGroupShouldBeFound("name.equals=$DEFAULT_NAME")

        // Get all the walletGroupList where name equals to UPDATED_NAME
        defaultWalletGroupShouldNotBeFound("name.equals=$UPDATED_NAME")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllWalletGroupsByNameIsNotEqualToSomething() {
        // Initialize the database
        walletGroupRepository.saveAndFlush(walletGroup)

        // Get all the walletGroupList where name not equals to DEFAULT_NAME
        defaultWalletGroupShouldNotBeFound("name.notEquals=" + DEFAULT_NAME)

        // Get all the walletGroupList where name not equals to UPDATED_NAME
        defaultWalletGroupShouldBeFound("name.notEquals=" + UPDATED_NAME)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllWalletGroupsByNameIsInShouldWork() {
        // Initialize the database
        walletGroupRepository.saveAndFlush(walletGroup)

        // Get all the walletGroupList where name in DEFAULT_NAME or UPDATED_NAME
        defaultWalletGroupShouldBeFound("name.in=$DEFAULT_NAME,$UPDATED_NAME")

        // Get all the walletGroupList where name equals to UPDATED_NAME
        defaultWalletGroupShouldNotBeFound("name.in=$UPDATED_NAME")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllWalletGroupsByNameIsNullOrNotNull() {
        // Initialize the database
        walletGroupRepository.saveAndFlush(walletGroup)

        // Get all the walletGroupList where name is not null
        defaultWalletGroupShouldBeFound("name.specified=true")

        // Get all the walletGroupList where name is null
        defaultWalletGroupShouldNotBeFound("name.specified=false")
    }
    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllWalletGroupsByNameContainsSomething() {
        // Initialize the database
        walletGroupRepository.saveAndFlush(walletGroup)

        // Get all the walletGroupList where name contains DEFAULT_NAME
        defaultWalletGroupShouldBeFound("name.contains=" + DEFAULT_NAME)

        // Get all the walletGroupList where name contains UPDATED_NAME
        defaultWalletGroupShouldNotBeFound("name.contains=" + UPDATED_NAME)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllWalletGroupsByNameNotContainsSomething() {
        // Initialize the database
        walletGroupRepository.saveAndFlush(walletGroup)

        // Get all the walletGroupList where name does not contain DEFAULT_NAME
        defaultWalletGroupShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME)

        // Get all the walletGroupList where name does not contain UPDATED_NAME
        defaultWalletGroupShouldBeFound("name.doesNotContain=" + UPDATED_NAME)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllWalletGroupsByUserIsEqualToSomething() {
        // Initialize the database
        walletGroupRepository.saveAndFlush(walletGroup)
        val user = UserResourceIT.createEntity(em)
        em.persist(user)
        em.flush()
        walletGroup.user = user
        walletGroupRepository.saveAndFlush(walletGroup)
        val userId = user.id

        // Get all the walletGroupList where user equals to userId
        defaultWalletGroupShouldBeFound("userId.equals=$userId")

        // Get all the walletGroupList where user equals to (userId?.plus(1))
        defaultWalletGroupShouldNotBeFound("userId.equals=${(userId?.plus(1))}")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllWalletGroupsByWalletsIsEqualToSomething() {
        // Initialize the database
        walletGroupRepository.saveAndFlush(walletGroup)
        val wallets = WalletResourceIT.createEntity(em)
        em.persist(wallets)
        em.flush()
        walletGroup.addWallets(wallets)
        walletGroupRepository.saveAndFlush(walletGroup)
        val walletsId = wallets.id

        // Get all the walletGroupList where wallets equals to walletsId
        defaultWalletGroupShouldBeFound("walletsId.equals=$walletsId")

        // Get all the walletGroupList where wallets equals to (walletsId?.plus(1))
        defaultWalletGroupShouldNotBeFound("walletsId.equals=${(walletsId?.plus(1))}")
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */

    @Throws(Exception::class)
    private fun defaultWalletGroupShouldBeFound(filter: String) {
        restWalletGroupMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(walletGroup.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))

        // Check, that the count call also returns 1
        restWalletGroupMockMvc.perform(get(ENTITY_API_URL + "/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"))
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    @Throws(Exception::class)
    private fun defaultWalletGroupShouldNotBeFound(filter: String) {
        restWalletGroupMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)

        // Check, that the count call also returns 0
        restWalletGroupMockMvc.perform(get(ENTITY_API_URL + "/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"))
    }
    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingWalletGroup() {
        // Get the walletGroup
        restWalletGroupMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun putNewWalletGroup() {
        // Initialize the database
        walletGroupRepository.saveAndFlush(walletGroup)

        val databaseSizeBeforeUpdate = walletGroupRepository.findAll().size

        // Update the walletGroup
        val updatedWalletGroup = walletGroupRepository.findById(walletGroup.id).get()
        // Disconnect from session so that the updates on updatedWalletGroup are not directly saved in db
        em.detach(updatedWalletGroup)
        updatedWalletGroup.name = UPDATED_NAME
        val walletGroupDTO = walletGroupMapper.toDto(updatedWalletGroup)

        restWalletGroupMockMvc.perform(
            put(ENTITY_API_URL_ID, walletGroupDTO.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(walletGroupDTO))
        ).andExpect(status().isOk)

        // Validate the WalletGroup in the database
        val walletGroupList = walletGroupRepository.findAll()
        assertThat(walletGroupList).hasSize(databaseSizeBeforeUpdate)
        val testWalletGroup = walletGroupList[walletGroupList.size - 1]
        assertThat(testWalletGroup.name).isEqualTo(UPDATED_NAME)
    }

    @Test
    @Transactional
    fun putNonExistingWalletGroup() {
        val databaseSizeBeforeUpdate = walletGroupRepository.findAll().size
        walletGroup.id = count.incrementAndGet()

        // Create the WalletGroup
        val walletGroupDTO = walletGroupMapper.toDto(walletGroup)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWalletGroupMockMvc.perform(
            put(ENTITY_API_URL_ID, walletGroupDTO.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(walletGroupDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the WalletGroup in the database
        val walletGroupList = walletGroupRepository.findAll()
        assertThat(walletGroupList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithIdMismatchWalletGroup() {
        val databaseSizeBeforeUpdate = walletGroupRepository.findAll().size
        walletGroup.id = count.incrementAndGet()

        // Create the WalletGroup
        val walletGroupDTO = walletGroupMapper.toDto(walletGroup)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWalletGroupMockMvc.perform(
            put(ENTITY_API_URL_ID, count.incrementAndGet())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(walletGroupDTO))
        ).andExpect(status().isBadRequest)

        // Validate the WalletGroup in the database
        val walletGroupList = walletGroupRepository.findAll()
        assertThat(walletGroupList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithMissingIdPathParamWalletGroup() {
        val databaseSizeBeforeUpdate = walletGroupRepository.findAll().size
        walletGroup.id = count.incrementAndGet()

        // Create the WalletGroup
        val walletGroupDTO = walletGroupMapper.toDto(walletGroup)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWalletGroupMockMvc.perform(
            put(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(walletGroupDTO))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the WalletGroup in the database
        val walletGroupList = walletGroupRepository.findAll()
        assertThat(walletGroupList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun partialUpdateWalletGroupWithPatch() {

        // Initialize the database
        walletGroupRepository.saveAndFlush(walletGroup)

        val databaseSizeBeforeUpdate = walletGroupRepository.findAll().size

// Update the walletGroup using partial update
        val partialUpdatedWalletGroup = WalletGroup().apply {
            id = walletGroup.id

            name = UPDATED_NAME
        }

        restWalletGroupMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedWalletGroup.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedWalletGroup))
        )
            .andExpect(status().isOk)

// Validate the WalletGroup in the database
        val walletGroupList = walletGroupRepository.findAll()
        assertThat(walletGroupList).hasSize(databaseSizeBeforeUpdate)
        val testWalletGroup = walletGroupList.last()
        assertThat(testWalletGroup.name).isEqualTo(UPDATED_NAME)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun fullUpdateWalletGroupWithPatch() {

        // Initialize the database
        walletGroupRepository.saveAndFlush(walletGroup)

        val databaseSizeBeforeUpdate = walletGroupRepository.findAll().size

// Update the walletGroup using partial update
        val partialUpdatedWalletGroup = WalletGroup().apply {
            id = walletGroup.id

            name = UPDATED_NAME
        }

        restWalletGroupMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedWalletGroup.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedWalletGroup))
        )
            .andExpect(status().isOk)

// Validate the WalletGroup in the database
        val walletGroupList = walletGroupRepository.findAll()
        assertThat(walletGroupList).hasSize(databaseSizeBeforeUpdate)
        val testWalletGroup = walletGroupList.last()
        assertThat(testWalletGroup.name).isEqualTo(UPDATED_NAME)
    }

    @Throws(Exception::class)
    fun patchNonExistingWalletGroup() {
        val databaseSizeBeforeUpdate = walletGroupRepository.findAll().size
        walletGroup.id = count.incrementAndGet()

        // Create the WalletGroup
        val walletGroupDTO = walletGroupMapper.toDto(walletGroup)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWalletGroupMockMvc.perform(
            patch(ENTITY_API_URL_ID, walletGroupDTO.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(walletGroupDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the WalletGroup in the database
        val walletGroupList = walletGroupRepository.findAll()
        assertThat(walletGroupList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithIdMismatchWalletGroup() {
        val databaseSizeBeforeUpdate = walletGroupRepository.findAll().size
        walletGroup.id = count.incrementAndGet()

        // Create the WalletGroup
        val walletGroupDTO = walletGroupMapper.toDto(walletGroup)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWalletGroupMockMvc.perform(
            patch(ENTITY_API_URL_ID, count.incrementAndGet())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(walletGroupDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the WalletGroup in the database
        val walletGroupList = walletGroupRepository.findAll()
        assertThat(walletGroupList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithMissingIdPathParamWalletGroup() {
        val databaseSizeBeforeUpdate = walletGroupRepository.findAll().size
        walletGroup.id = count.incrementAndGet()

        // Create the WalletGroup
        val walletGroupDTO = walletGroupMapper.toDto(walletGroup)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWalletGroupMockMvc.perform(
            patch(ENTITY_API_URL)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(walletGroupDTO))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the WalletGroup in the database
        val walletGroupList = walletGroupRepository.findAll()
        assertThat(walletGroupList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteWalletGroup() {
        // Initialize the database
        walletGroupRepository.saveAndFlush(walletGroup)

        val databaseSizeBeforeDelete = walletGroupRepository.findAll().size

        // Delete the walletGroup
        restWalletGroupMockMvc.perform(
            delete(ENTITY_API_URL_ID, walletGroup.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val walletGroupList = walletGroupRepository.findAll()
        assertThat(walletGroupList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_NAME = "AAAAAAAAAA"
        private const val UPDATED_NAME = "BBBBBBBBBB"

        private val ENTITY_API_URL: String = "/api/wallet-groups"
        private val ENTITY_API_URL_ID: String = ENTITY_API_URL + "/{id}"

        private val random: Random = Random()
        private val count: AtomicLong = AtomicLong(random.nextInt().toLong() + (2 * Integer.MAX_VALUE))

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): WalletGroup {
            val walletGroup = WalletGroup(

                name = DEFAULT_NAME

            )

            // Add required entity
            val user = UserResourceIT.createEntity(em)
            em.persist(user)
            em.flush()
            walletGroup.user = user
            return walletGroup
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): WalletGroup {
            val walletGroup = WalletGroup(

                name = UPDATED_NAME

            )

            // Add required entity
            val user = UserResourceIT.createEntity(em)
            em.persist(user)
            em.flush()
            walletGroup.user = user
            return walletGroup
        }
    }
}
