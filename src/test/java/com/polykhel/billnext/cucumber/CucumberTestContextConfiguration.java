package com.polykhel.billnext.cucumber;

import com.polykhel.billnext.BillNextApp;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

@CucumberContextConfiguration
@SpringBootTest(classes = BillNextApp.class)
@WebAppConfiguration
public class CucumberTestContextConfiguration {}
