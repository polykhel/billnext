package com.polykhel.billnext.web.rest

import com.polykhel.billnext.IntegrationTest
import com.polykhel.billnext.domain.Wallet
import com.polykhel.billnext.domain.WalletGroup
import com.polykhel.billnext.repository.WalletRepository
import com.polykhel.billnext.service.mapper.WalletMapper
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
import java.util.Random
import java.util.concurrent.atomic.AtomicLong
import javax.persistence.EntityManager
import kotlin.test.assertNotNull

/**
 * Integration tests for the [WalletResource] REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class WalletResourceIT {
    @Autowired
    private lateinit var walletRepository: WalletRepository

    @Autowired
    private lateinit var walletMapper: WalletMapper

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
    private lateinit var restWalletMockMvc: MockMvc

    private lateinit var wallet: Wallet

    @BeforeEach
    fun initTest() {
        wallet = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createWallet() {
        val databaseSizeBeforeCreate = walletRepository.findAll().size

        // Create the Wallet
        val walletDTO = walletMapper.toDto(wallet)
        restWalletMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(walletDTO))
        ).andExpect(status().isCreated)

        // Validate the Wallet in the database
        val walletList = walletRepository.findAll()
        assertThat(walletList).hasSize(databaseSizeBeforeCreate + 1)
        val testWallet = walletList[walletList.size - 1]

        assertThat(testWallet.name).isEqualTo(DEFAULT_NAME)
        assertThat(testWallet.amount?.stripTrailingZeros()).isEqualTo(DEFAULT_AMOUNT.stripTrailingZeros())
        assertThat(testWallet.currency).isEqualTo(DEFAULT_CURRENCY)
        assertThat(testWallet.remarks).isEqualTo(DEFAULT_REMARKS)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createWalletWithExistingId() {
        // Create the Wallet with an existing ID
        wallet.id = 1L
        val walletDTO = walletMapper.toDto(wallet)

        val databaseSizeBeforeCreate = walletRepository.findAll().size

        // An entity with an existing ID cannot be created, so this API call must fail
        restWalletMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(walletDTO))
        ).andExpect(status().isBadRequest)

        // Validate the Wallet in the database
        val walletList = walletRepository.findAll()
        assertThat(walletList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun checkNameIsRequired() {
        val databaseSizeBeforeTest = walletRepository.findAll().size
        // set the field null
        wallet.name = null

        // Create the Wallet, which fails.
        val walletDTO = walletMapper.toDto(wallet)

        restWalletMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(walletDTO))
        ).andExpect(status().isBadRequest)

        val walletList = walletRepository.findAll()
        assertThat(walletList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun checkAmountIsRequired() {
        val databaseSizeBeforeTest = walletRepository.findAll().size
        // set the field null
        wallet.amount = null

        // Create the Wallet, which fails.
        val walletDTO = walletMapper.toDto(wallet)

        restWalletMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(walletDTO))
        ).andExpect(status().isBadRequest)

        val walletList = walletRepository.findAll()
        assertThat(walletList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllWallets() {
        // Initialize the database
        walletRepository.saveAndFlush(wallet)

        // Get all the walletList
        restWalletMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(wallet.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY)))
            .andExpect(jsonPath("$.[*].remarks").value(hasItem(DEFAULT_REMARKS)))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getWallet() {
        // Initialize the database
        walletRepository.saveAndFlush(wallet)

        val id = wallet.id
        assertNotNull(id)

        // Get the wallet
        restWalletMockMvc.perform(get(ENTITY_API_URL_ID, wallet.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(wallet.id?.toInt()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.currency").value(DEFAULT_CURRENCY))
            .andExpect(jsonPath("$.remarks").value(DEFAULT_REMARKS))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getWalletsByIdFiltering() {
        // Initialize the database
        walletRepository.saveAndFlush(wallet)
        val id = wallet.id

        defaultWalletShouldBeFound("id.equals=" + id)
        defaultWalletShouldNotBeFound("id.notEquals=" + id)
        defaultWalletShouldBeFound("id.greaterThanOrEqual=" + id)
        defaultWalletShouldNotBeFound("id.greaterThan=" + id)

        defaultWalletShouldBeFound("id.lessThanOrEqual=" + id)
        defaultWalletShouldNotBeFound("id.lessThan=" + id)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllWalletsByNameIsEqualToSomething() {
        // Initialize the database
        walletRepository.saveAndFlush(wallet)

        // Get all the walletList where name equals to DEFAULT_NAME
        defaultWalletShouldBeFound("name.equals=$DEFAULT_NAME")

        // Get all the walletList where name equals to UPDATED_NAME
        defaultWalletShouldNotBeFound("name.equals=$UPDATED_NAME")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllWalletsByNameIsNotEqualToSomething() {
        // Initialize the database
        walletRepository.saveAndFlush(wallet)

        // Get all the walletList where name not equals to DEFAULT_NAME
        defaultWalletShouldNotBeFound("name.notEquals=" + DEFAULT_NAME)

        // Get all the walletList where name not equals to UPDATED_NAME
        defaultWalletShouldBeFound("name.notEquals=" + UPDATED_NAME)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllWalletsByNameIsInShouldWork() {
        // Initialize the database
        walletRepository.saveAndFlush(wallet)

        // Get all the walletList where name in DEFAULT_NAME or UPDATED_NAME
        defaultWalletShouldBeFound("name.in=$DEFAULT_NAME,$UPDATED_NAME")

        // Get all the walletList where name equals to UPDATED_NAME
        defaultWalletShouldNotBeFound("name.in=$UPDATED_NAME")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllWalletsByNameIsNullOrNotNull() {
        // Initialize the database
        walletRepository.saveAndFlush(wallet)

        // Get all the walletList where name is not null
        defaultWalletShouldBeFound("name.specified=true")

        // Get all the walletList where name is null
        defaultWalletShouldNotBeFound("name.specified=false")
    }
    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllWalletsByNameContainsSomething() {
        // Initialize the database
        walletRepository.saveAndFlush(wallet)

        // Get all the walletList where name contains DEFAULT_NAME
        defaultWalletShouldBeFound("name.contains=" + DEFAULT_NAME)

        // Get all the walletList where name contains UPDATED_NAME
        defaultWalletShouldNotBeFound("name.contains=" + UPDATED_NAME)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllWalletsByNameNotContainsSomething() {
        // Initialize the database
        walletRepository.saveAndFlush(wallet)

        // Get all the walletList where name does not contain DEFAULT_NAME
        defaultWalletShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME)

        // Get all the walletList where name does not contain UPDATED_NAME
        defaultWalletShouldBeFound("name.doesNotContain=" + UPDATED_NAME)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllWalletsByAmountIsEqualToSomething() {
        // Initialize the database
        walletRepository.saveAndFlush(wallet)

        // Get all the walletList where amount equals to DEFAULT_AMOUNT
        defaultWalletShouldBeFound("amount.equals=$DEFAULT_AMOUNT")

        // Get all the walletList where amount equals to UPDATED_AMOUNT
        defaultWalletShouldNotBeFound("amount.equals=$UPDATED_AMOUNT")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllWalletsByAmountIsNotEqualToSomething() {
        // Initialize the database
        walletRepository.saveAndFlush(wallet)

        // Get all the walletList where amount not equals to DEFAULT_AMOUNT
        defaultWalletShouldNotBeFound("amount.notEquals=" + DEFAULT_AMOUNT)

        // Get all the walletList where amount not equals to UPDATED_AMOUNT
        defaultWalletShouldBeFound("amount.notEquals=" + UPDATED_AMOUNT)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllWalletsByAmountIsInShouldWork() {
        // Initialize the database
        walletRepository.saveAndFlush(wallet)

        // Get all the walletList where amount in DEFAULT_AMOUNT or UPDATED_AMOUNT
        defaultWalletShouldBeFound("amount.in=$DEFAULT_AMOUNT,$UPDATED_AMOUNT")

        // Get all the walletList where amount equals to UPDATED_AMOUNT
        defaultWalletShouldNotBeFound("amount.in=$UPDATED_AMOUNT")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllWalletsByAmountIsNullOrNotNull() {
        // Initialize the database
        walletRepository.saveAndFlush(wallet)

        // Get all the walletList where amount is not null
        defaultWalletShouldBeFound("amount.specified=true")

        // Get all the walletList where amount is null
        defaultWalletShouldNotBeFound("amount.specified=false")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllWalletsByAmountIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        walletRepository.saveAndFlush(wallet)

        // Get all the walletList where amount is greater than or equal to DEFAULT_AMOUNT
        defaultWalletShouldBeFound("amount.greaterThanOrEqual=$DEFAULT_AMOUNT")

        // Get all the walletList where amount is greater than or equal to UPDATED_AMOUNT
        defaultWalletShouldNotBeFound("amount.greaterThanOrEqual=$UPDATED_AMOUNT")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllWalletsByAmountIsLessThanOrEqualToSomething() {
        // Initialize the database
        walletRepository.saveAndFlush(wallet)

        // Get all the walletList where amount is less than or equal to DEFAULT_AMOUNT
        defaultWalletShouldBeFound("amount.lessThanOrEqual=$DEFAULT_AMOUNT")

        // Get all the walletList where amount is less than or equal to SMALLER_AMOUNT
        defaultWalletShouldNotBeFound("amount.lessThanOrEqual=$SMALLER_AMOUNT")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllWalletsByAmountIsLessThanSomething() {
        // Initialize the database
        walletRepository.saveAndFlush(wallet)

        // Get all the walletList where amount is less than DEFAULT_AMOUNT
        defaultWalletShouldNotBeFound("amount.lessThan=$DEFAULT_AMOUNT")

        // Get all the walletList where amount is less than UPDATED_AMOUNT
        defaultWalletShouldBeFound("amount.lessThan=$UPDATED_AMOUNT")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllWalletsByAmountIsGreaterThanSomething() {
        // Initialize the database
        walletRepository.saveAndFlush(wallet)

        // Get all the walletList where amount is greater than DEFAULT_AMOUNT
        defaultWalletShouldNotBeFound("amount.greaterThan=$DEFAULT_AMOUNT")

        // Get all the walletList where amount is greater than SMALLER_AMOUNT
        defaultWalletShouldBeFound("amount.greaterThan=$SMALLER_AMOUNT")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllWalletsByCurrencyIsEqualToSomething() {
        // Initialize the database
        walletRepository.saveAndFlush(wallet)

        // Get all the walletList where currency equals to DEFAULT_CURRENCY
        defaultWalletShouldBeFound("currency.equals=$DEFAULT_CURRENCY")

        // Get all the walletList where currency equals to UPDATED_CURRENCY
        defaultWalletShouldNotBeFound("currency.equals=$UPDATED_CURRENCY")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllWalletsByCurrencyIsNotEqualToSomething() {
        // Initialize the database
        walletRepository.saveAndFlush(wallet)

        // Get all the walletList where currency not equals to DEFAULT_CURRENCY
        defaultWalletShouldNotBeFound("currency.notEquals=" + DEFAULT_CURRENCY)

        // Get all the walletList where currency not equals to UPDATED_CURRENCY
        defaultWalletShouldBeFound("currency.notEquals=" + UPDATED_CURRENCY)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllWalletsByCurrencyIsInShouldWork() {
        // Initialize the database
        walletRepository.saveAndFlush(wallet)

        // Get all the walletList where currency in DEFAULT_CURRENCY or UPDATED_CURRENCY
        defaultWalletShouldBeFound("currency.in=$DEFAULT_CURRENCY,$UPDATED_CURRENCY")

        // Get all the walletList where currency equals to UPDATED_CURRENCY
        defaultWalletShouldNotBeFound("currency.in=$UPDATED_CURRENCY")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllWalletsByCurrencyIsNullOrNotNull() {
        // Initialize the database
        walletRepository.saveAndFlush(wallet)

        // Get all the walletList where currency is not null
        defaultWalletShouldBeFound("currency.specified=true")

        // Get all the walletList where currency is null
        defaultWalletShouldNotBeFound("currency.specified=false")
    }
    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllWalletsByCurrencyContainsSomething() {
        // Initialize the database
        walletRepository.saveAndFlush(wallet)

        // Get all the walletList where currency contains DEFAULT_CURRENCY
        defaultWalletShouldBeFound("currency.contains=" + DEFAULT_CURRENCY)

        // Get all the walletList where currency contains UPDATED_CURRENCY
        defaultWalletShouldNotBeFound("currency.contains=" + UPDATED_CURRENCY)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllWalletsByCurrencyNotContainsSomething() {
        // Initialize the database
        walletRepository.saveAndFlush(wallet)

        // Get all the walletList where currency does not contain DEFAULT_CURRENCY
        defaultWalletShouldNotBeFound("currency.doesNotContain=" + DEFAULT_CURRENCY)

        // Get all the walletList where currency does not contain UPDATED_CURRENCY
        defaultWalletShouldBeFound("currency.doesNotContain=" + UPDATED_CURRENCY)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllWalletsByRemarksIsEqualToSomething() {
        // Initialize the database
        walletRepository.saveAndFlush(wallet)

        // Get all the walletList where remarks equals to DEFAULT_REMARKS
        defaultWalletShouldBeFound("remarks.equals=$DEFAULT_REMARKS")

        // Get all the walletList where remarks equals to UPDATED_REMARKS
        defaultWalletShouldNotBeFound("remarks.equals=$UPDATED_REMARKS")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllWalletsByRemarksIsNotEqualToSomething() {
        // Initialize the database
        walletRepository.saveAndFlush(wallet)

        // Get all the walletList where remarks not equals to DEFAULT_REMARKS
        defaultWalletShouldNotBeFound("remarks.notEquals=" + DEFAULT_REMARKS)

        // Get all the walletList where remarks not equals to UPDATED_REMARKS
        defaultWalletShouldBeFound("remarks.notEquals=" + UPDATED_REMARKS)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllWalletsByRemarksIsInShouldWork() {
        // Initialize the database
        walletRepository.saveAndFlush(wallet)

        // Get all the walletList where remarks in DEFAULT_REMARKS or UPDATED_REMARKS
        defaultWalletShouldBeFound("remarks.in=$DEFAULT_REMARKS,$UPDATED_REMARKS")

        // Get all the walletList where remarks equals to UPDATED_REMARKS
        defaultWalletShouldNotBeFound("remarks.in=$UPDATED_REMARKS")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllWalletsByRemarksIsNullOrNotNull() {
        // Initialize the database
        walletRepository.saveAndFlush(wallet)

        // Get all the walletList where remarks is not null
        defaultWalletShouldBeFound("remarks.specified=true")

        // Get all the walletList where remarks is null
        defaultWalletShouldNotBeFound("remarks.specified=false")
    }
    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllWalletsByRemarksContainsSomething() {
        // Initialize the database
        walletRepository.saveAndFlush(wallet)

        // Get all the walletList where remarks contains DEFAULT_REMARKS
        defaultWalletShouldBeFound("remarks.contains=" + DEFAULT_REMARKS)

        // Get all the walletList where remarks contains UPDATED_REMARKS
        defaultWalletShouldNotBeFound("remarks.contains=" + UPDATED_REMARKS)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllWalletsByRemarksNotContainsSomething() {
        // Initialize the database
        walletRepository.saveAndFlush(wallet)

        // Get all the walletList where remarks does not contain DEFAULT_REMARKS
        defaultWalletShouldNotBeFound("remarks.doesNotContain=" + DEFAULT_REMARKS)

        // Get all the walletList where remarks does not contain UPDATED_REMARKS
        defaultWalletShouldBeFound("remarks.doesNotContain=" + UPDATED_REMARKS)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllWalletsByWalletGroupIsEqualToSomething() {
        // Initialize the database
        walletRepository.saveAndFlush(wallet)
        val walletGroup = WalletGroupResourceIT.createEntity(em)
        em.persist(walletGroup)
        em.flush()
        wallet.walletGroup = walletGroup
        walletRepository.saveAndFlush(wallet)
        val walletGroupId = walletGroup.id

        // Get all the walletList where walletGroup equals to walletGroupId
        defaultWalletShouldBeFound("walletGroupId.equals=$walletGroupId")

        // Get all the walletList where walletGroup equals to (walletGroupId?.plus(1))
        defaultWalletShouldNotBeFound("walletGroupId.equals=${(walletGroupId?.plus(1))}")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllWalletsByActivityIsEqualToSomething() {
        // Initialize the database
        walletRepository.saveAndFlush(wallet)
        val activity = ActivityResourceIT.createEntity(em)
        em.persist(activity)
        em.flush()
        wallet.addActivity(activity)
        walletRepository.saveAndFlush(wallet)
        val activityId = activity.id

        // Get all the walletList where activity equals to activityId
        defaultWalletShouldBeFound("activityId.equals=$activityId")

        // Get all the walletList where activity equals to (activityId?.plus(1))
        defaultWalletShouldNotBeFound("activityId.equals=${(activityId?.plus(1))}")
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */

    @Throws(Exception::class)
    private fun defaultWalletShouldBeFound(filter: String) {
        restWalletMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(wallet.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY)))
            .andExpect(jsonPath("$.[*].remarks").value(hasItem(DEFAULT_REMARKS)))

        // Check, that the count call also returns 1
        restWalletMockMvc.perform(get(ENTITY_API_URL + "/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"))
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    @Throws(Exception::class)
    private fun defaultWalletShouldNotBeFound(filter: String) {
        restWalletMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)

        // Check, that the count call also returns 0
        restWalletMockMvc.perform(get(ENTITY_API_URL + "/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"))
    }
    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingWallet() {
        // Get the wallet
        restWalletMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun putNewWallet() {
        // Initialize the database
        walletRepository.saveAndFlush(wallet)

        val databaseSizeBeforeUpdate = walletRepository.findAll().size

        // Update the wallet
        val updatedWallet = walletRepository.findById(wallet.id).get()
        // Disconnect from session so that the updates on updatedWallet are not directly saved in db
        em.detach(updatedWallet)
        updatedWallet.name = UPDATED_NAME
        updatedWallet.amount = UPDATED_AMOUNT
        updatedWallet.currency = UPDATED_CURRENCY
        updatedWallet.remarks = UPDATED_REMARKS
        val walletDTO = walletMapper.toDto(updatedWallet)

        restWalletMockMvc.perform(
            put(ENTITY_API_URL_ID, walletDTO.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(walletDTO))
        ).andExpect(status().isOk)

        // Validate the Wallet in the database
        val walletList = walletRepository.findAll()
        assertThat(walletList).hasSize(databaseSizeBeforeUpdate)
        val testWallet = walletList[walletList.size - 1]
        assertThat(testWallet.name).isEqualTo(UPDATED_NAME)
        assertThat(testWallet.amount?.stripTrailingZeros()).isEqualTo(UPDATED_AMOUNT.stripTrailingZeros())
        assertThat(testWallet.currency).isEqualTo(UPDATED_CURRENCY)
        assertThat(testWallet.remarks).isEqualTo(UPDATED_REMARKS)
    }

    @Test
    @Transactional
    fun putNonExistingWallet() {
        val databaseSizeBeforeUpdate = walletRepository.findAll().size
        wallet.id = count.incrementAndGet()

        // Create the Wallet
        val walletDTO = walletMapper.toDto(wallet)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWalletMockMvc.perform(
            put(ENTITY_API_URL_ID, walletDTO.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(walletDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the Wallet in the database
        val walletList = walletRepository.findAll()
        assertThat(walletList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithIdMismatchWallet() {
        val databaseSizeBeforeUpdate = walletRepository.findAll().size
        wallet.id = count.incrementAndGet()

        // Create the Wallet
        val walletDTO = walletMapper.toDto(wallet)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWalletMockMvc.perform(
            put(ENTITY_API_URL_ID, count.incrementAndGet())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(walletDTO))
        ).andExpect(status().isBadRequest)

        // Validate the Wallet in the database
        val walletList = walletRepository.findAll()
        assertThat(walletList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithMissingIdPathParamWallet() {
        val databaseSizeBeforeUpdate = walletRepository.findAll().size
        wallet.id = count.incrementAndGet()

        // Create the Wallet
        val walletDTO = walletMapper.toDto(wallet)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWalletMockMvc.perform(
            put(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(walletDTO))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the Wallet in the database
        val walletList = walletRepository.findAll()
        assertThat(walletList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun partialUpdateWalletWithPatch() {

        // Initialize the database
        walletRepository.saveAndFlush(wallet)

        val databaseSizeBeforeUpdate = walletRepository.findAll().size

// Update the wallet using partial update
        val partialUpdatedWallet = Wallet().apply {
            id = wallet.id

            amount = UPDATED_AMOUNT
            currency = UPDATED_CURRENCY
        }

        restWalletMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedWallet.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedWallet))
        )
            .andExpect(status().isOk)

// Validate the Wallet in the database
        val walletList = walletRepository.findAll()
        assertThat(walletList).hasSize(databaseSizeBeforeUpdate)
        val testWallet = walletList.last()
        assertThat(testWallet.name).isEqualTo(DEFAULT_NAME)
        assertThat(testWallet.amount?.stripTrailingZeros()).isEqualByComparingTo(UPDATED_AMOUNT.stripTrailingZeros())
        assertThat(testWallet.currency).isEqualTo(UPDATED_CURRENCY)
        assertThat(testWallet.remarks).isEqualTo(DEFAULT_REMARKS)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun fullUpdateWalletWithPatch() {

        // Initialize the database
        walletRepository.saveAndFlush(wallet)

        val databaseSizeBeforeUpdate = walletRepository.findAll().size

// Update the wallet using partial update
        val partialUpdatedWallet = Wallet().apply {
            id = wallet.id

            name = UPDATED_NAME
            amount = UPDATED_AMOUNT
            currency = UPDATED_CURRENCY
            remarks = UPDATED_REMARKS
        }

        restWalletMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedWallet.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedWallet))
        )
            .andExpect(status().isOk)

// Validate the Wallet in the database
        val walletList = walletRepository.findAll()
        assertThat(walletList).hasSize(databaseSizeBeforeUpdate)
        val testWallet = walletList.last()
        assertThat(testWallet.name).isEqualTo(UPDATED_NAME)
        assertThat(testWallet.amount?.stripTrailingZeros()).isEqualByComparingTo(UPDATED_AMOUNT.stripTrailingZeros())
        assertThat(testWallet.currency).isEqualTo(UPDATED_CURRENCY)
        assertThat(testWallet.remarks).isEqualTo(UPDATED_REMARKS)
    }

    @Throws(Exception::class)
    fun patchNonExistingWallet() {
        val databaseSizeBeforeUpdate = walletRepository.findAll().size
        wallet.id = count.incrementAndGet()

        // Create the Wallet
        val walletDTO = walletMapper.toDto(wallet)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWalletMockMvc.perform(
            patch(ENTITY_API_URL_ID, walletDTO.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(walletDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the Wallet in the database
        val walletList = walletRepository.findAll()
        assertThat(walletList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithIdMismatchWallet() {
        val databaseSizeBeforeUpdate = walletRepository.findAll().size
        wallet.id = count.incrementAndGet()

        // Create the Wallet
        val walletDTO = walletMapper.toDto(wallet)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWalletMockMvc.perform(
            patch(ENTITY_API_URL_ID, count.incrementAndGet())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(walletDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the Wallet in the database
        val walletList = walletRepository.findAll()
        assertThat(walletList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithMissingIdPathParamWallet() {
        val databaseSizeBeforeUpdate = walletRepository.findAll().size
        wallet.id = count.incrementAndGet()

        // Create the Wallet
        val walletDTO = walletMapper.toDto(wallet)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWalletMockMvc.perform(
            patch(ENTITY_API_URL)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(walletDTO))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the Wallet in the database
        val walletList = walletRepository.findAll()
        assertThat(walletList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteWallet() {
        // Initialize the database
        walletRepository.saveAndFlush(wallet)

        val databaseSizeBeforeDelete = walletRepository.findAll().size

        // Delete the wallet
        restWalletMockMvc.perform(
            delete(ENTITY_API_URL_ID, wallet.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val walletList = walletRepository.findAll()
        assertThat(walletList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_NAME = "AAAAAAAAAA"
        private const val UPDATED_NAME = "BBBBBBBBBB"

        private val DEFAULT_AMOUNT: BigDecimal = BigDecimal(1)
        private val UPDATED_AMOUNT: BigDecimal = BigDecimal(2)
        private val SMALLER_AMOUNT: BigDecimal = BigDecimal(1 - 1)

        private const val DEFAULT_CURRENCY = "AAAAAAAAAA"
        private const val UPDATED_CURRENCY = "BBBBBBBBBB"

        private const val DEFAULT_REMARKS = "AAAAAAAAAA"
        private const val UPDATED_REMARKS = "BBBBBBBBBB"

        private val ENTITY_API_URL: String = "/api/wallets"
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
        fun createEntity(em: EntityManager): Wallet {
            val wallet = Wallet(

                name = DEFAULT_NAME,

                amount = DEFAULT_AMOUNT,

                currency = DEFAULT_CURRENCY,

                remarks = DEFAULT_REMARKS

            )

            // Add required entity
            val walletGroup: WalletGroup
            if (em.findAll(WalletGroup::class).isEmpty()) {
                walletGroup = WalletGroupResourceIT.createEntity(em)
                em.persist(walletGroup)
                em.flush()
            } else {
                walletGroup = em.findAll(WalletGroup::class).get(0)
            }
            wallet.walletGroup = walletGroup
            return wallet
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Wallet {
            val wallet = Wallet(

                name = UPDATED_NAME,

                amount = UPDATED_AMOUNT,

                currency = UPDATED_CURRENCY,

                remarks = UPDATED_REMARKS

            )

            // Add required entity
            val walletGroup: WalletGroup
            if (em.findAll(WalletGroup::class).isEmpty()) {
                walletGroup = WalletGroupResourceIT.createUpdatedEntity(em)
                em.persist(walletGroup)
                em.flush()
            } else {
                walletGroup = em.findAll(WalletGroup::class).get(0)
            }
            wallet.walletGroup = walletGroup
            return wallet
        }
    }
}
