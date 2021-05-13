package com.polykhel.billnext.service.mapper

import org.junit.jupiter.api.BeforeEach

class SubcategoryMapperTest {

    private lateinit var subcategoryMapper: SubcategoryMapper

    @BeforeEach
    fun setUp() {
        subcategoryMapper = SubcategoryMapperImpl()
    }
}
