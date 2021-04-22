package com.polykhel.billnext;

import com.polykhel.billnext.BillNextApp;
import com.polykhel.billnext.config.TestSecurityConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { BillNextApp.class, TestSecurityConfiguration.class })
public @interface IntegrationTest {
}
