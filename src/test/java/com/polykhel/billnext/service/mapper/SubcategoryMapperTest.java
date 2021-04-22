package com.polykhel.billnext.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SubcategoryMapperTest {

    private SubcategoryMapper subcategoryMapper;

    @BeforeEach
    public void setUp() {
        subcategoryMapper = new SubcategoryMapperImpl();
    }
}
