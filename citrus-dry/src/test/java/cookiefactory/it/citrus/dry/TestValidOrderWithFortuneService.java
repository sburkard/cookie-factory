package cookiefactory.it.citrus.dry;

import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.config.CitrusSpringConfig;
import com.consol.citrus.dsl.junit.JUnit4CitrusTest;
import com.consol.citrus.dsl.runner.TestRunner;
import cookiefactory.it.citrus.dry.behavior.CookieOrderServiceBehavior;
import cookiefactory.it.citrus.dry.behavior.CookieProductionResultBehavior;
import cookiefactory.it.citrus.dry.behavior.FortuneServiceSimulation;
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
public class TestValidOrderWithFortuneService extends JUnit4CitrusTest {

    @Autowired
    private ConnectionFactory jmsConnectionFactory;

    @Test
    @CitrusTest
    public void testValidOrderWithFortuneService(@CitrusResource TestRunner runner) {
        runner.description("Send a valid order and make sure the correct number/flavour of cookies are produced. Additionally make sure that the request to the external fortune service is correct.");

        PurgeCookieQueues purgeQueues = new PurgeCookieQueues(jmsConnectionFactory);
        runner.applyBehavior(purgeQueues);

        CookieOrderServiceBehavior callOrderService = new CookieOrderServiceBehavior("httpClient");
        runner.applyBehavior(
                callOrderService.withRequestPayload("{ \"flavour\":\"fortune\", \"quantity\":42 }")
                        .assertResponseWith("Thank you for your order!")
                        .assertResponseHeader("citrus_http_status_code", 200)
        );

        FortuneServiceSimulation simulateFoturneService = new FortuneServiceSimulation("fortuneServer");
        runner.applyBehavior(simulateFoturneService
                .whenRecieving("")
                .answerWith("{ \"fortune\": \"Fortune simulation.\" }")
                .setResponseHeader("citrus_http_status_code", 200)
        );

        CookieProductionResultBehavior receiveProducedCookies = new CookieProductionResultBehavior("outputQueue");
        runner.applyBehavior(receiveProducedCookies
                .assertPayloadWith("")
                .assertHeader("flavour", "fortune")
                .assertHeader("quantity", 42)
        );

    }
}
