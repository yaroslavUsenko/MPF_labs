package sumdu.edu.ua.architecture;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.domain.JavaClasses;
import org.testng.annotations.Test;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

public class CoreArchitectureTest {
    @Test
    public void coreShouldNotDependOnServletOrJdbc() {
        JavaClasses imported = new ClassFileImporter().importPackages("sumdu.edu.ua.core");

        noClasses()
                .that().resideInAPackage("..core..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("jakarta.servlet..", "java.sql..")
                .check(imported);
    }
}
