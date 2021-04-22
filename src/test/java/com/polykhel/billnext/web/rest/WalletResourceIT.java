package com.polykhel.billnext.web.rest;

import static com.polykhel.billnext.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.polykhel.billnext.IntegrationTest;
import com.polykhel.billnext.domain.Activity;
import com.polykhel.billnext.domain.Wallet;
import com.polykhel.billnext.domain.WalletGroup;
import com.polykhel.billnext.repository.WalletRepository;
import com.polykhel.billnext.service.criteria.WalletCriteria;
import com.polykhel.billnext.service.dto.WalletDTO;
import com.polykhel.billnext.service.mapper.WalletMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link WalletResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class WalletResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_AMOUNT = new BigDecimal(1 - 1);

    private static final String DEFAULT_CURRENCY = "AAAAAAAAAA";
    private static final String UPDATED_CURRENCY = "BBBBBBBBBB";

    private static final String DEFAULT_REMARKS = "AAAAAAAAAA";
    private static final String UPDATED_REMARKS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/wallets";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletMapper walletMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWalletMockMvc;

    private Wallet wallet;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Wallet createEntity(EntityManager em) {
        Wallet wallet = new Wallet().name(DEFAULT_NAME).amount(DEFAULT_AMOUNT).currency(DEFAULT_CURRENCY).remarks(DEFAULT_REMARKS);
        // Add required entity
        WalletGroup walletGroup;
        if (TestUtil.findAll(em, WalletGroup.class).isEmpty()) {
            walletGroup = WalletGroupResourceIT.createEntity(em);
            em.persist(walletGroup);
            em.flush();
        } else {
            walletGroup = TestUtil.findAll(em, WalletGroup.class).get(0);
        }
        wallet.setWalletGroup(walletGroup);
        return wallet;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Wallet createUpdatedEntity(EntityManager em) {
        Wallet wallet = new Wallet().name(UPDATED_NAME).amount(UPDATED_AMOUNT).currency(UPDATED_CURRENCY).remarks(UPDATED_REMARKS);
        // Add required entity
        WalletGroup walletGroup;
        if (TestUtil.findAll(em, WalletGroup.class).isEmpty()) {
            walletGroup = WalletGroupResourceIT.createUpdatedEntity(em);
            em.persist(walletGroup);
            em.flush();
        } else {
            walletGroup = TestUtil.findAll(em, WalletGroup.class).get(0);
        }
        wallet.setWalletGroup(walletGroup);
        return wallet;
    }

    @BeforeEach
    public void initTest() {
        wallet = createEntity(em);
    }

    @Test
    @Transactional
    void createWallet() throws Exception {
        int databaseSizeBeforeCreate = walletRepository.findAll().size();
        // Create the Wallet
        WalletDTO walletDTO = walletMapper.toDto(wallet);
        restWalletMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(walletDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Wallet in the database
        List<Wallet> walletList = walletRepository.findAll();
        assertThat(walletList).hasSize(databaseSizeBeforeCreate + 1);
        Wallet testWallet = walletList.get(walletList.size() - 1);
        assertThat(testWallet.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testWallet.getAmount()).isEqualByComparingTo(DEFAULT_AMOUNT);
        assertThat(testWallet.getCurrency()).isEqualTo(DEFAULT_CURRENCY);
        assertThat(testWallet.getRemarks()).isEqualTo(DEFAULT_REMARKS);
    }

    @Test
    @Transactional
    void createWalletWithExistingId() throws Exception {
        // Create the Wallet with an existing ID
        wallet.setId(1L);
        WalletDTO walletDTO = walletMapper.toDto(wallet);

        int databaseSizeBeforeCreate = walletRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWalletMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(walletDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Wallet in the database
        List<Wallet> walletList = walletRepository.findAll();
        assertThat(walletList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = walletRepository.findAll().size();
        // set the field null
        wallet.setName(null);

        // Create the Wallet, which fails.
        WalletDTO walletDTO = walletMapper.toDto(wallet);

        restWalletMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(walletDTO))
            )
            .andExpect(status().isBadRequest());

        List<Wallet> walletList = walletRepository.findAll();
        assertThat(walletList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = walletRepository.findAll().size();
        // set the field null
        wallet.setAmount(null);

        // Create the Wallet, which fails.
        WalletDTO walletDTO = walletMapper.toDto(wallet);

        restWalletMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(walletDTO))
            )
            .andExpect(status().isBadRequest());

        List<Wallet> walletList = walletRepository.findAll();
        assertThat(walletList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllWallets() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        // Get all the walletList
        restWalletMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(wallet.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY)))
            .andExpect(jsonPath("$.[*].remarks").value(hasItem(DEFAULT_REMARKS)));
    }

    @Test
    @Transactional
    void getWallet() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        // Get the wallet
        restWalletMockMvc
            .perform(get(ENTITY_API_URL_ID, wallet.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(wallet.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.currency").value(DEFAULT_CURRENCY))
            .andExpect(jsonPath("$.remarks").value(DEFAULT_REMARKS));
    }

    @Test
    @Transactional
    void getWalletsByIdFiltering() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        Long id = wallet.getId();

        defaultWalletShouldBeFound("id.equals=" + id);
        defaultWalletShouldNotBeFound("id.notEquals=" + id);

        defaultWalletShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultWalletShouldNotBeFound("id.greaterThan=" + id);

        defaultWalletShouldBeFound("id.lessThanOrEqual=" + id);
        defaultWalletShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllWalletsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        // Get all the walletList where name equals to DEFAULT_NAME
        defaultWalletShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the walletList where name equals to UPDATED_NAME
        defaultWalletShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWalletsByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        // Get all the walletList where name not equals to DEFAULT_NAME
        defaultWalletShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the walletList where name not equals to UPDATED_NAME
        defaultWalletShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWalletsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        // Get all the walletList where name in DEFAULT_NAME or UPDATED_NAME
        defaultWalletShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the walletList where name equals to UPDATED_NAME
        defaultWalletShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWalletsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        // Get all the walletList where name is not null
        defaultWalletShouldBeFound("name.specified=true");

        // Get all the walletList where name is null
        defaultWalletShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllWalletsByNameContainsSomething() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        // Get all the walletList where name contains DEFAULT_NAME
        defaultWalletShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the walletList where name contains UPDATED_NAME
        defaultWalletShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWalletsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        // Get all the walletList where name does not contain DEFAULT_NAME
        defaultWalletShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the walletList where name does not contain UPDATED_NAME
        defaultWalletShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWalletsByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        // Get all the walletList where amount equals to DEFAULT_AMOUNT
        defaultWalletShouldBeFound("amount.equals=" + DEFAULT_AMOUNT);

        // Get all the walletList where amount equals to UPDATED_AMOUNT
        defaultWalletShouldNotBeFound("amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllWalletsByAmountIsNotEqualToSomething() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        // Get all the walletList where amount not equals to DEFAULT_AMOUNT
        defaultWalletShouldNotBeFound("amount.notEquals=" + DEFAULT_AMOUNT);

        // Get all the walletList where amount not equals to UPDATED_AMOUNT
        defaultWalletShouldBeFound("amount.notEquals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllWalletsByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        // Get all the walletList where amount in DEFAULT_AMOUNT or UPDATED_AMOUNT
        defaultWalletShouldBeFound("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT);

        // Get all the walletList where amount equals to UPDATED_AMOUNT
        defaultWalletShouldNotBeFound("amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllWalletsByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        // Get all the walletList where amount is not null
        defaultWalletShouldBeFound("amount.specified=true");

        // Get all the walletList where amount is null
        defaultWalletShouldNotBeFound("amount.specified=false");
    }

    @Test
    @Transactional
    void getAllWalletsByAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        // Get all the walletList where amount is greater than or equal to DEFAULT_AMOUNT
        defaultWalletShouldBeFound("amount.greaterThanOrEqual=" + DEFAULT_AMOUNT);

        // Get all the walletList where amount is greater than or equal to UPDATED_AMOUNT
        defaultWalletShouldNotBeFound("amount.greaterThanOrEqual=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllWalletsByAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        // Get all the walletList where amount is less than or equal to DEFAULT_AMOUNT
        defaultWalletShouldBeFound("amount.lessThanOrEqual=" + DEFAULT_AMOUNT);

        // Get all the walletList where amount is less than or equal to SMALLER_AMOUNT
        defaultWalletShouldNotBeFound("amount.lessThanOrEqual=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllWalletsByAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        // Get all the walletList where amount is less than DEFAULT_AMOUNT
        defaultWalletShouldNotBeFound("amount.lessThan=" + DEFAULT_AMOUNT);

        // Get all the walletList where amount is less than UPDATED_AMOUNT
        defaultWalletShouldBeFound("amount.lessThan=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllWalletsByAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        // Get all the walletList where amount is greater than DEFAULT_AMOUNT
        defaultWalletShouldNotBeFound("amount.greaterThan=" + DEFAULT_AMOUNT);

        // Get all the walletList where amount is greater than SMALLER_AMOUNT
        defaultWalletShouldBeFound("amount.greaterThan=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllWalletsByCurrencyIsEqualToSomething() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        // Get all the walletList where currency equals to DEFAULT_CURRENCY
        defaultWalletShouldBeFound("currency.equals=" + DEFAULT_CURRENCY);

        // Get all the walletList where currency equals to UPDATED_CURRENCY
        defaultWalletShouldNotBeFound("currency.equals=" + UPDATED_CURRENCY);
    }

    @Test
    @Transactional
    void getAllWalletsByCurrencyIsNotEqualToSomething() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        // Get all the walletList where currency not equals to DEFAULT_CURRENCY
        defaultWalletShouldNotBeFound("currency.notEquals=" + DEFAULT_CURRENCY);

        // Get all the walletList where currency not equals to UPDATED_CURRENCY
        defaultWalletShouldBeFound("currency.notEquals=" + UPDATED_CURRENCY);
    }

    @Test
    @Transactional
    void getAllWalletsByCurrencyIsInShouldWork() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        // Get all the walletList where currency in DEFAULT_CURRENCY or UPDATED_CURRENCY
        defaultWalletShouldBeFound("currency.in=" + DEFAULT_CURRENCY + "," + UPDATED_CURRENCY);

        // Get all the walletList where currency equals to UPDATED_CURRENCY
        defaultWalletShouldNotBeFound("currency.in=" + UPDATED_CURRENCY);
    }

    @Test
    @Transactional
    void getAllWalletsByCurrencyIsNullOrNotNull() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        // Get all the walletList where currency is not null
        defaultWalletShouldBeFound("currency.specified=true");

        // Get all the walletList where currency is null
        defaultWalletShouldNotBeFound("currency.specified=false");
    }

    @Test
    @Transactional
    void getAllWalletsByCurrencyContainsSomething() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        // Get all the walletList where currency contains DEFAULT_CURRENCY
        defaultWalletShouldBeFound("currency.contains=" + DEFAULT_CURRENCY);

        // Get all the walletList where currency contains UPDATED_CURRENCY
        defaultWalletShouldNotBeFound("currency.contains=" + UPDATED_CURRENCY);
    }

    @Test
    @Transactional
    void getAllWalletsByCurrencyNotContainsSomething() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        // Get all the walletList where currency does not contain DEFAULT_CURRENCY
        defaultWalletShouldNotBeFound("currency.doesNotContain=" + DEFAULT_CURRENCY);

        // Get all the walletList where currency does not contain UPDATED_CURRENCY
        defaultWalletShouldBeFound("currency.doesNotContain=" + UPDATED_CURRENCY);
    }

    @Test
    @Transactional
    void getAllWalletsByRemarksIsEqualToSomething() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        // Get all the walletList where remarks equals to DEFAULT_REMARKS
        defaultWalletShouldBeFound("remarks.equals=" + DEFAULT_REMARKS);

        // Get all the walletList where remarks equals to UPDATED_REMARKS
        defaultWalletShouldNotBeFound("remarks.equals=" + UPDATED_REMARKS);
    }

    @Test
    @Transactional
    void getAllWalletsByRemarksIsNotEqualToSomething() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        // Get all the walletList where remarks not equals to DEFAULT_REMARKS
        defaultWalletShouldNotBeFound("remarks.notEquals=" + DEFAULT_REMARKS);

        // Get all the walletList where remarks not equals to UPDATED_REMARKS
        defaultWalletShouldBeFound("remarks.notEquals=" + UPDATED_REMARKS);
    }

    @Test
    @Transactional
    void getAllWalletsByRemarksIsInShouldWork() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        // Get all the walletList where remarks in DEFAULT_REMARKS or UPDATED_REMARKS
        defaultWalletShouldBeFound("remarks.in=" + DEFAULT_REMARKS + "," + UPDATED_REMARKS);

        // Get all the walletList where remarks equals to UPDATED_REMARKS
        defaultWalletShouldNotBeFound("remarks.in=" + UPDATED_REMARKS);
    }

    @Test
    @Transactional
    void getAllWalletsByRemarksIsNullOrNotNull() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        // Get all the walletList where remarks is not null
        defaultWalletShouldBeFound("remarks.specified=true");

        // Get all the walletList where remarks is null
        defaultWalletShouldNotBeFound("remarks.specified=false");
    }

    @Test
    @Transactional
    void getAllWalletsByRemarksContainsSomething() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        // Get all the walletList where remarks contains DEFAULT_REMARKS
        defaultWalletShouldBeFound("remarks.contains=" + DEFAULT_REMARKS);

        // Get all the walletList where remarks contains UPDATED_REMARKS
        defaultWalletShouldNotBeFound("remarks.contains=" + UPDATED_REMARKS);
    }

    @Test
    @Transactional
    void getAllWalletsByRemarksNotContainsSomething() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        // Get all the walletList where remarks does not contain DEFAULT_REMARKS
        defaultWalletShouldNotBeFound("remarks.doesNotContain=" + DEFAULT_REMARKS);

        // Get all the walletList where remarks does not contain UPDATED_REMARKS
        defaultWalletShouldBeFound("remarks.doesNotContain=" + UPDATED_REMARKS);
    }

    @Test
    @Transactional
    void getAllWalletsByWalletGroupIsEqualToSomething() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);
        WalletGroup walletGroup = WalletGroupResourceIT.createEntity(em);
        em.persist(walletGroup);
        em.flush();
        wallet.setWalletGroup(walletGroup);
        walletRepository.saveAndFlush(wallet);
        Long walletGroupId = walletGroup.getId();

        // Get all the walletList where walletGroup equals to walletGroupId
        defaultWalletShouldBeFound("walletGroupId.equals=" + walletGroupId);

        // Get all the walletList where walletGroup equals to (walletGroupId + 1)
        defaultWalletShouldNotBeFound("walletGroupId.equals=" + (walletGroupId + 1));
    }

    @Test
    @Transactional
    void getAllWalletsByActivityIsEqualToSomething() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);
        Activity activity = ActivityResourceIT.createEntity(em);
        em.persist(activity);
        em.flush();
        wallet.addActivity(activity);
        walletRepository.saveAndFlush(wallet);
        Long activityId = activity.getId();

        // Get all the walletList where activity equals to activityId
        defaultWalletShouldBeFound("activityId.equals=" + activityId);

        // Get all the walletList where activity equals to (activityId + 1)
        defaultWalletShouldNotBeFound("activityId.equals=" + (activityId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultWalletShouldBeFound(String filter) throws Exception {
        restWalletMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(wallet.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY)))
            .andExpect(jsonPath("$.[*].remarks").value(hasItem(DEFAULT_REMARKS)));

        // Check, that the count call also returns 1
        restWalletMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultWalletShouldNotBeFound(String filter) throws Exception {
        restWalletMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restWalletMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingWallet() throws Exception {
        // Get the wallet
        restWalletMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewWallet() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        int databaseSizeBeforeUpdate = walletRepository.findAll().size();

        // Update the wallet
        Wallet updatedWallet = walletRepository.findById(wallet.getId()).get();
        // Disconnect from session so that the updates on updatedWallet are not directly saved in db
        em.detach(updatedWallet);
        updatedWallet.name(UPDATED_NAME).amount(UPDATED_AMOUNT).currency(UPDATED_CURRENCY).remarks(UPDATED_REMARKS);
        WalletDTO walletDTO = walletMapper.toDto(updatedWallet);

        restWalletMockMvc
            .perform(
                put(ENTITY_API_URL_ID, walletDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(walletDTO))
            )
            .andExpect(status().isOk());

        // Validate the Wallet in the database
        List<Wallet> walletList = walletRepository.findAll();
        assertThat(walletList).hasSize(databaseSizeBeforeUpdate);
        Wallet testWallet = walletList.get(walletList.size() - 1);
        assertThat(testWallet.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testWallet.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testWallet.getCurrency()).isEqualTo(UPDATED_CURRENCY);
        assertThat(testWallet.getRemarks()).isEqualTo(UPDATED_REMARKS);
    }

    @Test
    @Transactional
    void putNonExistingWallet() throws Exception {
        int databaseSizeBeforeUpdate = walletRepository.findAll().size();
        wallet.setId(count.incrementAndGet());

        // Create the Wallet
        WalletDTO walletDTO = walletMapper.toDto(wallet);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWalletMockMvc
            .perform(
                put(ENTITY_API_URL_ID, walletDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(walletDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Wallet in the database
        List<Wallet> walletList = walletRepository.findAll();
        assertThat(walletList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchWallet() throws Exception {
        int databaseSizeBeforeUpdate = walletRepository.findAll().size();
        wallet.setId(count.incrementAndGet());

        // Create the Wallet
        WalletDTO walletDTO = walletMapper.toDto(wallet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWalletMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(walletDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Wallet in the database
        List<Wallet> walletList = walletRepository.findAll();
        assertThat(walletList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWallet() throws Exception {
        int databaseSizeBeforeUpdate = walletRepository.findAll().size();
        wallet.setId(count.incrementAndGet());

        // Create the Wallet
        WalletDTO walletDTO = walletMapper.toDto(wallet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWalletMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(walletDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Wallet in the database
        List<Wallet> walletList = walletRepository.findAll();
        assertThat(walletList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateWalletWithPatch() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        int databaseSizeBeforeUpdate = walletRepository.findAll().size();

        // Update the wallet using partial update
        Wallet partialUpdatedWallet = new Wallet();
        partialUpdatedWallet.setId(wallet.getId());

        partialUpdatedWallet.name(UPDATED_NAME).amount(UPDATED_AMOUNT);

        restWalletMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWallet.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWallet))
            )
            .andExpect(status().isOk());

        // Validate the Wallet in the database
        List<Wallet> walletList = walletRepository.findAll();
        assertThat(walletList).hasSize(databaseSizeBeforeUpdate);
        Wallet testWallet = walletList.get(walletList.size() - 1);
        assertThat(testWallet.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testWallet.getAmount()).isEqualByComparingTo(UPDATED_AMOUNT);
        assertThat(testWallet.getCurrency()).isEqualTo(DEFAULT_CURRENCY);
        assertThat(testWallet.getRemarks()).isEqualTo(DEFAULT_REMARKS);
    }

    @Test
    @Transactional
    void fullUpdateWalletWithPatch() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        int databaseSizeBeforeUpdate = walletRepository.findAll().size();

        // Update the wallet using partial update
        Wallet partialUpdatedWallet = new Wallet();
        partialUpdatedWallet.setId(wallet.getId());

        partialUpdatedWallet.name(UPDATED_NAME).amount(UPDATED_AMOUNT).currency(UPDATED_CURRENCY).remarks(UPDATED_REMARKS);

        restWalletMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWallet.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWallet))
            )
            .andExpect(status().isOk());

        // Validate the Wallet in the database
        List<Wallet> walletList = walletRepository.findAll();
        assertThat(walletList).hasSize(databaseSizeBeforeUpdate);
        Wallet testWallet = walletList.get(walletList.size() - 1);
        assertThat(testWallet.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testWallet.getAmount()).isEqualByComparingTo(UPDATED_AMOUNT);
        assertThat(testWallet.getCurrency()).isEqualTo(UPDATED_CURRENCY);
        assertThat(testWallet.getRemarks()).isEqualTo(UPDATED_REMARKS);
    }

    @Test
    @Transactional
    void patchNonExistingWallet() throws Exception {
        int databaseSizeBeforeUpdate = walletRepository.findAll().size();
        wallet.setId(count.incrementAndGet());

        // Create the Wallet
        WalletDTO walletDTO = walletMapper.toDto(wallet);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWalletMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, walletDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(walletDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Wallet in the database
        List<Wallet> walletList = walletRepository.findAll();
        assertThat(walletList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWallet() throws Exception {
        int databaseSizeBeforeUpdate = walletRepository.findAll().size();
        wallet.setId(count.incrementAndGet());

        // Create the Wallet
        WalletDTO walletDTO = walletMapper.toDto(wallet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWalletMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(walletDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Wallet in the database
        List<Wallet> walletList = walletRepository.findAll();
        assertThat(walletList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWallet() throws Exception {
        int databaseSizeBeforeUpdate = walletRepository.findAll().size();
        wallet.setId(count.incrementAndGet());

        // Create the Wallet
        WalletDTO walletDTO = walletMapper.toDto(wallet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWalletMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(walletDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Wallet in the database
        List<Wallet> walletList = walletRepository.findAll();
        assertThat(walletList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteWallet() throws Exception {
        // Initialize the database
        walletRepository.saveAndFlush(wallet);

        int databaseSizeBeforeDelete = walletRepository.findAll().size();

        // Delete the wallet
        restWalletMockMvc
            .perform(delete(ENTITY_API_URL_ID, wallet.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Wallet> walletList = walletRepository.findAll();
        assertThat(walletList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
