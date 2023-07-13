package cookiefactory;

import org.apache.activemq.junit.EmbeddedActiveMQBroker;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest
@EnableJms
@ActiveProfiles("test")
public class CookieFactoryTest {
    private static final String DIRECT_ORDER = "direct:order";
    private static final String MOCK_OUTPUT = "mock:output";

    @Rule
    public EmbeddedActiveMQBroker broker = new EmbeddedActiveMQBroker();
    @Autowired
    private CamelContext camelContext;

    @EndpointInject(uri = DIRECT_ORDER)
    private ProducerTemplate producer;
    @EndpointInject(uri = MOCK_OUTPUT)
    private MockEndpoint outputMock;


    @Test
    public void testValidOrder() throws Exception {
        outputMock.expectedMessageCount(1);
        outputMock.expectedHeaderReceived(CookieFactory.HEADER_NAME_FLAVOUR, CookieFactory.FLAVOUR_FORTUNE);
        outputMock.expectedHeaderReceived(CookieFactory.HEADER_NAME_QUANTITY, 42);

        producer.sendBody("{ \"flavour\":\"fortune\", \"quantity\":42 }");

        outputMock.assertIsSatisfied();
    }

    @Test
    public void testInvalidOrder() throws Exception {
        outputMock.expectedMessageCount(0);

        String response = (String) producer.requestBody("{ \"flavour\":\"fortune\" }");

        outputMock.setAssertPeriod(2000);
        outputMock.assertIsSatisfied();
        assertThat(response).contains("your order is invalid");
    }

    @Before
    public void setup() throws Exception {
        camelContext.getRouteDefinition(CookieFactory.ROUTE_INPUT_HTTP_ADAPTER_ID)
            .adviceWith(camelContext, new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        replaceFromWith(DIRECT_ORDER);
                    }
            });
        camelContext.getRouteDefinition(CookieFactory.ROUTE_FORTUNE_ID)
                .adviceWith(camelContext, new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        interceptSendToEndpoint("http4:*")
                                .skipSendToOriginalEndpoint().setBody(constant("{ \"fortune\": \"Fortune mock.\" }"));
                    }
                });
        camelContext.getRouteDefinition(CookieFactory.ROUTE_OUTPUT_JMS_ADAPTER_ID)
                .adviceWith(camelContext, new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        interceptSendToEndpoint(CookieFactory.ROUTE_OUTPUT_JMS_ADAPTER_OUTPUT_URI)
                                .skipSendToOriginalEndpoint().to(MOCK_OUTPUT);
                    }
                });

        MockEndpoint.resetMocks(camelContext);
    }

}
