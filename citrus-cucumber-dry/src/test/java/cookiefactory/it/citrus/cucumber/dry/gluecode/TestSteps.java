package cookiefactory.it.citrus.cucumber.dry.gluecode;

import com.consol.citrus.annotations.CitrusResource;
import com.consol.citrus.config.CitrusSpringConfig;
import com.consol.citrus.dsl.runner.TestRunner;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.util.FileUtils;
import cookiefactory.it.citrus.cucumber.dry.configuration.HttpConfiguration;
import cookiefactory.it.citrus.cucumber.dry.configuration.JmsConfiguration;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;

import javax.jms.ConnectionFactory;
import java.io.IOException;

@ContextConfiguration(classes = {CitrusSpringConfig.class, HttpConfiguration.class, JmsConfiguration.class})
public class TestSteps {
    @CitrusResource
    private TestRunner runner;
    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Given("^All queues are empty$")
    public void purgeQueues() {
        runner.purgeQueues(action -> action.connectionFactory(connectionFactory)
                .queue("cookiefactory.production")
                .queue("cookiefactory.result")
        );
    }

    @When("^I send the order \"([^\"]*)\"$")
    public void sendCookieOrder(String orderPayloadFilename) {
        runner.send(action -> action.endpoint("httpClient")
                .name("invalidOrderRequest")
                .messageType(MessageType.JSON)
                .payload(new ClassPathResource("testdata/" + orderPayloadFilename + ".json"))
        );
    }

    @When("^I send an order for \"([0-9]+)\" \"([^\"]*)\" cookies$")
    public void sendCookieOrder(int quantity, String flavour) throws IOException {
        String jsonTemplate = FileUtils.readToString(new ClassPathResource("testdata/OrderTemplate.json"));
        final String jsonPayload = String.format(jsonTemplate, flavour, quantity);

        runner.send(action -> action.endpoint("httpClient")
                .name("invalidOrderRequest")
                .messageType(MessageType.JSON)
                .payload(jsonPayload)
        );
    }

    @Then("^I must receive the [R|r]esponse \"([^\"]*)\" with [R|r]esponse code \"([0-9]{3})\"$")
    public void receiveOrderFailure(String errorMessage, int responseCode) {
        runner.receive(action -> action.endpoint("httpClient")
                .name("invalidOrderResponse")
                .messageType(MessageType.PLAINTEXT)
                .payload(errorMessage)
                .header("citrus_http_status_code", responseCode)
        );
    }

    @Then("^\"([0-9]+)\" [C|c]ookies with \"([^\"]*)\" flavour must be produced$")
    public void receiveProducedCookies(int quantity, String flavour) {
        runner.receive(action -> action.endpoint("outputQueue")
                .name("doneMessage")
                .messageType(MessageType.PLAINTEXT)
                .payload("")
                .header("flavour", flavour)
                .header("quantity", quantity));
    }

    @Then("^A fortune must be generated$")
    public void simulateFortuneService() {
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
    }

}
