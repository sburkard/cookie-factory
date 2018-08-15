package cookiefactory.it.citrus;

import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.config.CitrusSpringConfig;
import com.consol.citrus.dsl.junit.JUnit4CitrusTest;
import com.consol.citrus.dsl.runner.TestRunner;
import com.consol.citrus.message.MessageType;
import cookiefactory.it.citrus.configuration.HttpConfiguration;
import cookiefactory.it.citrus.configuration.JmsConfiguration;
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

        runner.purgeQueues(action -> action.connectionFactory(jmsConnectionFactory)
                .queue("cookiefactory.production")
                .queue("cookiefactory.result")
        );
        runner.send(action -> action.endpoint("httpClient")
                .name("sendOrderRequest")
                .messageType(MessageType.JSON)
                .payload("{ \"flavour\":\"fortune\", \"quantity\":42 }")
        );
        runner.receive(action -> action.endpoint("httpClient")
                .name("sendOrderResponse")
                .messageType(MessageType.PLAINTEXT)
                .payload("Thank you for your order!")
                .header("citrus_http_status_code", 200)
        );
        runner.receive(action -> action.endpoint("fortuneServer")
                .name("fortuneRequest")
                .messageType(MessageType.PLAINTEXT)
                .payload("")
        );
        runner.send(action -> action.endpoint("fortuneServer")
                .name("fortuneResponse")
                .messageType(MessageType.JSON)
                .payload("{ \"fortune\": \"Fortune simulation.\" }")
                .header("citrus_http_status_code", 200)
        );
        runner.receive(action -> action.endpoint("outputQueue")
                .name("doneMessage")
                .messageType(MessageType.PLAINTEXT)
                .payload("")
                .header("flavour", "fortune")
                .header("quantity", "42")
        );

    }
}
