package com.polykhel.billnext.web.rest;

import static com.polykhel.billnext.web.rest.TestUtil.sameInstant;
import static com.polykhel.billnext.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.polykhel.billnext.IntegrationTest;
import com.polykhel.billnext.domain.Activity;
import com.polykhel.billnext.domain.Category;
import com.polykhel.billnext.domain.User;
import com.polykhel.billnext.domain.Wallet;
import com.polykhel.billnext.domain.enumeration.ActivityType;
import com.polykhel.billnext.repository.ActivityRepository;
import com.polykhel.billnext.service.criteria.ActivityCriteria;
import com.polykhel.billnext.service.dto.ActivityDTO;
import com.polykhel.billnext.service.mapper.ActivityMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
 * Integration tests for the {@link ActivityResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ActivityResourceIT {

    private static final ZonedDateTime DEFAULT_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);
    private static final ZonedDateTime SMALLER_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(-1L), ZoneOffset.UTC);

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);
    private static final BigDecimal SMALLER_AMOUNT = new BigDecimal(1 - 1);

    private static final String DEFAULT_REMARKS = "AAAAAAAAAA";
    private static final String UPDATED_REMARKS = "BBBBBBBBBB";

    private static final ActivityType DEFAULT_TYPE = ActivityType.INCOME;
    private static final ActivityType UPDATED_TYPE = ActivityType.EXPENSE;

    private static final String ENTITY_API_URL = "/api/activities";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restActivityMockMvc;

    private Activity activity;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Activity createEntity(EntityManager em) {
        Activity activity = new Activity().date(DEFAULT_DATE).amount(DEFAULT_AMOUNT).remarks(DEFAULT_REMARKS).type(DEFAULT_TYPE);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        activity.setUser(user);
        // Add required entity
        Wallet wallet;
        if (TestUtil.findAll(em, Wallet.class).isEmpty()) {
            wallet = WalletResourceIT.createEntity(em);
            em.persist(wallet);
            em.flush();
        } else {
            wallet = TestUtil.findAll(em, Wallet.class).get(0);
        }
        activity.setWallet(wallet);
        // Add required entity
        Category category;
        if (TestUtil.findAll(em, Category.class).isEmpty()) {
            category = CategoryResourceIT.createEntity(em);
            em.persist(category);
            em.flush();
        } else {
            category = TestUtil.findAll(em, Category.class).get(0);
        }
        activity.setCategory(category);
        return activity;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Activity createUpdatedEntity(EntityManager em) {
        Activity activity = new Activity().date(UPDATED_DATE).amount(UPDATED_AMOUNT).remarks(UPDATED_REMARKS).type(UPDATED_TYPE);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        activity.setUser(user);
        // Add required entity
        Wallet wallet;
        if (TestUtil.findAll(em, Wallet.class).isEmpty()) {
            wallet = WalletResourceIT.createUpdatedEntity(em);
            em.persist(wallet);
            em.flush();
        } else {
            wallet = TestUtil.findAll(em, Wallet.class).get(0);
        }
        activity.setWallet(wallet);
        // Add required entity
        Category category;
        if (TestUtil.findAll(em, Category.class).isEmpty()) {
            category = CategoryResourceIT.createUpdatedEntity(em);
            em.persist(category);
            em.flush();
        } else {
            category = TestUtil.findAll(em, Category.class).get(0);
        }
        activity.setCategory(category);
        return activity;
    }

    @BeforeEach
    public void initTest() {
        activity = createEntity(em);
    }

    @Test
    @Transactional
    void createActivity() throws Exception {
        int databaseSizeBeforeCreate = activityRepository.findAll().size();
        // Create the Activity
        ActivityDTO activityDTO = activityMapper.toDto(activity);
        restActivityMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(activityDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeCreate + 1);
        Activity testActivity = activityList.get(activityList.size() - 1);
        assertThat(testActivity.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testActivity.getAmount()).isEqualByComparingTo(DEFAULT_AMOUNT);
        assertThat(testActivity.getRemarks()).isEqualTo(DEFAULT_REMARKS);
        assertThat(testActivity.getType()).isEqualTo(DEFAULT_TYPE);
    }

    @Test
    @Transactional
    void createActivityWithExistingId() throws Exception {
        // Create the Activity with an existing ID
        activity.setId(1L);
        ActivityDTO activityDTO = activityMapper.toDto(activity);

        int databaseSizeBeforeCreate = activityRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restActivityMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(activityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = activityRepository.findAll().size();
        // set the field null
        activity.setDate(null);

        // Create the Activity, which fails.
        ActivityDTO activityDTO = activityMapper.toDto(activity);

        restActivityMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(activityDTO))
            )
            .andExpect(status().isBadRequest());

        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = activityRepository.findAll().size();
        // set the field null
        activity.setAmount(null);

        // Create the Activity, which fails.
        ActivityDTO activityDTO = activityMapper.toDto(activity);

        restActivityMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(activityDTO))
            )
            .andExpect(status().isBadRequest());

        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = activityRepository.findAll().size();
        // set the field null
        activity.setType(null);

        // Create the Activity, which fails.
        ActivityDTO activityDTO = activityMapper.toDto(activity);

        restActivityMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(activityDTO))
            )
            .andExpect(status().isBadRequest());

        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllActivities() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList
        restActivityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(activity.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].remarks").value(hasItem(DEFAULT_REMARKS)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));
    }

    @Test
    @Transactional
    void getActivity() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get the activity
        restActivityMockMvc
            .perform(get(ENTITY_API_URL_ID, activity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(activity.getId().intValue()))
            .andExpect(jsonPath("$.date").value(sameInstant(DEFAULT_DATE)))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.remarks").value(DEFAULT_REMARKS))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()));
    }

    @Test
    @Transactional
    void getActivitiesByIdFiltering() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        Long id = activity.getId();

        defaultActivityShouldBeFound("id.equals=" + id);
        defaultActivityShouldNotBeFound("id.notEquals=" + id);

        defaultActivityShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultActivityShouldNotBeFound("id.greaterThan=" + id);

        defaultActivityShouldBeFound("id.lessThanOrEqual=" + id);
        defaultActivityShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllActivitiesByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where date equals to DEFAULT_DATE
        defaultActivityShouldBeFound("date.equals=" + DEFAULT_DATE);

        // Get all the activityList where date equals to UPDATED_DATE
        defaultActivityShouldNotBeFound("date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllActivitiesByDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where date not equals to DEFAULT_DATE
        defaultActivityShouldNotBeFound("date.notEquals=" + DEFAULT_DATE);

        // Get all the activityList where date not equals to UPDATED_DATE
        defaultActivityShouldBeFound("date.notEquals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllActivitiesByDateIsInShouldWork() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where date in DEFAULT_DATE or UPDATED_DATE
        defaultActivityShouldBeFound("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE);

        // Get all the activityList where date equals to UPDATED_DATE
        defaultActivityShouldNotBeFound("date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllActivitiesByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where date is not null
        defaultActivityShouldBeFound("date.specified=true");

        // Get all the activityList where date is null
        defaultActivityShouldNotBeFound("date.specified=false");
    }

    @Test
    @Transactional
    void getAllActivitiesByDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where date is greater than or equal to DEFAULT_DATE
        defaultActivityShouldBeFound("date.greaterThanOrEqual=" + DEFAULT_DATE);

        // Get all the activityList where date is greater than or equal to UPDATED_DATE
        defaultActivityShouldNotBeFound("date.greaterThanOrEqual=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllActivitiesByDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where date is less than or equal to DEFAULT_DATE
        defaultActivityShouldBeFound("date.lessThanOrEqual=" + DEFAULT_DATE);

        // Get all the activityList where date is less than or equal to SMALLER_DATE
        defaultActivityShouldNotBeFound("date.lessThanOrEqual=" + SMALLER_DATE);
    }

    @Test
    @Transactional
    void getAllActivitiesByDateIsLessThanSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where date is less than DEFAULT_DATE
        defaultActivityShouldNotBeFound("date.lessThan=" + DEFAULT_DATE);

        // Get all the activityList where date is less than UPDATED_DATE
        defaultActivityShouldBeFound("date.lessThan=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllActivitiesByDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where date is greater than DEFAULT_DATE
        defaultActivityShouldNotBeFound("date.greaterThan=" + DEFAULT_DATE);

        // Get all the activityList where date is greater than SMALLER_DATE
        defaultActivityShouldBeFound("date.greaterThan=" + SMALLER_DATE);
    }

    @Test
    @Transactional
    void getAllActivitiesByAmountIsEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where amount equals to DEFAULT_AMOUNT
        defaultActivityShouldBeFound("amount.equals=" + DEFAULT_AMOUNT);

        // Get all the activityList where amount equals to UPDATED_AMOUNT
        defaultActivityShouldNotBeFound("amount.equals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllActivitiesByAmountIsNotEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where amount not equals to DEFAULT_AMOUNT
        defaultActivityShouldNotBeFound("amount.notEquals=" + DEFAULT_AMOUNT);

        // Get all the activityList where amount not equals to UPDATED_AMOUNT
        defaultActivityShouldBeFound("amount.notEquals=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllActivitiesByAmountIsInShouldWork() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where amount in DEFAULT_AMOUNT or UPDATED_AMOUNT
        defaultActivityShouldBeFound("amount.in=" + DEFAULT_AMOUNT + "," + UPDATED_AMOUNT);

        // Get all the activityList where amount equals to UPDATED_AMOUNT
        defaultActivityShouldNotBeFound("amount.in=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllActivitiesByAmountIsNullOrNotNull() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where amount is not null
        defaultActivityShouldBeFound("amount.specified=true");

        // Get all the activityList where amount is null
        defaultActivityShouldNotBeFound("amount.specified=false");
    }

    @Test
    @Transactional
    void getAllActivitiesByAmountIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where amount is greater than or equal to DEFAULT_AMOUNT
        defaultActivityShouldBeFound("amount.greaterThanOrEqual=" + DEFAULT_AMOUNT);

        // Get all the activityList where amount is greater than or equal to UPDATED_AMOUNT
        defaultActivityShouldNotBeFound("amount.greaterThanOrEqual=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllActivitiesByAmountIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where amount is less than or equal to DEFAULT_AMOUNT
        defaultActivityShouldBeFound("amount.lessThanOrEqual=" + DEFAULT_AMOUNT);

        // Get all the activityList where amount is less than or equal to SMALLER_AMOUNT
        defaultActivityShouldNotBeFound("amount.lessThanOrEqual=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllActivitiesByAmountIsLessThanSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where amount is less than DEFAULT_AMOUNT
        defaultActivityShouldNotBeFound("amount.lessThan=" + DEFAULT_AMOUNT);

        // Get all the activityList where amount is less than UPDATED_AMOUNT
        defaultActivityShouldBeFound("amount.lessThan=" + UPDATED_AMOUNT);
    }

    @Test
    @Transactional
    void getAllActivitiesByAmountIsGreaterThanSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where amount is greater than DEFAULT_AMOUNT
        defaultActivityShouldNotBeFound("amount.greaterThan=" + DEFAULT_AMOUNT);

        // Get all the activityList where amount is greater than SMALLER_AMOUNT
        defaultActivityShouldBeFound("amount.greaterThan=" + SMALLER_AMOUNT);
    }

    @Test
    @Transactional
    void getAllActivitiesByRemarksIsEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where remarks equals to DEFAULT_REMARKS
        defaultActivityShouldBeFound("remarks.equals=" + DEFAULT_REMARKS);

        // Get all the activityList where remarks equals to UPDATED_REMARKS
        defaultActivityShouldNotBeFound("remarks.equals=" + UPDATED_REMARKS);
    }

    @Test
    @Transactional
    void getAllActivitiesByRemarksIsNotEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where remarks not equals to DEFAULT_REMARKS
        defaultActivityShouldNotBeFound("remarks.notEquals=" + DEFAULT_REMARKS);

        // Get all the activityList where remarks not equals to UPDATED_REMARKS
        defaultActivityShouldBeFound("remarks.notEquals=" + UPDATED_REMARKS);
    }

    @Test
    @Transactional
    void getAllActivitiesByRemarksIsInShouldWork() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where remarks in DEFAULT_REMARKS or UPDATED_REMARKS
        defaultActivityShouldBeFound("remarks.in=" + DEFAULT_REMARKS + "," + UPDATED_REMARKS);

        // Get all the activityList where remarks equals to UPDATED_REMARKS
        defaultActivityShouldNotBeFound("remarks.in=" + UPDATED_REMARKS);
    }

    @Test
    @Transactional
    void getAllActivitiesByRemarksIsNullOrNotNull() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where remarks is not null
        defaultActivityShouldBeFound("remarks.specified=true");

        // Get all the activityList where remarks is null
        defaultActivityShouldNotBeFound("remarks.specified=false");
    }

    @Test
    @Transactional
    void getAllActivitiesByRemarksContainsSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where remarks contains DEFAULT_REMARKS
        defaultActivityShouldBeFound("remarks.contains=" + DEFAULT_REMARKS);

        // Get all the activityList where remarks contains UPDATED_REMARKS
        defaultActivityShouldNotBeFound("remarks.contains=" + UPDATED_REMARKS);
    }

    @Test
    @Transactional
    void getAllActivitiesByRemarksNotContainsSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where remarks does not contain DEFAULT_REMARKS
        defaultActivityShouldNotBeFound("remarks.doesNotContain=" + DEFAULT_REMARKS);

        // Get all the activityList where remarks does not contain UPDATED_REMARKS
        defaultActivityShouldBeFound("remarks.doesNotContain=" + UPDATED_REMARKS);
    }

    @Test
    @Transactional
    void getAllActivitiesByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where type equals to DEFAULT_TYPE
        defaultActivityShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the activityList where type equals to UPDATED_TYPE
        defaultActivityShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllActivitiesByTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where type not equals to DEFAULT_TYPE
        defaultActivityShouldNotBeFound("type.notEquals=" + DEFAULT_TYPE);

        // Get all the activityList where type not equals to UPDATED_TYPE
        defaultActivityShouldBeFound("type.notEquals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllActivitiesByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultActivityShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the activityList where type equals to UPDATED_TYPE
        defaultActivityShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllActivitiesByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        // Get all the activityList where type is not null
        defaultActivityShouldBeFound("type.specified=true");

        // Get all the activityList where type is null
        defaultActivityShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    void getAllActivitiesByUserIsEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        activity.setUser(user);
        activityRepository.saveAndFlush(activity);
        String userId = user.getId();

        // Get all the activityList where user equals to userId
        defaultActivityShouldBeFound("userId.equals=" + userId);

        // Get all the activityList where user equals to "invalid-id"
        defaultActivityShouldNotBeFound("userId.equals=" + "invalid-id");
    }

    @Test
    @Transactional
    void getAllActivitiesByWalletIsEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);
        Wallet wallet = WalletResourceIT.createEntity(em);
        em.persist(wallet);
        em.flush();
        activity.setWallet(wallet);
        activityRepository.saveAndFlush(activity);
        Long walletId = wallet.getId();

        // Get all the activityList where wallet equals to walletId
        defaultActivityShouldBeFound("walletId.equals=" + walletId);

        // Get all the activityList where wallet equals to (walletId + 1)
        defaultActivityShouldNotBeFound("walletId.equals=" + (walletId + 1));
    }

    @Test
    @Transactional
    void getAllActivitiesByCategoryIsEqualToSomething() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);
        Category category = CategoryResourceIT.createEntity(em);
        em.persist(category);
        em.flush();
        activity.setCategory(category);
        activityRepository.saveAndFlush(activity);
        Long categoryId = category.getId();

        // Get all the activityList where category equals to categoryId
        defaultActivityShouldBeFound("categoryId.equals=" + categoryId);

        // Get all the activityList where category equals to (categoryId + 1)
        defaultActivityShouldNotBeFound("categoryId.equals=" + (categoryId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultActivityShouldBeFound(String filter) throws Exception {
        restActivityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(activity.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].remarks").value(hasItem(DEFAULT_REMARKS)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())));

        // Check, that the count call also returns 1
        restActivityMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultActivityShouldNotBeFound(String filter) throws Exception {
        restActivityMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restActivityMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingActivity() throws Exception {
        // Get the activity
        restActivityMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewActivity() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        int databaseSizeBeforeUpdate = activityRepository.findAll().size();

        // Update the activity
        Activity updatedActivity = activityRepository.findById(activity.getId()).get();
        // Disconnect from session so that the updates on updatedActivity are not directly saved in db
        em.detach(updatedActivity);
        updatedActivity.date(UPDATED_DATE).amount(UPDATED_AMOUNT).remarks(UPDATED_REMARKS).type(UPDATED_TYPE);
        ActivityDTO activityDTO = activityMapper.toDto(updatedActivity);

        restActivityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, activityDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(activityDTO))
            )
            .andExpect(status().isOk());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate);
        Activity testActivity = activityList.get(activityList.size() - 1);
        assertThat(testActivity.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testActivity.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testActivity.getRemarks()).isEqualTo(UPDATED_REMARKS);
        assertThat(testActivity.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingActivity() throws Exception {
        int databaseSizeBeforeUpdate = activityRepository.findAll().size();
        activity.setId(count.incrementAndGet());

        // Create the Activity
        ActivityDTO activityDTO = activityMapper.toDto(activity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restActivityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, activityDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(activityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchActivity() throws Exception {
        int databaseSizeBeforeUpdate = activityRepository.findAll().size();
        activity.setId(count.incrementAndGet());

        // Create the Activity
        ActivityDTO activityDTO = activityMapper.toDto(activity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActivityMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(activityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamActivity() throws Exception {
        int databaseSizeBeforeUpdate = activityRepository.findAll().size();
        activity.setId(count.incrementAndGet());

        // Create the Activity
        ActivityDTO activityDTO = activityMapper.toDto(activity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActivityMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(activityDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateActivityWithPatch() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        int databaseSizeBeforeUpdate = activityRepository.findAll().size();

        // Update the activity using partial update
        Activity partialUpdatedActivity = new Activity();
        partialUpdatedActivity.setId(activity.getId());

        partialUpdatedActivity.date(UPDATED_DATE).remarks(UPDATED_REMARKS).type(UPDATED_TYPE);

        restActivityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedActivity.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedActivity))
            )
            .andExpect(status().isOk());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate);
        Activity testActivity = activityList.get(activityList.size() - 1);
        assertThat(testActivity.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testActivity.getAmount()).isEqualByComparingTo(DEFAULT_AMOUNT);
        assertThat(testActivity.getRemarks()).isEqualTo(UPDATED_REMARKS);
        assertThat(testActivity.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateActivityWithPatch() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        int databaseSizeBeforeUpdate = activityRepository.findAll().size();

        // Update the activity using partial update
        Activity partialUpdatedActivity = new Activity();
        partialUpdatedActivity.setId(activity.getId());

        partialUpdatedActivity.date(UPDATED_DATE).amount(UPDATED_AMOUNT).remarks(UPDATED_REMARKS).type(UPDATED_TYPE);

        restActivityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedActivity.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedActivity))
            )
            .andExpect(status().isOk());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate);
        Activity testActivity = activityList.get(activityList.size() - 1);
        assertThat(testActivity.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testActivity.getAmount()).isEqualByComparingTo(UPDATED_AMOUNT);
        assertThat(testActivity.getRemarks()).isEqualTo(UPDATED_REMARKS);
        assertThat(testActivity.getType()).isEqualTo(UPDATED_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingActivity() throws Exception {
        int databaseSizeBeforeUpdate = activityRepository.findAll().size();
        activity.setId(count.incrementAndGet());

        // Create the Activity
        ActivityDTO activityDTO = activityMapper.toDto(activity);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restActivityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, activityDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(activityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchActivity() throws Exception {
        int databaseSizeBeforeUpdate = activityRepository.findAll().size();
        activity.setId(count.incrementAndGet());

        // Create the Activity
        ActivityDTO activityDTO = activityMapper.toDto(activity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActivityMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(activityDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamActivity() throws Exception {
        int databaseSizeBeforeUpdate = activityRepository.findAll().size();
        activity.setId(count.incrementAndGet());

        // Create the Activity
        ActivityDTO activityDTO = activityMapper.toDto(activity);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restActivityMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(activityDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Activity in the database
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteActivity() throws Exception {
        // Initialize the database
        activityRepository.saveAndFlush(activity);

        int databaseSizeBeforeDelete = activityRepository.findAll().size();

        // Delete the activity
        restActivityMockMvc
            .perform(delete(ENTITY_API_URL_ID, activity.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Activity> activityList = activityRepository.findAll();
        assertThat(activityList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
