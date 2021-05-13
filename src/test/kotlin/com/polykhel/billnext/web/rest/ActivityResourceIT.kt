package com.polykhel.billnext.web.rest

import com.polykhel.billnext.IntegrationTest
import com.polykhel.billnext.domain.Activity
import com.polykhel.billnext.domain.Category
import com.polykhel.billnext.domain.Wallet
import com.polykhel.billnext.domain.enumeration.ActivityType
import com.polykhel.billnext.repository.ActivityRepository
import com.polykhel.billnext.service.mapper.ActivityMapper
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
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.Random
import java.util.concurrent.atomic.AtomicLong
import javax.persistence.EntityManager
import kotlin.test.assertNotNull

/**
 * Integration tests for the [ActivityResource] REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ActivityResourceIT {
    @Autowired
    private lateinit var activityRepository: ActivityRepository

    @Autowired
    private lateinit var activityMapper: ActivityMapper

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
    private lateinit var restActivityMockMvc: MockMvc

    private lateinit var activity: Activity

    @BeforeEach
    fun initTest() {
        activity = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createActivity() {
        val databaseSizeBeforeCreate = activityRepository.findAll().size

        // Create the Activity
        val activityDTO = activityMapper.toDto(activity)
        restActivityMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(activityDTO))
        ).andExpect(status().isCreated)

        // Validate the Activity in the database
        val activityList = activityRepository.findAll()
        assertThat(activityList).hasSize(databaseSizeBeforeCreate + 1)
        val testActivity = activityList[activityList.size - 1]

        assertThat(testActivity.date).isEqualTo(DEFAULT_DATE)
        assertThat(testActivity.amount?.stripTrailingZeros()).isEqualTo(DEFAULT_AMOUNT.stripTrailingZeros())
        assertThat(testActivity.remarks).isEqualTo(DEFAULT_REMARKS)
        assertThat(testActivity.type).isEqualTo(DEFAULT_TYPE)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createActivityWithExistingId() {
        // Create the Activity with an existing ID
        activity.id = 1L
        val activityDTO = activityMapper.toDto(activity)

        val databaseSizeBeforeCreate = activityRepository.findAll().size

        // An entity with an existing ID cannot be created, so this API call must fail
        restActivityMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(activityDTO))
        ).andExpect(status().isBadRequest)

        // Validate the Activity in the database
        val activityList = activityRepository.findAll()
        assertThat(activityList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun checkDateIsRequired() {
        val databaseSizeBeforeTest = activityRepository.findAll().size
        // set the field null
        activity.date = null

        // Create the Activity, which fails.
        val activityDTO = activityMapper.toDto(activity)

        restActivityMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(activityDTO))
        ).andExpect(status().isBadRequest)

        val activityList = activityRepository.findAll()
        assertThat(activityList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun checkAmountIsRequired() {
        val databaseSizeBeforeTest = activityRepository.findAll().size
        // set the field null
        activity.amount = null

        // Create the Activity, which fails.
        val activityDTO = activityMapper.toDto(activity)

        restActivityMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(activityDTO))
        ).andExpect(status().isBadRequest)

        val activityList = activityRepository.findAll()
        assertThat(activityList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun checkTypeIsRequired() {
        val databaseSizeBeforeTest = activityRepository.findAll().size
        // set the field null
        activity.type = null

        // Create the Activity, which fails.
        val activityDTO = activityMapper.toDto(activity)

        restActivityMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(activityDTO))
        ).andExpect(status().isBadRequest)

        val activityList = activityRepository.findAll()
        assertThat(activityList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllActivities() {
        // Initialize the database
        activityRepository.saveAndFlush(activity)

        // Get all the activityList
        restActivityMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(activity.id?.toInt())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].remarks").value(hasItem(DEFAULT_REMARKS)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getActivity() {
        // Initialize the database
        activityRepository.saveAndFlush(activity)

        val id = activity.id
        assertNotNull(id)

        // Get the activity
        restActivityMockMvc.perform(get(ENTITY_API_URL_ID, activity.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(activity.id?.toInt()))
            .andExpect(jsonPath("$.date").value(sameInstant(DEFAULT_DATE)))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.remarks").value(DEFAULT_REMARKS))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getActivitiesByIdFiltering() {
        // Initialize the database
        activityRepository.saveAndFlush(activity)
        val id = activity.id

        defaultActivityShouldBeFound("id.equals=" + id)
        defaultActivityShouldNotBeFound("id.notEquals=" + id)
        defaultActivityShouldBeFound("id.greaterThanOrEqual=" + id)
        defaultActivityShouldNotBeFound("id.greaterThan=" + id)

        defaultActivityShouldBeFound("id.lessThanOrEqual=" + id)
        defaultActivityShouldNotBeFound("id.lessThan=" + id)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllActivitiesByDateIsEqualToSomething() {
        // Initialize the database
        activityRepository.saveAndFlush(activity)

        // Get all the activityList where date equals to DEFAULT_DATE
        defaultActivityShouldBeFound("date.equals=$DEFAULT_DATE")

        // Get all the activityList where date equals to UPDATED_DATE
        defaultActivityShouldNotBeFound("date.equals=$UPDATED_DATE")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllActivitiesByDateIsNotEqualToSomething() {
        // Initialize the database
        activityRepository.saveAndFlush(activity)

        // Get all the activityList where date not equals to DEFAULT_DATE
        defaultActivityShouldNotBeFound("date.notEquals=" + DEFAULT_DATE)

        // Get all the activityList where date not equals to UPDATED_DATE
        defaultActivityShouldBeFound("date.notEquals=" + UPDATED_DATE)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllActivitiesByDateIsInShouldWork() {
        // Initialize the database
        activityRepository.saveAndFlush(activity)

        // Get all the activityList where date in DEFAULT_DATE or UPDATED_DATE
        defaultActivityShouldBeFound("date.in=$DEFAULT_DATE,$UPDATED_DATE")

        // Get all the activityList where date equals to UPDATED_DATE
        defaultActivityShouldNotBeFound("date.in=$UPDATED_DATE")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllActivitiesByDateIsNullOrNotNull() {
        // Initialize the database
        activityRepository.saveAndFlush(activity)

        // Get all the activityList where date is not null
        defaultActivityShouldBeFound("date.specified=true")

        // Get all the activityList where date is null
        defaultActivityShouldNotBeFound("date.specified=false")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllActivitiesByDateIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        activityRepository.saveAndFlush(activity)

        // Get all the activityList where date is greater than or equal to DEFAULT_DATE
        defaultActivityShouldBeFound("date.greaterThanOrEqual=$DEFAULT_DATE")

        // Get all the activityList where date is greater than or equal to UPDATED_DATE
        defaultActivityShouldNotBeFound("date.greaterThanOrEqual=$UPDATED_DATE")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllActivitiesByDateIsLessThanOrEqualToSomething() {
        // Initialize the database
        activityRepository.saveAndFlush(activity)

        // Get all the activityList where date is less than or equal to DEFAULT_DATE
        defaultActivityShouldBeFound("date.lessThanOrEqual=$DEFAULT_DATE")

        // Get all the activityList where date is less than or equal to SMALLER_DATE
        defaultActivityShouldNotBeFound("date.lessThanOrEqual=$SMALLER_DATE")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllActivitiesByDateIsLessThanSomething() {
        // Initialize the database
        activityRepository.saveAndFlush(activity)

        // Get all the activityList where date is less than DEFAULT_DATE
        defaultActivityShouldNotBeFound("date.lessThan=$DEFAULT_DATE")

        // Get all the activityList where date is less than UPDATED_DATE
        defaultActivityShouldBeFound("date.lessThan=$UPDATED_DATE")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllActivitiesByDateIsGreaterThanSomething() {
        // Initialize the database
        activityRepository.saveAndFlush(activity)

        // Get all the activityList where date is greater than DEFAULT_DATE
        defaultActivityShouldNotBeFound("date.greaterThan=$DEFAULT_DATE")

        // Get all the activityList where date is greater than SMALLER_DATE
        defaultActivityShouldBeFound("date.greaterThan=$SMALLER_DATE")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllActivitiesByAmountIsEqualToSomething() {
        // Initialize the database
        activityRepository.saveAndFlush(activity)

        // Get all the activityList where amount equals to DEFAULT_AMOUNT
        defaultActivityShouldBeFound("amount.equals=$DEFAULT_AMOUNT")

        // Get all the activityList where amount equals to UPDATED_AMOUNT
        defaultActivityShouldNotBeFound("amount.equals=$UPDATED_AMOUNT")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllActivitiesByAmountIsNotEqualToSomething() {
        // Initialize the database
        activityRepository.saveAndFlush(activity)

        // Get all the activityList where amount not equals to DEFAULT_AMOUNT
        defaultActivityShouldNotBeFound("amount.notEquals=" + DEFAULT_AMOUNT)

        // Get all the activityList where amount not equals to UPDATED_AMOUNT
        defaultActivityShouldBeFound("amount.notEquals=" + UPDATED_AMOUNT)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllActivitiesByAmountIsInShouldWork() {
        // Initialize the database
        activityRepository.saveAndFlush(activity)

        // Get all the activityList where amount in DEFAULT_AMOUNT or UPDATED_AMOUNT
        defaultActivityShouldBeFound("amount.in=$DEFAULT_AMOUNT,$UPDATED_AMOUNT")

        // Get all the activityList where amount equals to UPDATED_AMOUNT
        defaultActivityShouldNotBeFound("amount.in=$UPDATED_AMOUNT")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllActivitiesByAmountIsNullOrNotNull() {
        // Initialize the database
        activityRepository.saveAndFlush(activity)

        // Get all the activityList where amount is not null
        defaultActivityShouldBeFound("amount.specified=true")

        // Get all the activityList where amount is null
        defaultActivityShouldNotBeFound("amount.specified=false")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllActivitiesByAmountIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        activityRepository.saveAndFlush(activity)

        // Get all the activityList where amount is greater than or equal to DEFAULT_AMOUNT
        defaultActivityShouldBeFound("amount.greaterThanOrEqual=$DEFAULT_AMOUNT")

        // Get all the activityList where amount is greater than or equal to UPDATED_AMOUNT
        defaultActivityShouldNotBeFound("amount.greaterThanOrEqual=$UPDATED_AMOUNT")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllActivitiesByAmountIsLessThanOrEqualToSomething() {
        // Initialize the database
        activityRepository.saveAndFlush(activity)

        // Get all the activityList where amount is less than or equal to DEFAULT_AMOUNT
        defaultActivityShouldBeFound("amount.lessThanOrEqual=$DEFAULT_AMOUNT")

        // Get all the activityList where amount is less than or equal to SMALLER_AMOUNT
        defaultActivityShouldNotBeFound("amount.lessThanOrEqual=$SMALLER_AMOUNT")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllActivitiesByAmountIsLessThanSomething() {
        // Initialize the database
        activityRepository.saveAndFlush(activity)

        // Get all the activityList where amount is less than DEFAULT_AMOUNT
        defaultActivityShouldNotBeFound("amount.lessThan=$DEFAULT_AMOUNT")

        // Get all the activityList where amount is less than UPDATED_AMOUNT
        defaultActivityShouldBeFound("amount.lessThan=$UPDATED_AMOUNT")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllActivitiesByAmountIsGreaterThanSomething() {
        // Initialize the database
        activityRepository.saveAndFlush(activity)

        // Get all the activityList where amount is greater than DEFAULT_AMOUNT
        defaultActivityShouldNotBeFound("amount.greaterThan=$DEFAULT_AMOUNT")

        // Get all the activityList where amount is greater than SMALLER_AMOUNT
        defaultActivityShouldBeFound("amount.greaterThan=$SMALLER_AMOUNT")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllActivitiesByRemarksIsEqualToSomething() {
        // Initialize the database
        activityRepository.saveAndFlush(activity)

        // Get all the activityList where remarks equals to DEFAULT_REMARKS
        defaultActivityShouldBeFound("remarks.equals=$DEFAULT_REMARKS")

        // Get all the activityList where remarks equals to UPDATED_REMARKS
        defaultActivityShouldNotBeFound("remarks.equals=$UPDATED_REMARKS")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllActivitiesByRemarksIsNotEqualToSomething() {
        // Initialize the database
        activityRepository.saveAndFlush(activity)

        // Get all the activityList where remarks not equals to DEFAULT_REMARKS
        defaultActivityShouldNotBeFound("remarks.notEquals=" + DEFAULT_REMARKS)

        // Get all the activityList where remarks not equals to UPDATED_REMARKS
        defaultActivityShouldBeFound("remarks.notEquals=" + UPDATED_REMARKS)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllActivitiesByRemarksIsInShouldWork() {
        // Initialize the database
        activityRepository.saveAndFlush(activity)

        // Get all the activityList where remarks in DEFAULT_REMARKS or UPDATED_REMARKS
        defaultActivityShouldBeFound("remarks.in=$DEFAULT_REMARKS,$UPDATED_REMARKS")

        // Get all the activityList where remarks equals to UPDATED_REMARKS
        defaultActivityShouldNotBeFound("remarks.in=$UPDATED_REMARKS")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllActivitiesByRemarksIsNullOrNotNull() {
        // Initialize the database
        activityRepository.saveAndFlush(activity)

        // Get all the activityList where remarks is not null
        defaultActivityShouldBeFound("remarks.specified=true")

        // Get all the activityList where remarks is null
        defaultActivityShouldNotBeFound("remarks.specified=false")
    }
    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllActivitiesByRemarksContainsSomething() {
        // Initialize the database
        activityRepository.saveAndFlush(activity)

        // Get all the activityList where remarks contains DEFAULT_REMARKS
        defaultActivityShouldBeFound("remarks.contains=" + DEFAULT_REMARKS)

        // Get all the activityList where remarks contains UPDATED_REMARKS
        defaultActivityShouldNotBeFound("remarks.contains=" + UPDATED_REMARKS)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllActivitiesByRemarksNotContainsSomething() {
        // Initialize the database
        activityRepository.saveAndFlush(activity)

        // Get all the activityList where remarks does not contain DEFAULT_REMARKS
        defaultActivityShouldNotBeFound("remarks.doesNotContain=" + DEFAULT_REMARKS)

        // Get all the activityList where remarks does not contain UPDATED_REMARKS
        defaultActivityShouldBeFound("remarks.doesNotContain=" + UPDATED_REMARKS)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllActivitiesByTypeIsEqualToSomething() {
        // Initialize the database
        activityRepository.saveAndFlush(activity)

        // Get all the activityList where type equals to DEFAULT_TYPE
        defaultActivityShouldBeFound("type.equals=$DEFAULT_TYPE")

        // Get all the activityList where type equals to UPDATED_TYPE
        defaultActivityShouldNotBeFound("type.equals=$UPDATED_TYPE")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllActivitiesByTypeIsNotEqualToSomething() {
        // Initialize the database
        activityRepository.saveAndFlush(activity)

        // Get all the activityList where type not equals to DEFAULT_TYPE
        defaultActivityShouldNotBeFound("type.notEquals=" + DEFAULT_TYPE)

        // Get all the activityList where type not equals to UPDATED_TYPE
        defaultActivityShouldBeFound("type.notEquals=" + UPDATED_TYPE)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllActivitiesByTypeIsInShouldWork() {
        // Initialize the database
        activityRepository.saveAndFlush(activity)

        // Get all the activityList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultActivityShouldBeFound("type.in=$DEFAULT_TYPE,$UPDATED_TYPE")

        // Get all the activityList where type equals to UPDATED_TYPE
        defaultActivityShouldNotBeFound("type.in=$UPDATED_TYPE")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllActivitiesByTypeIsNullOrNotNull() {
        // Initialize the database
        activityRepository.saveAndFlush(activity)

        // Get all the activityList where type is not null
        defaultActivityShouldBeFound("type.specified=true")

        // Get all the activityList where type is null
        defaultActivityShouldNotBeFound("type.specified=false")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllActivitiesByUserIsEqualToSomething() {
        // Initialize the database
        activityRepository.saveAndFlush(activity)
        val user = UserResourceIT.createEntity(em)
        em.persist(user)
        em.flush()
        activity.user = user
        activityRepository.saveAndFlush(activity)
        val userId = user.id

        // Get all the activityList where user equals to userId
        defaultActivityShouldBeFound("userId.equals=$userId")

        // Get all the activityList where user equals to (userId?.plus(1))
        defaultActivityShouldNotBeFound("userId.equals=${(userId?.plus(1))}")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllActivitiesByWalletIsEqualToSomething() {
        // Initialize the database
        activityRepository.saveAndFlush(activity)
        val wallet = WalletResourceIT.createEntity(em)
        em.persist(wallet)
        em.flush()
        activity.wallet = wallet
        activityRepository.saveAndFlush(activity)
        val walletId = wallet.id

        // Get all the activityList where wallet equals to walletId
        defaultActivityShouldBeFound("walletId.equals=$walletId")

        // Get all the activityList where wallet equals to (walletId?.plus(1))
        defaultActivityShouldNotBeFound("walletId.equals=${(walletId?.plus(1))}")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllActivitiesByCategoryIsEqualToSomething() {
        // Initialize the database
        activityRepository.saveAndFlush(activity)
        val category = CategoryResourceIT.createEntity(em)
        em.persist(category)
        em.flush()
        activity.category = category
        activityRepository.saveAndFlush(activity)
        val categoryId = category.id

        // Get all the activityList where category equals to categoryId
        defaultActivityShouldBeFound("categoryId.equals=$categoryId")

        // Get all the activityList where category equals to (categoryId?.plus(1))
        defaultActivityShouldNotBeFound("categoryId.equals=${(categoryId?.plus(1))}")
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */

    @Throws(Exception::class)
    private fun defaultActivityShouldBeFound(filter: String) {
        restActivityMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(activity.id?.toInt())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].remarks").value(hasItem(DEFAULT_REMARKS)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))

        // Check, that the count call also returns 1
        restActivityMockMvc.perform(get(ENTITY_API_URL + "/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"))
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    @Throws(Exception::class)
    private fun defaultActivityShouldNotBeFound(filter: String) {
        restActivityMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)

        // Check, that the count call also returns 0
        restActivityMockMvc.perform(get(ENTITY_API_URL + "/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"))
    }
    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingActivity() {
        // Get the activity
        restActivityMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun putNewActivity() {
        // Initialize the database
        activityRepository.saveAndFlush(activity)

        val databaseSizeBeforeUpdate = activityRepository.findAll().size

        // Update the activity
        val updatedActivity = activityRepository.findById(activity.id).get()
        // Disconnect from session so that the updates on updatedActivity are not directly saved in db
        em.detach(updatedActivity)
        updatedActivity.date = UPDATED_DATE
        updatedActivity.amount = UPDATED_AMOUNT
        updatedActivity.remarks = UPDATED_REMARKS
        updatedActivity.type = UPDATED_TYPE
        val activityDTO = activityMapper.toDto(updatedActivity)

        restActivityMockMvc.perform(
            put(ENTITY_API_URL_ID, activityDTO.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(activityDTO))
        ).andExpect(status().isOk)

        // Validate the Activity in the database
        val activityList = activityRepository.findAll()
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate)
        val testActivity = activityList[activityList.size - 1]
        assertThat(testActivity.date).isEqualTo(UPDATED_DATE)
        assertThat(testActivity.amount?.stripTrailingZeros()).isEqualTo(UPDATED_AMOUNT.stripTrailingZeros())
        assertThat(testActivity.remarks).isEqualTo(UPDATED_REMARKS)
        assertThat(testActivity.type).isEqualTo(UPDATED_TYPE)
    }

    @Test
    @Transactional
    fun putNonExistingActivity() {
        val databaseSizeBeforeUpdate = activityRepository.findAll().size
        activity.id = count.incrementAndGet()

        // Create the Activity
        val activityDTO = activityMapper.toDto(activity)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restActivityMockMvc.perform(
            put(ENTITY_API_URL_ID, activityDTO.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(activityDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the Activity in the database
        val activityList = activityRepository.findAll()
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithIdMismatchActivity() {
        val databaseSizeBeforeUpdate = activityRepository.findAll().size
        activity.id = count.incrementAndGet()

        // Create the Activity
        val activityDTO = activityMapper.toDto(activity)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActivityMockMvc.perform(
            put(ENTITY_API_URL_ID, count.incrementAndGet())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(activityDTO))
        ).andExpect(status().isBadRequest)

        // Validate the Activity in the database
        val activityList = activityRepository.findAll()
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithMissingIdPathParamActivity() {
        val databaseSizeBeforeUpdate = activityRepository.findAll().size
        activity.id = count.incrementAndGet()

        // Create the Activity
        val activityDTO = activityMapper.toDto(activity)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActivityMockMvc.perform(
            put(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(activityDTO))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the Activity in the database
        val activityList = activityRepository.findAll()
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun partialUpdateActivityWithPatch() {

        // Initialize the database
        activityRepository.saveAndFlush(activity)

        val databaseSizeBeforeUpdate = activityRepository.findAll().size

// Update the activity using partial update
        val partialUpdatedActivity = Activity().apply {
            id = activity.id

            remarks = UPDATED_REMARKS
        }

        restActivityMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedActivity.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedActivity))
        )
            .andExpect(status().isOk)

// Validate the Activity in the database
        val activityList = activityRepository.findAll()
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate)
        val testActivity = activityList.last()
        assertThat(testActivity.date).isEqualTo(DEFAULT_DATE)
        assertThat(testActivity.amount?.stripTrailingZeros()).isEqualByComparingTo(DEFAULT_AMOUNT.stripTrailingZeros())
        assertThat(testActivity.remarks).isEqualTo(UPDATED_REMARKS)
        assertThat(testActivity.type).isEqualTo(DEFAULT_TYPE)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun fullUpdateActivityWithPatch() {

        // Initialize the database
        activityRepository.saveAndFlush(activity)

        val databaseSizeBeforeUpdate = activityRepository.findAll().size

// Update the activity using partial update
        val partialUpdatedActivity = Activity().apply {
            id = activity.id

            date = UPDATED_DATE
            amount = UPDATED_AMOUNT
            remarks = UPDATED_REMARKS
            type = UPDATED_TYPE
        }

        restActivityMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedActivity.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedActivity))
        )
            .andExpect(status().isOk)

// Validate the Activity in the database
        val activityList = activityRepository.findAll()
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate)
        val testActivity = activityList.last()
        assertThat(testActivity.date).isEqualTo(UPDATED_DATE)
        assertThat(testActivity.amount?.stripTrailingZeros()).isEqualByComparingTo(UPDATED_AMOUNT.stripTrailingZeros())
        assertThat(testActivity.remarks).isEqualTo(UPDATED_REMARKS)
        assertThat(testActivity.type).isEqualTo(UPDATED_TYPE)
    }

    @Throws(Exception::class)
    fun patchNonExistingActivity() {
        val databaseSizeBeforeUpdate = activityRepository.findAll().size
        activity.id = count.incrementAndGet()

        // Create the Activity
        val activityDTO = activityMapper.toDto(activity)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restActivityMockMvc.perform(
            patch(ENTITY_API_URL_ID, activityDTO.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(activityDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the Activity in the database
        val activityList = activityRepository.findAll()
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithIdMismatchActivity() {
        val databaseSizeBeforeUpdate = activityRepository.findAll().size
        activity.id = count.incrementAndGet()

        // Create the Activity
        val activityDTO = activityMapper.toDto(activity)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActivityMockMvc.perform(
            patch(ENTITY_API_URL_ID, count.incrementAndGet())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(activityDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the Activity in the database
        val activityList = activityRepository.findAll()
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithMissingIdPathParamActivity() {
        val databaseSizeBeforeUpdate = activityRepository.findAll().size
        activity.id = count.incrementAndGet()

        // Create the Activity
        val activityDTO = activityMapper.toDto(activity)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActivityMockMvc.perform(
            patch(ENTITY_API_URL)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(activityDTO))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the Activity in the database
        val activityList = activityRepository.findAll()
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteActivity() {
        // Initialize the database
        activityRepository.saveAndFlush(activity)

        val databaseSizeBeforeDelete = activityRepository.findAll().size

        // Delete the activity
        restActivityMockMvc.perform(
            delete(ENTITY_API_URL_ID, activity.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val activityList = activityRepository.findAll()
        assertThat(activityList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private val DEFAULT_DATE: ZonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC)
        private val UPDATED_DATE: ZonedDateTime = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0)
        private val SMALLER_DATE: ZonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC)

        private val DEFAULT_AMOUNT: BigDecimal = BigDecimal(1)
        private val UPDATED_AMOUNT: BigDecimal = BigDecimal(2)
        private val SMALLER_AMOUNT: BigDecimal = BigDecimal(1 - 1)

        private const val DEFAULT_REMARKS = "AAAAAAAAAA"
        private const val UPDATED_REMARKS = "BBBBBBBBBB"

        private val DEFAULT_TYPE: ActivityType = ActivityType.INCOME
        private val UPDATED_TYPE: ActivityType = ActivityType.EXPENSE

        private val ENTITY_API_URL: String = "/api/activities"
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
        fun createEntity(em: EntityManager): Activity {
            val activity = Activity(

                date = DEFAULT_DATE,

                amount = DEFAULT_AMOUNT,

                remarks = DEFAULT_REMARKS,

                type = DEFAULT_TYPE

            )

            // Add required entity
            val user = UserResourceIT.createEntity(em)
            em.persist(user)
            em.flush()
            activity.user = user
            // Add required entity
            val wallet: Wallet
            if (em.findAll(Wallet::class).isEmpty()) {
                wallet = WalletResourceIT.createEntity(em)
                em.persist(wallet)
                em.flush()
            } else {
                wallet = em.findAll(Wallet::class).get(0)
            }
            activity.wallet = wallet
            // Add required entity
            val category: Category
            if (em.findAll(Category::class).isEmpty()) {
                category = CategoryResourceIT.createEntity(em)
                em.persist(category)
                em.flush()
            } else {
                category = em.findAll(Category::class).get(0)
            }
            activity.category = category
            return activity
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Activity {
            val activity = Activity(

                date = UPDATED_DATE,

                amount = UPDATED_AMOUNT,

                remarks = UPDATED_REMARKS,

                type = UPDATED_TYPE

            )

            // Add required entity
            val user = UserResourceIT.createEntity(em)
            em.persist(user)
            em.flush()
            activity.user = user
            // Add required entity
            val wallet: Wallet
            if (em.findAll(Wallet::class).isEmpty()) {
                wallet = WalletResourceIT.createUpdatedEntity(em)
                em.persist(wallet)
                em.flush()
            } else {
                wallet = em.findAll(Wallet::class).get(0)
            }
            activity.wallet = wallet
            // Add required entity
            val category: Category
            if (em.findAll(Category::class).isEmpty()) {
                category = CategoryResourceIT.createUpdatedEntity(em)
                em.persist(category)
                em.flush()
            } else {
                category = em.findAll(Category::class).get(0)
            }
            activity.category = category
            return activity
        }
    }
}
