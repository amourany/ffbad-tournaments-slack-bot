package fr.amou.ffbad.tournaments.slack.bot

import com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import com.tngtech.archunit.lang.syntax.elements.ClassesShouldConjunction

@AnalyzeClasses(
        importOptions = [ DoNotIncludeTests::class ],
        packages = ["fr.amou.ffbad.tournaments.slack.bot"]
)
class HexagonalArchitectureTest {

    companion object {
        const val APIS = "..domain.api.."
        const val APPLICATION = "..domain.application.."
        const val CORE_DOMAIN = "..domain.core.."
        const val DRIVEN_INFRA = "..infra.driven.."
        const val DRIVING_INFRA = "..infra.driving.."
        const val MODEL = "..domain.model.."
        const val SPIS = "..domain.spi.."
    }

    @ArchTest
    val apiAllowAccess: ClassesShouldConjunction = ArchRuleDefinition
            .classes().that().resideInAPackage(APIS)
            .should().onlyHaveDependentClassesThat().resideInAnyPackage(APIS, APPLICATION, DRIVING_INFRA)

    @ArchTest
    val apiDenyAccess: ClassesShouldConjunction = ArchRuleDefinition
            .noClasses().that().resideInAPackage(APIS)
            .should().dependOnClassesThat().resideInAnyPackage(CORE_DOMAIN, DRIVING_INFRA, DRIVEN_INFRA, SPIS)

    @ArchTest
    val applicationDenyAccess: ClassesShouldConjunction = ArchRuleDefinition
            .noClasses().that().resideInAnyPackage(APIS, DRIVEN_INFRA, DRIVING_INFRA, CORE_DOMAIN, SPIS)
            .should().dependOnClassesThat().resideInAPackage(APPLICATION)

    @ArchTest
    val coreDomainAllowAccess: ClassesShouldConjunction = ArchRuleDefinition
            .classes().that().resideInAPackage(CORE_DOMAIN)
            .should().onlyHaveDependentClassesThat().resideInAnyPackage(APPLICATION, CORE_DOMAIN)

    @ArchTest
    val coreDomainDenyAccess: ClassesShouldConjunction = ArchRuleDefinition
            .noClasses().that().resideInAPackage(CORE_DOMAIN)
            .should().dependOnClassesThat().resideInAnyPackage(APIS, APPLICATION, DRIVEN_INFRA, DRIVING_INFRA)

    @ArchTest
    val drivenInfraDenyAccess: ClassesShouldConjunction = ArchRuleDefinition
            .noClasses().that().resideInAPackage(DRIVING_INFRA)
            .should().dependOnClassesThat().resideInAPackage(DRIVEN_INFRA)

    @ArchTest
    val drivingInfraDenyAccess: ClassesShouldConjunction = ArchRuleDefinition
            .noClasses().that().resideInAPackage(DRIVEN_INFRA)
            .should().dependOnClassesThat().resideInAPackage(DRIVING_INFRA)

    @ArchTest
    val modelDenyAccess: ClassesShouldConjunction = ArchRuleDefinition
            .noClasses().that().resideInAPackage(MODEL)
            .should().dependOnClassesThat().resideInAnyPackage(APIS, APPLICATION, CORE_DOMAIN, DRIVEN_INFRA, DRIVING_INFRA, SPIS)

    @ArchTest
    val spiAllowAccess: ClassesShouldConjunction = ArchRuleDefinition
            .classes().that().resideInAPackage(SPIS)
            .should().onlyHaveDependentClassesThat().resideInAnyPackage(CORE_DOMAIN, DRIVEN_INFRA, SPIS)

}
