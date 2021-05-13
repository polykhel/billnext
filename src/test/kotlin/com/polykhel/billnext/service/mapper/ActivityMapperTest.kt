package com.polykhel.billnext.service.mapper

import org.junit.jupiter.api.BeforeEach

class ActivityMapperTest {

    private lateinit var activityMapper: ActivityMapper

    @BeforeEach
    fun setUp() {
        activityMapper = ActivityMapperImpl()
    }
}
