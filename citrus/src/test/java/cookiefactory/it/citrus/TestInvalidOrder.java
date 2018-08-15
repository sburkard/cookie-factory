package cookiefactory.it.citrus;

import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.annotations.CitrusTest;
import com.consol.citrus.config.CitrusSpringConfig;
import com.consol.citrus.dsl.junit.JUnit4CitrusTest;
import com.consol.citrus.dsl.runner.TestRunner;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.message.MessageType;
import cookiefactory.it.citrus.configuration.HttpConfiguration;
import cookiefactory.it.citrus.configuration.SchemaConfiguration;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {CitrusSpringConfig.class, HttpConfiguration.class, SchemaConfiguration.class })
@DirtiesContext
public class TestInvalidOrder extends JUnit4CitrusTest {

    @Autowired
    HttpClient httpClient;

    @Test
    @CitrusTest
    public void testInvalidOrder(@CitrusResource TestRunner runner) {
        runner.description("Send an invalid order and make sure no cookies are produced");

        runner.send(action -> action.endpoint("httpClient")
                .name("sendOrderRequest")
                .messageType(MessageType.JSON)
                .payload("{ \"flavour\":\"notExistingFlavour\", \"quantity\":42 }")
        );
        runner.receive(action -> action.endpoint("httpClient")
                .name("sendOrderResponse")
                .messageType(MessageType.PLAINTEXT)
                .payload("Sorry, your order is invalid! \n@ignore(100)@")
                .header("citrus_http_status_code", 400)
        );
    }
}
