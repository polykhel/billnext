package com.polykhel.billnext.web.rest

import com.polykhel.billnext.IntegrationTest
import com.polykhel.billnext.domain.Category
import com.polykhel.billnext.domain.Subcategory
import com.polykhel.billnext.repository.SubcategoryRepository
import com.polykhel.billnext.service.mapper.SubcategoryMapper
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
 * Integration tests for the [SubcategoryResource] REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SubcategoryResourceIT {
    @Autowired
    private lateinit var subcategoryRepository: SubcategoryRepository

    @Autowired
    private lateinit var subcategoryMapper: SubcategoryMapper

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
    private lateinit var restSubcategoryMockMvc: MockMvc

    private lateinit var subcategory: Subcategory

    @BeforeEach
    fun initTest() {
        subcategory = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createSubcategory() {
        val databaseSizeBeforeCreate = subcategoryRepository.findAll().size

        // Create the Subcategory
        val subcategoryDTO = subcategoryMapper.toDto(subcategory)
        restSubcategoryMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(subcategoryDTO))
        ).andExpect(status().isCreated)

        // Validate the Subcategory in the database
        val subcategoryList = subcategoryRepository.findAll()
        assertThat(subcategoryList).hasSize(databaseSizeBeforeCreate + 1)
        val testSubcategory = subcategoryList[subcategoryList.size - 1]

        assertThat(testSubcategory.name).isEqualTo(DEFAULT_NAME)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createSubcategoryWithExistingId() {
        // Create the Subcategory with an existing ID
        subcategory.id = 1L
        val subcategoryDTO = subcategoryMapper.toDto(subcategory)

        val databaseSizeBeforeCreate = subcategoryRepository.findAll().size

        // An entity with an existing ID cannot be created, so this API call must fail
        restSubcategoryMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(subcategoryDTO))
        ).andExpect(status().isBadRequest)

        // Validate the Subcategory in the database
        val subcategoryList = subcategoryRepository.findAll()
        assertThat(subcategoryList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun checkNameIsRequired() {
        val databaseSizeBeforeTest = subcategoryRepository.findAll().size
        // set the field null
        subcategory.name = null

        // Create the Subcategory, which fails.
        val subcategoryDTO = subcategoryMapper.toDto(subcategory)

        restSubcategoryMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(subcategoryDTO))
        ).andExpect(status().isBadRequest)

        val subcategoryList = subcategoryRepository.findAll()
        assertThat(subcategoryList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllSubcategories() {
        // Initialize the database
        subcategoryRepository.saveAndFlush(subcategory)

        // Get all the subcategoryList
        restSubcategoryMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(subcategory.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getSubcategory() {
        // Initialize the database
        subcategoryRepository.saveAndFlush(subcategory)

        val id = subcategory.id
        assertNotNull(id)

        // Get the subcategory
        restSubcategoryMockMvc.perform(get(ENTITY_API_URL_ID, subcategory.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(subcategory.id?.toInt()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
    }
    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingSubcategory() {
        // Get the subcategory
        restSubcategoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun putNewSubcategory() {
        // Initialize the database
        subcategoryRepository.saveAndFlush(subcategory)

        val databaseSizeBeforeUpdate = subcategoryRepository.findAll().size

        // Update the subcategory
        val updatedSubcategory = subcategoryRepository.findById(subcategory.id).get()
        // Disconnect from session so that the updates on updatedSubcategory are not directly saved in db
        em.detach(updatedSubcategory)
        updatedSubcategory.name = UPDATED_NAME
        val subcategoryDTO = subcategoryMapper.toDto(updatedSubcategory)

        restSubcategoryMockMvc.perform(
            put(ENTITY_API_URL_ID, subcategoryDTO.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(subcategoryDTO))
        ).andExpect(status().isOk)

        // Validate the Subcategory in the database
        val subcategoryList = subcategoryRepository.findAll()
        assertThat(subcategoryList).hasSize(databaseSizeBeforeUpdate)
        val testSubcategory = subcategoryList[subcategoryList.size - 1]
        assertThat(testSubcategory.name).isEqualTo(UPDATED_NAME)
    }

    @Test
    @Transactional
    fun putNonExistingSubcategory() {
        val databaseSizeBeforeUpdate = subcategoryRepository.findAll().size
        subcategory.id = count.incrementAndGet()

        // Create the Subcategory
        val subcategoryDTO = subcategoryMapper.toDto(subcategory)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSubcategoryMockMvc.perform(
            put(ENTITY_API_URL_ID, subcategoryDTO.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(subcategoryDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the Subcategory in the database
        val subcategoryList = subcategoryRepository.findAll()
        assertThat(subcategoryList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithIdMismatchSubcategory() {
        val databaseSizeBeforeUpdate = subcategoryRepository.findAll().size
        subcategory.id = count.incrementAndGet()

        // Create the Subcategory
        val subcategoryDTO = subcategoryMapper.toDto(subcategory)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubcategoryMockMvc.perform(
            put(ENTITY_API_URL_ID, count.incrementAndGet())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(subcategoryDTO))
        ).andExpect(status().isBadRequest)

        // Validate the Subcategory in the database
        val subcategoryList = subcategoryRepository.findAll()
        assertThat(subcategoryList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithMissingIdPathParamSubcategory() {
        val databaseSizeBeforeUpdate = subcategoryRepository.findAll().size
        subcategory.id = count.incrementAndGet()

        // Create the Subcategory
        val subcategoryDTO = subcategoryMapper.toDto(subcategory)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubcategoryMockMvc.perform(
            put(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(subcategoryDTO))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the Subcategory in the database
        val subcategoryList = subcategoryRepository.findAll()
        assertThat(subcategoryList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun partialUpdateSubcategoryWithPatch() {

        // Initialize the database
        subcategoryRepository.saveAndFlush(subcategory)

        val databaseSizeBeforeUpdate = subcategoryRepository.findAll().size

// Update the subcategory using partial update
        val partialUpdatedSubcategory = Subcategory().apply {
            id = subcategory.id

            name = UPDATED_NAME
        }

        restSubcategoryMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedSubcategory.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedSubcategory))
        )
            .andExpect(status().isOk)

// Validate the Subcategory in the database
        val subcategoryList = subcategoryRepository.findAll()
        assertThat(subcategoryList).hasSize(databaseSizeBeforeUpdate)
        val testSubcategory = subcategoryList.last()
        assertThat(testSubcategory.name).isEqualTo(UPDATED_NAME)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun fullUpdateSubcategoryWithPatch() {

        // Initialize the database
        subcategoryRepository.saveAndFlush(subcategory)

        val databaseSizeBeforeUpdate = subcategoryRepository.findAll().size

// Update the subcategory using partial update
        val partialUpdatedSubcategory = Subcategory().apply {
            id = subcategory.id

            name = UPDATED_NAME
        }

        restSubcategoryMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedSubcategory.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedSubcategory))
        )
            .andExpect(status().isOk)

// Validate the Subcategory in the database
        val subcategoryList = subcategoryRepository.findAll()
        assertThat(subcategoryList).hasSize(databaseSizeBeforeUpdate)
        val testSubcategory = subcategoryList.last()
        assertThat(testSubcategory.name).isEqualTo(UPDATED_NAME)
    }

    @Throws(Exception::class)
    fun patchNonExistingSubcategory() {
        val databaseSizeBeforeUpdate = subcategoryRepository.findAll().size
        subcategory.id = count.incrementAndGet()

        // Create the Subcategory
        val subcategoryDTO = subcategoryMapper.toDto(subcategory)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSubcategoryMockMvc.perform(
            patch(ENTITY_API_URL_ID, subcategoryDTO.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(subcategoryDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the Subcategory in the database
        val subcategoryList = subcategoryRepository.findAll()
        assertThat(subcategoryList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithIdMismatchSubcategory() {
        val databaseSizeBeforeUpdate = subcategoryRepository.findAll().size
        subcategory.id = count.incrementAndGet()

        // Create the Subcategory
        val subcategoryDTO = subcategoryMapper.toDto(subcategory)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubcategoryMockMvc.perform(
            patch(ENTITY_API_URL_ID, count.incrementAndGet())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(subcategoryDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the Subcategory in the database
        val subcategoryList = subcategoryRepository.findAll()
        assertThat(subcategoryList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithMissingIdPathParamSubcategory() {
        val databaseSizeBeforeUpdate = subcategoryRepository.findAll().size
        subcategory.id = count.incrementAndGet()

        // Create the Subcategory
        val subcategoryDTO = subcategoryMapper.toDto(subcategory)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSubcategoryMockMvc.perform(
            patch(ENTITY_API_URL)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(subcategoryDTO))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the Subcategory in the database
        val subcategoryList = subcategoryRepository.findAll()
        assertThat(subcategoryList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteSubcategory() {
        // Initialize the database
        subcategoryRepository.saveAndFlush(subcategory)

        val databaseSizeBeforeDelete = subcategoryRepository.findAll().size

        // Delete the subcategory
        restSubcategoryMockMvc.perform(
            delete(ENTITY_API_URL_ID, subcategory.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val subcategoryList = subcategoryRepository.findAll()
        assertThat(subcategoryList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_NAME = "AAAAAAAAAA"
        private const val UPDATED_NAME = "BBBBBBBBBB"

        private val ENTITY_API_URL: String = "/api/subcategories"
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
        fun createEntity(em: EntityManager): Subcategory {
            val subcategory = Subcategory(

                name = DEFAULT_NAME

            )

            // Add required entity
            val category: Category
            if (em.findAll(Category::class).isEmpty()) {
                category = CategoryResourceIT.createEntity(em)
                em.persist(category)
                em.flush()
            } else {
                category = em.findAll(Category::class).get(0)
            }
            subcategory.category = category
            return subcategory
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Subcategory {
            val subcategory = Subcategory(

                name = UPDATED_NAME

            )

            // Add required entity
            val category: Category
            if (em.findAll(Category::class).isEmpty()) {
                category = CategoryResourceIT.createUpdatedEntity(em)
                em.persist(category)
                em.flush()
            } else {
                category = em.findAll(Category::class).get(0)
            }
            subcategory.category = category
            return subcategory
        }
    }
}
