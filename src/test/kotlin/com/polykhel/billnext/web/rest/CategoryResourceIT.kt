package com.polykhel.billnext.web.rest

import com.polykhel.billnext.IntegrationTest
import com.polykhel.billnext.domain.Category
import com.polykhel.billnext.domain.enumeration.ActivityType
import com.polykhel.billnext.repository.CategoryRepository
import com.polykhel.billnext.service.mapper.CategoryMapper
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
 * Integration tests for the [CategoryResource] REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CategoryResourceIT {
    @Autowired
    private lateinit var categoryRepository: CategoryRepository

    @Autowired
    private lateinit var categoryMapper: CategoryMapper

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
    private lateinit var restCategoryMockMvc: MockMvc

    private lateinit var category: Category

    @BeforeEach
    fun initTest() {
        category = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createCategory() {
        val databaseSizeBeforeCreate = categoryRepository.findAll().size

        // Create the Category
        val categoryDTO = categoryMapper.toDto(category)
        restCategoryMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(categoryDTO))
        ).andExpect(status().isCreated)

        // Validate the Category in the database
        val categoryList = categoryRepository.findAll()
        assertThat(categoryList).hasSize(databaseSizeBeforeCreate + 1)
        val testCategory = categoryList[categoryList.size - 1]

        assertThat(testCategory.name).isEqualTo(DEFAULT_NAME)
        assertThat(testCategory.type).isEqualTo(DEFAULT_TYPE)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createCategoryWithExistingId() {
        // Create the Category with an existing ID
        category.id = 1L
        val categoryDTO = categoryMapper.toDto(category)

        val databaseSizeBeforeCreate = categoryRepository.findAll().size

        // An entity with an existing ID cannot be created, so this API call must fail
        restCategoryMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(categoryDTO))
        ).andExpect(status().isBadRequest)

        // Validate the Category in the database
        val categoryList = categoryRepository.findAll()
        assertThat(categoryList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun checkNameIsRequired() {
        val databaseSizeBeforeTest = categoryRepository.findAll().size
        // set the field null
        category.name = null

        // Create the Category, which fails.
        val categoryDTO = categoryMapper.toDto(category)

        restCategoryMockMvc.perform(
            post(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(categoryDTO))
        ).andExpect(status().isBadRequest)

        val categoryList = categoryRepository.findAll()
        assertThat(categoryList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllCategories() {
        // Initialize the database
        categoryRepository.saveAndFlush(category)

        // Get all the categoryList
        restCategoryMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(category.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getCategory() {
        // Initialize the database
        categoryRepository.saveAndFlush(category)

        val id = category.id
        assertNotNull(id)

        // Get the category
        restCategoryMockMvc.perform(get(ENTITY_API_URL_ID, category.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(category.id?.toInt()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getCategoriesByIdFiltering() {
        // Initialize the database
        categoryRepository.saveAndFlush(category)
        val id = category.id

        defaultCategoryShouldBeFound("id.equals=" + id)
        defaultCategoryShouldNotBeFound("id.notEquals=" + id)
        defaultCategoryShouldBeFound("id.greaterThanOrEqual=" + id)
        defaultCategoryShouldNotBeFound("id.greaterThan=" + id)

        defaultCategoryShouldBeFound("id.lessThanOrEqual=" + id)
        defaultCategoryShouldNotBeFound("id.lessThan=" + id)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllCategoriesByNameIsEqualToSomething() {
        // Initialize the database
        categoryRepository.saveAndFlush(category)

        // Get all the categoryList where name equals to DEFAULT_NAME
        defaultCategoryShouldBeFound("name.equals=$DEFAULT_NAME")

        // Get all the categoryList where name equals to UPDATED_NAME
        defaultCategoryShouldNotBeFound("name.equals=$UPDATED_NAME")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllCategoriesByNameIsNotEqualToSomething() {
        // Initialize the database
        categoryRepository.saveAndFlush(category)

        // Get all the categoryList where name not equals to DEFAULT_NAME
        defaultCategoryShouldNotBeFound("name.notEquals=" + DEFAULT_NAME)

        // Get all the categoryList where name not equals to UPDATED_NAME
        defaultCategoryShouldBeFound("name.notEquals=" + UPDATED_NAME)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllCategoriesByNameIsInShouldWork() {
        // Initialize the database
        categoryRepository.saveAndFlush(category)

        // Get all the categoryList where name in DEFAULT_NAME or UPDATED_NAME
        defaultCategoryShouldBeFound("name.in=$DEFAULT_NAME,$UPDATED_NAME")

        // Get all the categoryList where name equals to UPDATED_NAME
        defaultCategoryShouldNotBeFound("name.in=$UPDATED_NAME")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllCategoriesByNameIsNullOrNotNull() {
        // Initialize the database
        categoryRepository.saveAndFlush(category)

        // Get all the categoryList where name is not null
        defaultCategoryShouldBeFound("name.specified=true")

        // Get all the categoryList where name is null
        defaultCategoryShouldNotBeFound("name.specified=false")
    }
    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllCategoriesByNameContainsSomething() {
        // Initialize the database
        categoryRepository.saveAndFlush(category)

        // Get all the categoryList where name contains DEFAULT_NAME
        defaultCategoryShouldBeFound("name.contains=" + DEFAULT_NAME)

        // Get all the categoryList where name contains UPDATED_NAME
        defaultCategoryShouldNotBeFound("name.contains=" + UPDATED_NAME)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllCategoriesByNameNotContainsSomething() {
        // Initialize the database
        categoryRepository.saveAndFlush(category)

        // Get all the categoryList where name does not contain DEFAULT_NAME
        defaultCategoryShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME)

        // Get all the categoryList where name does not contain UPDATED_NAME
        defaultCategoryShouldBeFound("name.doesNotContain=" + UPDATED_NAME)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllCategoriesByTypeIsEqualToSomething() {
        // Initialize the database
        categoryRepository.saveAndFlush(category)

        // Get all the categoryList where type equals to DEFAULT_TYPE
        defaultCategoryShouldBeFound("type.equals=$DEFAULT_TYPE")

        // Get all the categoryList where type equals to UPDATED_TYPE
        defaultCategoryShouldNotBeFound("type.equals=$UPDATED_TYPE")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllCategoriesByTypeIsNotEqualToSomething() {
        // Initialize the database
        categoryRepository.saveAndFlush(category)

        // Get all the categoryList where type not equals to DEFAULT_TYPE
        defaultCategoryShouldNotBeFound("type.notEquals=" + DEFAULT_TYPE)

        // Get all the categoryList where type not equals to UPDATED_TYPE
        defaultCategoryShouldBeFound("type.notEquals=" + UPDATED_TYPE)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllCategoriesByTypeIsInShouldWork() {
        // Initialize the database
        categoryRepository.saveAndFlush(category)

        // Get all the categoryList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultCategoryShouldBeFound("type.in=$DEFAULT_TYPE,$UPDATED_TYPE")

        // Get all the categoryList where type equals to UPDATED_TYPE
        defaultCategoryShouldNotBeFound("type.in=$UPDATED_TYPE")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllCategoriesByTypeIsNullOrNotNull() {
        // Initialize the database
        categoryRepository.saveAndFlush(category)

        // Get all the categoryList where type is not null
        defaultCategoryShouldBeFound("type.specified=true")

        // Get all the categoryList where type is null
        defaultCategoryShouldNotBeFound("type.specified=false")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllCategoriesByUserIsEqualToSomething() {
        // Initialize the database
        categoryRepository.saveAndFlush(category)
        val user = UserResourceIT.createEntity(em)
        em.persist(user)
        em.flush()
        category.user = user
        categoryRepository.saveAndFlush(category)
        val userId = user.id

        // Get all the categoryList where user equals to userId
        defaultCategoryShouldBeFound("userId.equals=$userId")

        // Get all the categoryList where user equals to (userId?.plus(1))
        defaultCategoryShouldNotBeFound("userId.equals=${(userId?.plus(1))}")
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllCategoriesByActivityIsEqualToSomething() {
        // Initialize the database
        categoryRepository.saveAndFlush(category)
        val activity = ActivityResourceIT.createEntity(em)
        em.persist(activity)
        em.flush()
        category.addActivity(activity)
        categoryRepository.saveAndFlush(category)
        val activityId = activity.id

        // Get all the categoryList where activity equals to activityId
        defaultCategoryShouldBeFound("activityId.equals=$activityId")

        // Get all the categoryList where activity equals to (activityId?.plus(1))
        defaultCategoryShouldNotBeFound("activityId.equals=${(activityId?.plus(1))}")
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */

    @Throws(Exception::class)
    private fun defaultCategoryShouldBeFound(filter: String) {
        restCategoryMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(category.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))

        // Check, that the count call also returns 1
        restCategoryMockMvc.perform(get(ENTITY_API_URL + "/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"))
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    @Throws(Exception::class)
    private fun defaultCategoryShouldNotBeFound(filter: String) {
        restCategoryMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray)
            .andExpect(jsonPath("$").isEmpty)

        // Check, that the count call also returns 0
        restCategoryMockMvc.perform(get(ENTITY_API_URL + "/count?sort=id,desc&$filter"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"))
    }
    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingCategory() {
        // Get the category
        restCategoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun putNewCategory() {
        // Initialize the database
        categoryRepository.saveAndFlush(category)

        val databaseSizeBeforeUpdate = categoryRepository.findAll().size

        // Update the category
        val updatedCategory = categoryRepository.findById(category.id).get()
        // Disconnect from session so that the updates on updatedCategory are not directly saved in db
        em.detach(updatedCategory)
        updatedCategory.name = UPDATED_NAME
        updatedCategory.type = UPDATED_TYPE
        val categoryDTO = categoryMapper.toDto(updatedCategory)

        restCategoryMockMvc.perform(
            put(ENTITY_API_URL_ID, categoryDTO.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(categoryDTO))
        ).andExpect(status().isOk)

        // Validate the Category in the database
        val categoryList = categoryRepository.findAll()
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate)
        val testCategory = categoryList[categoryList.size - 1]
        assertThat(testCategory.name).isEqualTo(UPDATED_NAME)
        assertThat(testCategory.type).isEqualTo(UPDATED_TYPE)
    }

    @Test
    @Transactional
    fun putNonExistingCategory() {
        val databaseSizeBeforeUpdate = categoryRepository.findAll().size
        category.id = count.incrementAndGet()

        // Create the Category
        val categoryDTO = categoryMapper.toDto(category)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCategoryMockMvc.perform(
            put(ENTITY_API_URL_ID, categoryDTO.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(categoryDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the Category in the database
        val categoryList = categoryRepository.findAll()
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithIdMismatchCategory() {
        val databaseSizeBeforeUpdate = categoryRepository.findAll().size
        category.id = count.incrementAndGet()

        // Create the Category
        val categoryDTO = categoryMapper.toDto(category)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCategoryMockMvc.perform(
            put(ENTITY_API_URL_ID, count.incrementAndGet())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(categoryDTO))
        ).andExpect(status().isBadRequest)

        // Validate the Category in the database
        val categoryList = categoryRepository.findAll()
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithMissingIdPathParamCategory() {
        val databaseSizeBeforeUpdate = categoryRepository.findAll().size
        category.id = count.incrementAndGet()

        // Create the Category
        val categoryDTO = categoryMapper.toDto(category)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCategoryMockMvc.perform(
            put(ENTITY_API_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(categoryDTO))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the Category in the database
        val categoryList = categoryRepository.findAll()
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun partialUpdateCategoryWithPatch() {

        // Initialize the database
        categoryRepository.saveAndFlush(category)

        val databaseSizeBeforeUpdate = categoryRepository.findAll().size

// Update the category using partial update
        val partialUpdatedCategory = Category().apply {
            id = category.id

            name = UPDATED_NAME
        }

        restCategoryMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedCategory.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedCategory))
        )
            .andExpect(status().isOk)

// Validate the Category in the database
        val categoryList = categoryRepository.findAll()
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate)
        val testCategory = categoryList.last()
        assertThat(testCategory.name).isEqualTo(UPDATED_NAME)
        assertThat(testCategory.type).isEqualTo(DEFAULT_TYPE)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun fullUpdateCategoryWithPatch() {

        // Initialize the database
        categoryRepository.saveAndFlush(category)

        val databaseSizeBeforeUpdate = categoryRepository.findAll().size

// Update the category using partial update
        val partialUpdatedCategory = Category().apply {
            id = category.id

            name = UPDATED_NAME
            type = UPDATED_TYPE
        }

        restCategoryMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedCategory.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedCategory))
        )
            .andExpect(status().isOk)

// Validate the Category in the database
        val categoryList = categoryRepository.findAll()
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate)
        val testCategory = categoryList.last()
        assertThat(testCategory.name).isEqualTo(UPDATED_NAME)
        assertThat(testCategory.type).isEqualTo(UPDATED_TYPE)
    }

    @Throws(Exception::class)
    fun patchNonExistingCategory() {
        val databaseSizeBeforeUpdate = categoryRepository.findAll().size
        category.id = count.incrementAndGet()

        // Create the Category
        val categoryDTO = categoryMapper.toDto(category)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCategoryMockMvc.perform(
            patch(ENTITY_API_URL_ID, categoryDTO.id)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(categoryDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the Category in the database
        val categoryList = categoryRepository.findAll()
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithIdMismatchCategory() {
        val databaseSizeBeforeUpdate = categoryRepository.findAll().size
        category.id = count.incrementAndGet()

        // Create the Category
        val categoryDTO = categoryMapper.toDto(category)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCategoryMockMvc.perform(
            patch(ENTITY_API_URL_ID, count.incrementAndGet())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(categoryDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the Category in the database
        val categoryList = categoryRepository.findAll()
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithMissingIdPathParamCategory() {
        val databaseSizeBeforeUpdate = categoryRepository.findAll().size
        category.id = count.incrementAndGet()

        // Create the Category
        val categoryDTO = categoryMapper.toDto(category)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCategoryMockMvc.perform(
            patch(ENTITY_API_URL)
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(categoryDTO))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the Category in the database
        val categoryList = categoryRepository.findAll()
        assertThat(categoryList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteCategory() {
        // Initialize the database
        categoryRepository.saveAndFlush(category)

        val databaseSizeBeforeDelete = categoryRepository.findAll().size

        // Delete the category
        restCategoryMockMvc.perform(
            delete(ENTITY_API_URL_ID, category.id)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val categoryList = categoryRepository.findAll()
        assertThat(categoryList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_NAME = "AAAAAAAAAA"
        private const val UPDATED_NAME = "BBBBBBBBBB"

        private val DEFAULT_TYPE: ActivityType = ActivityType.INCOME
        private val UPDATED_TYPE: ActivityType = ActivityType.EXPENSE

        private val ENTITY_API_URL: String = "/api/categories"
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
        fun createEntity(em: EntityManager): Category {
            val category = Category(

                name = DEFAULT_NAME,

                type = DEFAULT_TYPE

            )

            // Add required entity
            val user = UserResourceIT.createEntity(em)
            em.persist(user)
            em.flush()
            category.user = user
            return category
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Category {
            val category = Category(

                name = UPDATED_NAME,

                type = UPDATED_TYPE

            )

            // Add required entity
            val user = UserResourceIT.createEntity(em)
            em.persist(user)
            em.flush()
            category.user = user
            return category
        }
    }
}
