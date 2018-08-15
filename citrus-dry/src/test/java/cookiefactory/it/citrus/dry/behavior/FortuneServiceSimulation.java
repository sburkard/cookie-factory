package cookiefactory.it.citrus.dry.behavior;

import com.consol.citrus.dsl.builder.SendMessageBuilder;
import com.consol.citrus.dsl.runner.AbstractTestBehavior;
import com.consol.citrus.message.MessageType;

import java.util.HashMap;
import java.util.Map;

public class FortuneServiceSimulation extends AbstractTestBehavior {
    private String fortuneService;
    private String requestPayload;
    private String responsePayload;
    private Map<String, Object> responseHeaders;
    private String requestMessageStoreName = "fortune-request";
    private String responseMessageStoreName = "fortune-response";

    public FortuneServiceSimulation(String fortuneService) {
        this.fortuneService = fortuneService;
        this.responseHeaders = new HashMap<>();
    }

    @Override
    public void apply() {
        receive(action -> action.endpoint(fortuneService)
                .name(requestMessageStoreName)
                .messageType(MessageType.PLAINTEXT)
                .payload(requestPayload)
        );

        send(action -> {
            SendMessageBuilder endpoint = action.endpoint(fortuneService)
                .name(responseMessageStoreName)
                .messageType(MessageType.JSON)
                .payload(responsePayload);

            for (Map.Entry<String, Object> header : responseHeaders.entrySet()) {
                endpoint.header(header.getKey(), header.getValue());
            }
        });
    }

    public FortuneServiceSimulation whenRecieving(String payload) {
        this.requestPayload = payload;
        return this;
    }

    public FortuneServiceSimulation answerWith(String payload) {
        this.responsePayload = payload;
        return this;
    }

    public FortuneServiceSimulation setResponseHeader(String headerName, Object headerValue) {
        this.responseHeaders.put(headerName, headerValue);
        return this;
    }

    public FortuneServiceSimulation storeRequestMessageAs(String requestMessageName) {
        this.requestMessageStoreName = requestMessageName;
        return this;
    }

    public FortuneServiceSimulation storeResponseMessageAs(String responseMessageName) {
        this.responseMessageStoreName = responseMessageName;
        return this;
    }
}
