package cookiefactory.it.citrus.cucumber.dry;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        strict = true,
        //tags = { "@negative" },
        glue = { "com.consol.citrus.cucumber.step.runner.core",
                "cookiefactory.it.citrus.cucumber.dry.gluecode" },
        format = { "json:target/cucumber.json" },
        plugin = { "com.consol.citrus.cucumber.CitrusReporter" }
)
public class TestRunner {
}
