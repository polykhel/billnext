package com.polykhel.billnext.service.mapper

import org.junit.jupiter.api.BeforeEach

class CategoryMapperTest {

    private lateinit var categoryMapper: CategoryMapper

    @BeforeEach
    fun setUp() {
        categoryMapper = CategoryMapperImpl()
    }
}
