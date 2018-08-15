package cookiefactory.it.citrus.dry;

import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.config.CitrusSpringConfig;
import com.consol.citrus.dsl.junit.JUnit4CitrusTest;
import com.consol.citrus.dsl.runner.TestRunner;
import cookiefactory.it.citrus.dry.behavior.CookieOrderServiceBehavior;
import cookiefactory.it.citrus.dry.behavior.CookieProductionResultBehavior;
import cookiefactory.it.citrus.dry.behavior.PurgeCookieQueues;
import cookiefactory.it.citrus.dry.configuration.HttpConfiguration;
import cookiefactory.it.citrus.dry.configuration.JmsConfiguration;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import javax.jms.ConnectionFactory;

@ContextConfiguration(classes = {CitrusSpringConfig.class, HttpConfiguration.class, JmsConfiguration.class})
@DirtiesContext
public class TestValidOrderWithoutService extends JUnit4CitrusTest {

    @Autowired
    private ConnectionFactory jmsConnectionFactory;

    @Test
    @CitrusTest
    public void testValidOrderWithoutService(@CitrusResource TestRunner runner) {
        runner.description("Send a valid order and make sure the correct number/flavour of cookies are produced.");

        PurgeCookieQueues purgeQueues = new PurgeCookieQueues(jmsConnectionFactory);
        runner.applyBehavior(purgeQueues);

        CookieOrderServiceBehavior callOrderService = new CookieOrderServiceBehavior("httpClient");
        runner.applyBehavior(
                callOrderService.withRequestPayload("{ \"flavour\":\"chocolate\", \"quantity\":42 }")
                        .assertResponseWith("Thank you for your order!")
                        .assertResponseHeader("citrus_http_status_code", 200)
        );

        CookieProductionResultBehavior receiveProducedCookies = new CookieProductionResultBehavior("outputQueue");
        runner.applyBehavior(
                receiveProducedCookies.assertPayloadWith("")
                        .assertHeader("flavour", "chocolate")
                        .assertHeader("quantity", 42)
        );

    }
}
