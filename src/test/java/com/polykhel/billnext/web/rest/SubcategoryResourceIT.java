package com.polykhel.billnext.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.polykhel.billnext.IntegrationTest;
import com.polykhel.billnext.domain.Category;
import com.polykhel.billnext.domain.Subcategory;
import com.polykhel.billnext.repository.SubcategoryRepository;
import com.polykhel.billnext.service.dto.SubcategoryDTO;
import com.polykhel.billnext.service.mapper.SubcategoryMapper;
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
 * Integration tests for the {@link SubcategoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SubcategoryResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/subcategories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SubcategoryRepository subcategoryRepository;

    @Autowired
    private SubcategoryMapper subcategoryMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSubcategoryMockMvc;

    private Subcategory subcategory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Subcategory createEntity(EntityManager em) {
        Subcategory subcategory = new Subcategory().name(DEFAULT_NAME);
        // Add required entity
        Category category;
        if (TestUtil.findAll(em, Category.class).isEmpty()) {
            category = CategoryResourceIT.createEntity(em);
            em.persist(category);
            em.flush();
        } else {
            category = TestUtil.findAll(em, Category.class).get(0);
        }
        subcategory.setCategory(category);
        return subcategory;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Subcategory createUpdatedEntity(EntityManager em) {
        Subcategory subcategory = new Subcategory().name(UPDATED_NAME);
        // Add required entity
        Category category;
        if (TestUtil.findAll(em, Category.class).isEmpty()) {
            category = CategoryResourceIT.createUpdatedEntity(em);
            em.persist(category);
            em.flush();
        } else {
            category = TestUtil.findAll(em, Category.class).get(0);
        }
        subcategory.setCategory(category);
        return subcategory;
    }

    @BeforeEach
    public void initTest() {
        subcategory = createEntity(em);
    }

    @Test
    @Transactional
    void createSubcategory() throws Exception {
        int databaseSizeBeforeCreate = subcategoryRepository.findAll().size();
        // Create the Subcategory
        SubcategoryDTO subcategoryDTO = subcategoryMapper.toDto(subcategory);
        restSubcategoryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(subcategoryDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Subcategory in the database
        List<Subcategory> subcategoryList = subcategoryRepository.findAll();
        assertThat(subcategoryList).hasSize(databaseSizeBeforeCreate + 1);
        Subcategory testSubcategory = subcategoryList.get(subcategoryList.size() - 1);
        assertThat(testSubcategory.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void createSubcategoryWithExistingId() throws Exception {
        // Create the Subcategory with an existing ID
        subcategory.setId(1L);
        SubcategoryDTO subcategoryDTO = subcategoryMapper.toDto(subcategory);

        int databaseSizeBeforeCreate = subcategoryRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSubcategoryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(subcategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Subcategory in the database
        List<Subcategory> subcategoryList = subcategoryRepository.findAll();
        assertThat(subcategoryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = subcategoryRepository.findAll().size();
        // set the field null
        subcategory.setName(null);

        // Create the Subcategory, which fails.
        SubcategoryDTO subcategoryDTO = subcategoryMapper.toDto(subcategory);

        restSubcategoryMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(subcategoryDTO))
            )
            .andExpect(status().isBadRequest());

        List<Subcategory> subcategoryList = subcategoryRepository.findAll();
        assertThat(subcategoryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSubcategories() throws Exception {
        // Initialize the database
        subcategoryRepository.saveAndFlush(subcategory);

        // Get all the subcategoryList
        restSubcategoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(subcategory.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }

    @Test
    @Transactional
    void getSubcategory() throws Exception {
        // Initialize the database
        subcategoryRepository.saveAndFlush(subcategory);

        // Get the subcategory
        restSubcategoryMockMvc
            .perform(get(ENTITY_API_URL_ID, subcategory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(subcategory.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }

    @Test
    @Transactional
    void getNonExistingSubcategory() throws Exception {
        // Get the subcategory
        restSubcategoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewSubcategory() throws Exception {
        // Initialize the database
        subcategoryRepository.saveAndFlush(subcategory);

        int databaseSizeBeforeUpdate = subcategoryRepository.findAll().size();

        // Update the subcategory
        Subcategory updatedSubcategory = subcategoryRepository.findById(subcategory.getId()).get();
        // Disconnect from session so that the updates on updatedSubcategory are not directly saved in db
        em.detach(updatedSubcategory);
        updatedSubcategory.name(UPDATED_NAME);
        SubcategoryDTO subcategoryDTO = subcategoryMapper.toDto(updatedSubcategory);

        restSubcategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, subcategoryDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(subcategoryDTO))
            )
            .andExpect(status().isOk());

        // Validate the Subcategory in the database
        List<Subcategory> subcategoryList = subcategoryRepository.findAll();
        assertThat(subcategoryList).hasSize(databaseSizeBeforeUpdate);
        Subcategory testSubcategory = subcategoryList.get(subcategoryList.size() - 1);
        assertThat(testSubcategory.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void putNonExistingSubcategory() throws Exception {
        int databaseSizeBeforeUpdate = subcategoryRepository.findAll().size();
        subcategory.setId(count.incrementAndGet());

        // Create the Subcategory
        SubcategoryDTO subcategoryDTO = subcategoryMapper.toDto(subcategory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSubcategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, subcategoryDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(subcategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Subcategory in the database
        List<Subcategory> subcategoryList = subcategoryRepository.findAll();
        assertThat(subcategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSubcategory() throws Exception {
        int databaseSizeBeforeUpdate = subcategoryRepository.findAll().size();
        subcategory.setId(count.incrementAndGet());

        // Create the Subcategory
        SubcategoryDTO subcategoryDTO = subcategoryMapper.toDto(subcategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubcategoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(subcategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Subcategory in the database
        List<Subcategory> subcategoryList = subcategoryRepository.findAll();
        assertThat(subcategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSubcategory() throws Exception {
        int databaseSizeBeforeUpdate = subcategoryRepository.findAll().size();
        subcategory.setId(count.incrementAndGet());

        // Create the Subcategory
        SubcategoryDTO subcategoryDTO = subcategoryMapper.toDto(subcategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubcategoryMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(subcategoryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Subcategory in the database
        List<Subcategory> subcategoryList = subcategoryRepository.findAll();
        assertThat(subcategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSubcategoryWithPatch() throws Exception {
        // Initialize the database
        subcategoryRepository.saveAndFlush(subcategory);

        int databaseSizeBeforeUpdate = subcategoryRepository.findAll().size();

        // Update the subcategory using partial update
        Subcategory partialUpdatedSubcategory = new Subcategory();
        partialUpdatedSubcategory.setId(subcategory.getId());

        restSubcategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSubcategory.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSubcategory))
            )
            .andExpect(status().isOk());

        // Validate the Subcategory in the database
        List<Subcategory> subcategoryList = subcategoryRepository.findAll();
        assertThat(subcategoryList).hasSize(databaseSizeBeforeUpdate);
        Subcategory testSubcategory = subcategoryList.get(subcategoryList.size() - 1);
        assertThat(testSubcategory.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    void fullUpdateSubcategoryWithPatch() throws Exception {
        // Initialize the database
        subcategoryRepository.saveAndFlush(subcategory);

        int databaseSizeBeforeUpdate = subcategoryRepository.findAll().size();

        // Update the subcategory using partial update
        Subcategory partialUpdatedSubcategory = new Subcategory();
        partialUpdatedSubcategory.setId(subcategory.getId());

        partialUpdatedSubcategory.name(UPDATED_NAME);

        restSubcategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSubcategory.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSubcategory))
            )
            .andExpect(status().isOk());

        // Validate the Subcategory in the database
        List<Subcategory> subcategoryList = subcategoryRepository.findAll();
        assertThat(subcategoryList).hasSize(databaseSizeBeforeUpdate);
        Subcategory testSubcategory = subcategoryList.get(subcategoryList.size() - 1);
        assertThat(testSubcategory.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    void patchNonExistingSubcategory() throws Exception {
        int databaseSizeBeforeUpdate = subcategoryRepository.findAll().size();
        subcategory.setId(count.incrementAndGet());

        // Create the Subcategory
        SubcategoryDTO subcategoryDTO = subcategoryMapper.toDto(subcategory);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSubcategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, subcategoryDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(subcategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Subcategory in the database
        List<Subcategory> subcategoryList = subcategoryRepository.findAll();
        assertThat(subcategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSubcategory() throws Exception {
        int databaseSizeBeforeUpdate = subcategoryRepository.findAll().size();
        subcategory.setId(count.incrementAndGet());

        // Create the Subcategory
        SubcategoryDTO subcategoryDTO = subcategoryMapper.toDto(subcategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubcategoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(subcategoryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Subcategory in the database
        List<Subcategory> subcategoryList = subcategoryRepository.findAll();
        assertThat(subcategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSubcategory() throws Exception {
        int databaseSizeBeforeUpdate = subcategoryRepository.findAll().size();
        subcategory.setId(count.incrementAndGet());

        // Create the Subcategory
        SubcategoryDTO subcategoryDTO = subcategoryMapper.toDto(subcategory);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubcategoryMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(subcategoryDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Subcategory in the database
        List<Subcategory> subcategoryList = subcategoryRepository.findAll();
        assertThat(subcategoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSubcategory() throws Exception {
        // Initialize the database
        subcategoryRepository.saveAndFlush(subcategory);

        int databaseSizeBeforeDelete = subcategoryRepository.findAll().size();

        // Delete the subcategory
        restSubcategoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, subcategory.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Subcategory> subcategoryList = subcategoryRepository.findAll();
        assertThat(subcategoryList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
