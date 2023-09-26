package de.nikoconsulting.demo.hexagontransmon;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import de.nikoconsulting.demo.hexagontransmon.archunit.HexagonalArchitecture;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class DependencyRulesTest {

    private final JavaClasses classes = new ClassFileImporter().withImportOption(new ImportOption.DoNotIncludeTests())
            .importPackages("de.nikoconsulting.demo.hexagontransmon..");

    @Test
    void domainModelDoesNotDependOnOutside() {
        noClasses()
                .that()
                .resideInAPackage("de.nikoconsulting.demo.hexagontransmon.app.domain.model..")
                .should()
                .dependOnClassesThat()
                .resideOutsideOfPackages(
                        "de.nikoconsulting.demo.hexagontransmon.app.domain.model..",
                        "lombok..",
                        "java.."
                )
                .check(classes);
    }


    @Test
    public void services_hould_ot_access_controllers() {
        noClasses().that().resideInAPackage("..service..")
                .should().accessClassesThat().resideInAPackage("..in.web..").check(classes);
    }


    @Test
    public void persistence_should_not_access_services() {
        noClasses().that().resideInAPackage("..out.persistence..")
                .should().accessClassesThat().resideInAPackage("..app.domain.service..").check(classes);
    }

    @Test
    void validateRegistrationContextArchitecture() {
        HexagonalArchitecture.basePackage("de.nikoconsulting.demo.hexagontransmon")

                .withDomainLayer("app.domain")

                .withAdaptersLayer("adapter")
                    .incoming("in.web")
                    .outgoing("out.persistence")
                    .and()

                .withApplicationLayer("app")
                    .incomingPorts("port.in")
                    .outgoingPorts("port.out")
                    .services("domain.service")
                .and()

                //.withConfiguration("configuration")
                .check(classes);
    }


}
