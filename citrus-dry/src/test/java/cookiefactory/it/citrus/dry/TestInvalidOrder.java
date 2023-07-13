package cookiefactory.it.citrus.dry;

import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.config.CitrusSpringConfig;
import com.consol.citrus.dsl.junit.JUnit4CitrusTest;
import com.consol.citrus.dsl.runner.TestRunner;
import cookiefactory.it.citrus.dry.behavior.CookieOrderServiceBehavior;
import cookiefactory.it.citrus.dry.configuration.HttpConfiguration;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {CitrusSpringConfig.class, HttpConfiguration.class})
@DirtiesContext
public class TestInvalidOrder extends JUnit4CitrusTest {

    @Test
    @CitrusTest
    public void testInvalidOrder(@CitrusResource TestRunner runner) {
        runner.description("Send an invalid order and make sure no cookies are produced");

        CookieOrderServiceBehavior callOrderService = new CookieOrderServiceBehavior("httpClient");
        runner.applyBehavior(
                callOrderService.withRequestPayload("{ \"flavour\":\"notExistingFlavour\", \"quantity\":42 }")
                .assertResponseWith("Sorry, your order is invalid! \n@ignore(200)@")
                .assertResponseHeader("citrus_http_status_code", 400)
        );
    }
}
