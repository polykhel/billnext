package com.polykhel.billnext

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Test

class ArchTest {

    @Test
    fun servicesAndRepositoriesShouldNotDependOnWebLayer() {

        val importedClasses = ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.polykhel.billnext")

        noClasses()
            .that()
            .resideInAnyPackage("com.polykhel.billnext.service..")
            .or()
            .resideInAnyPackage("com.polykhel.billnext.repository..")
            .should().dependOnClassesThat()
            .resideInAnyPackage("..com.polykhel.billnext.web..")
            .because("Services and repositories should not depend on web layer")
            .check(importedClasses)
    }
}
