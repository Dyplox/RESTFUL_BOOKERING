package co.booker.runners;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Suite única de JUnit Platform que ejecuta todas las features de {@code src/test/resources/features}
 * con el motor de Cucumber.
 *
 * <p>La configuración (glue, plugins, paralelismo y filtro de tags por defecto) está en
 * {@code src/test/resources/junit-platform.properties}. Para ejecutar un subconjunto se filtra por
 * tags desde Gradle: {@code ./gradlew test -Ptags="@smoke"}.</p>
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
public class CucumberTestSuite {
}
