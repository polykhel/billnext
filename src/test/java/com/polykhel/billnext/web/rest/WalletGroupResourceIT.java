package com.polykhel.billnext.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.polykhel.billnext.IntegrationTest;
import com.polykhel.billnext.domain.User;
import com.polykhel.billnext.domain.Wallet;
import com.polykhel.billnext.domain.WalletGroup;
import com.polykhel.billnext.repository.WalletGroupRepository;
import com.polykhel.billnext.service.dto.WalletGroupDTO;
import com.polykhel.billnext.service.mapper.WalletGroupMapper;
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
 * Integration tests for the {@link WalletGroupResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class WalletGroupResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/wallet-groups";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private WalletGroupRepository walletGroupRepository;

    @Autowired
    private WalletGroupMapper walletGroupMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWalletGroupMockMvc;

    private WalletGroup walletGroup;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WalletGroup createEntity(EntityManager em) {
        WalletGroup walletGroup = new WalletGroup().name(DEFAULT_NAME);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        walletGroup.setUser(user);
        return walletGroup;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WalletGroup createUpdatedEntity(EntityManager em) {
        WalletGroup walletGroup = new WalletGroup().name(UPDATED_NAME);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        walletGroup.setUser(user);
        return walletGroup;
    }

    @BeforeEach
    public void initTest() {
        walletGroup = createEntity(em);
    }

    @Test
    @Transactional
    void createWalletGroup() throws Exception {
        int databaseSizeBeforeCreate = walletGroupRepository.findAll().size();
        // Create the WalletGroup
        WalletGroupDTO walletGroupDTO = walletGroupMapper.toDto(walletGroup);
        restWalletGroupMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(walletGroupDTO))
            )
            .andExpect(status().isCreated());

        // Validate the WalletGroup in the database
        List<WalletGroup> walletGroupList = walletGroupRepository.findAll();
        assertThat(walletGroupList).hasSize(databaseSizeBeforeCreate + 1);
        WalletGroup testWalletGroup = walletGroupList.get(walletGroupList.size() - 1);
        assertThat(testWalletGroup.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void createWalletGroupWithExistingId() throws Exception {
        // Create the WalletGroup with an existing ID
        walletGroup.setId(1L);
        WalletGroupDTO walletGroupDTO = walletGroupMapper.toDto(walletGroup);

        int databaseSizeBeforeCreate = walletGroupRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWalletGroupMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(walletGroupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WalletGroup in the database
        List<WalletGroup> walletGroupList = walletGroupRepository.findAll();
        assertThat(walletGroupList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = walletGroupRepository.findAll().size();
        // set the field null
        walletGroup.setName(null);

        // Create the WalletGroup, which fails.
        WalletGroupDTO walletGroupDTO = walletGroupMapper.toDto(walletGroup);

        restWalletGroupMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(walletGroupDTO))
            )
            .andExpect(status().isBadRequest());

        List<WalletGroup> walletGroupList = walletGroupRepository.findAll();
        assertThat(walletGroupList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllWalletGroups() throws Exception {
        // Initialize the database
        walletGroupRepository.saveAndFlush(walletGroup);

        // Get all the walletGroupList
        restWalletGroupMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(walletGroup.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getWalletGroup() throws Exception {
        // Initialize the database
        walletGroupRepository.saveAndFlush(walletGroup);

        // Get the walletGroup
        restWalletGroupMockMvc
            .perform(get(ENTITY_API_URL_ID, walletGroup.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(walletGroup.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getWalletGroupsByIdFiltering() throws Exception {
        // Initialize the database
        walletGroupRepository.saveAndFlush(walletGroup);

        Long id = walletGroup.getId();

        defaultWalletGroupShouldBeFound("id.equals=" + id);
        defaultWalletGroupShouldNotBeFound("id.notEquals=" + id);

        defaultWalletGroupShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultWalletGroupShouldNotBeFound("id.greaterThan=" + id);

        defaultWalletGroupShouldBeFound("id.lessThanOrEqual=" + id);
        defaultWalletGroupShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllWalletGroupsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        walletGroupRepository.saveAndFlush(walletGroup);

        // Get all the walletGroupList where name equals to DEFAULT_NAME
        defaultWalletGroupShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the walletGroupList where name equals to UPDATED_NAME
        defaultWalletGroupShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWalletGroupsByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        walletGroupRepository.saveAndFlush(walletGroup);

        // Get all the walletGroupList where name not equals to DEFAULT_NAME
        defaultWalletGroupShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the walletGroupList where name not equals to UPDATED_NAME
        defaultWalletGroupShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWalletGroupsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        walletGroupRepository.saveAndFlush(walletGroup);

        // Get all the walletGroupList where name in DEFAULT_NAME or UPDATED_NAME
        defaultWalletGroupShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the walletGroupList where name equals to UPDATED_NAME
        defaultWalletGroupShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWalletGroupsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        walletGroupRepository.saveAndFlush(walletGroup);

        // Get all the walletGroupList where name is not null
        defaultWalletGroupShouldBeFound("name.specified=true");

        // Get all the walletGroupList where name is null
        defaultWalletGroupShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllWalletGroupsByNameContainsSomething() throws Exception {
        // Initialize the database
        walletGroupRepository.saveAndFlush(walletGroup);

        // Get all the walletGroupList where name contains DEFAULT_NAME
        defaultWalletGroupShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the walletGroupList where name contains UPDATED_NAME
        defaultWalletGroupShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWalletGroupsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        walletGroupRepository.saveAndFlush(walletGroup);

        // Get all the walletGroupList where name does not contain DEFAULT_NAME
        defaultWalletGroupShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the walletGroupList where name does not contain UPDATED_NAME
        defaultWalletGroupShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllWalletGroupsByUserIsEqualToSomething() throws Exception {
        // Initialize the database
        walletGroupRepository.saveAndFlush(walletGroup);
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        walletGroup.setUser(user);
        walletGroupRepository.saveAndFlush(walletGroup);
        String userId = user.getId();

        // Get all the walletGroupList where user equals to userId
        defaultWalletGroupShouldBeFound("userId.equals=" + userId);

        // Get all the walletGroupList where user equals to "invalid-id"
        defaultWalletGroupShouldNotBeFound("userId.equals=" + "invalid-id");
    }

    @Test
    @Transactional
    void getAllWalletGroupsByWalletsIsEqualToSomething() throws Exception {
        // Initialize the database
        walletGroupRepository.saveAndFlush(walletGroup);
        Wallet wallets = WalletResourceIT.createEntity(em);
        em.persist(wallets);
        em.flush();
        walletGroup.addWallets(wallets);
        walletGroupRepository.saveAndFlush(walletGroup);
        Long walletsId = wallets.getId();

        // Get all the walletGroupList where wallets equals to walletsId
        defaultWalletGroupShouldBeFound("walletsId.equals=" + walletsId);

        // Get all the walletGroupList where wallets equals to (walletsId + 1)
        defaultWalletGroupShouldNotBeFound("walletsId.equals=" + (walletsId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultWalletGroupShouldBeFound(String filter) throws Exception {
        restWalletGroupMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(walletGroup.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));

        // Check, that the count call also returns 1
        restWalletGroupMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultWalletGroupShouldNotBeFound(String filter) throws Exception {
        restWalletGroupMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restWalletGroupMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingWalletGroup() throws Exception {
        // Get the walletGroup
        restWalletGroupMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewWalletGroup() throws Exception {
        // Initialize the database
        walletGroupRepository.saveAndFlush(walletGroup);

        int databaseSizeBeforeUpdate = walletGroupRepository.findAll().size();

        // Update the walletGroup
        WalletGroup updatedWalletGroup = walletGroupRepository.findById(walletGroup.getId()).get();
        // Disconnect from session so that the updates on updatedWalletGroup are not directly saved in db
        em.detach(updatedWalletGroup);
        updatedWalletGroup.name(UPDATED_NAME);
        WalletGroupDTO walletGroupDTO = walletGroupMapper.toDto(updatedWalletGroup);

        restWalletGroupMockMvc
            .perform(
                put(ENTITY_API_URL_ID, walletGroupDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(walletGroupDTO))
            )
            .andExpect(status().isOk());

        // Validate the WalletGroup in the database
        List<WalletGroup> walletGroupList = walletGroupRepository.findAll();
        assertThat(walletGroupList).hasSize(databaseSizeBeforeUpdate);
        WalletGroup testWalletGroup = walletGroupList.get(walletGroupList.size() - 1);
        assertThat(testWalletGroup.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void putNonExistingWalletGroup() throws Exception {
        int databaseSizeBeforeUpdate = walletGroupRepository.findAll().size();
        walletGroup.setId(count.incrementAndGet());

        // Create the WalletGroup
        WalletGroupDTO walletGroupDTO = walletGroupMapper.toDto(walletGroup);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWalletGroupMockMvc
            .perform(
                put(ENTITY_API_URL_ID, walletGroupDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(walletGroupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WalletGroup in the database
        List<WalletGroup> walletGroupList = walletGroupRepository.findAll();
        assertThat(walletGroupList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchWalletGroup() throws Exception {
        int databaseSizeBeforeUpdate = walletGroupRepository.findAll().size();
        walletGroup.setId(count.incrementAndGet());

        // Create the WalletGroup
        WalletGroupDTO walletGroupDTO = walletGroupMapper.toDto(walletGroup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWalletGroupMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(walletGroupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WalletGroup in the database
        List<WalletGroup> walletGroupList = walletGroupRepository.findAll();
        assertThat(walletGroupList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWalletGroup() throws Exception {
        int databaseSizeBeforeUpdate = walletGroupRepository.findAll().size();
        walletGroup.setId(count.incrementAndGet());

        // Create the WalletGroup
        WalletGroupDTO walletGroupDTO = walletGroupMapper.toDto(walletGroup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWalletGroupMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(walletGroupDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the WalletGroup in the database
        List<WalletGroup> walletGroupList = walletGroupRepository.findAll();
        assertThat(walletGroupList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateWalletGroupWithPatch() throws Exception {
        // Initialize the database
        walletGroupRepository.saveAndFlush(walletGroup);

        int databaseSizeBeforeUpdate = walletGroupRepository.findAll().size();

        // Update the walletGroup using partial update
        WalletGroup partialUpdatedWalletGroup = new WalletGroup();
        partialUpdatedWalletGroup.setId(walletGroup.getId());

        partialUpdatedWalletGroup.name(UPDATED_NAME);

        restWalletGroupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWalletGroup.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWalletGroup))
            )
            .andExpect(status().isOk());

        // Validate the WalletGroup in the database
        List<WalletGroup> walletGroupList = walletGroupRepository.findAll();
        assertThat(walletGroupList).hasSize(databaseSizeBeforeUpdate);
        WalletGroup testWalletGroup = walletGroupList.get(walletGroupList.size() - 1);
        assertThat(testWalletGroup.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void fullUpdateWalletGroupWithPatch() throws Exception {
        // Initialize the database
        walletGroupRepository.saveAndFlush(walletGroup);

        int databaseSizeBeforeUpdate = walletGroupRepository.findAll().size();

        // Update the walletGroup using partial update
        WalletGroup partialUpdatedWalletGroup = new WalletGroup();
        partialUpdatedWalletGroup.setId(walletGroup.getId());

        partialUpdatedWalletGroup.name(UPDATED_NAME);

        restWalletGroupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWalletGroup.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWalletGroup))
            )
            .andExpect(status().isOk());

        // Validate the WalletGroup in the database
        List<WalletGroup> walletGroupList = walletGroupRepository.findAll();
        assertThat(walletGroupList).hasSize(databaseSizeBeforeUpdate);
        WalletGroup testWalletGroup = walletGroupList.get(walletGroupList.size() - 1);
        assertThat(testWalletGroup.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingWalletGroup() throws Exception {
        int databaseSizeBeforeUpdate = walletGroupRepository.findAll().size();
        walletGroup.setId(count.incrementAndGet());

        // Create the WalletGroup
        WalletGroupDTO walletGroupDTO = walletGroupMapper.toDto(walletGroup);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWalletGroupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, walletGroupDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(walletGroupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WalletGroup in the database
        List<WalletGroup> walletGroupList = walletGroupRepository.findAll();
        assertThat(walletGroupList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWalletGroup() throws Exception {
        int databaseSizeBeforeUpdate = walletGroupRepository.findAll().size();
        walletGroup.setId(count.incrementAndGet());

        // Create the WalletGroup
        WalletGroupDTO walletGroupDTO = walletGroupMapper.toDto(walletGroup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWalletGroupMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(walletGroupDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the WalletGroup in the database
        List<WalletGroup> walletGroupList = walletGroupRepository.findAll();
        assertThat(walletGroupList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWalletGroup() throws Exception {
        int databaseSizeBeforeUpdate = walletGroupRepository.findAll().size();
        walletGroup.setId(count.incrementAndGet());

        // Create the WalletGroup
        WalletGroupDTO walletGroupDTO = walletGroupMapper.toDto(walletGroup);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWalletGroupMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(walletGroupDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the WalletGroup in the database
        List<WalletGroup> walletGroupList = walletGroupRepository.findAll();
        assertThat(walletGroupList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteWalletGroup() throws Exception {
        // Initialize the database
        walletGroupRepository.saveAndFlush(walletGroup);

        int databaseSizeBeforeDelete = walletGroupRepository.findAll().size();

        // Delete the walletGroup
        restWalletGroupMockMvc
            .perform(delete(ENTITY_API_URL_ID, walletGroup.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<WalletGroup> walletGroupList = walletGroupRepository.findAll();
        assertThat(walletGroupList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
