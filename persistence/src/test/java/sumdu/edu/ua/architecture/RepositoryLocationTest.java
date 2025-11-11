package sumdu.edu.ua.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class RepositoryLocationTest {
    @Test
    public void coreShouldNotDependOnServletOrJdbc() {
        JavaClasses imported = new ClassFileImporter().importPackages("sumdu.edu.ua");

        classes()
                .that().haveSimpleNameEndingWith("Repository")
                .should().resideInAPackage("..persistence..")
                .check(imported);
    }
}
