package com.polykhel.billnext.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;

class ActivityMapperTest {

    private ActivityMapper activityMapper;

    @BeforeEach
    public void setUp() {
        activityMapper = new ActivityMapperImpl();
    }
}
