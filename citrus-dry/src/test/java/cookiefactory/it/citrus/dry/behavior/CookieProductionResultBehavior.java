package cookiefactory.it.citrus.dry.behavior;

import com.consol.citrus.dsl.builder.ReceiveMessageBuilder;
import com.consol.citrus.dsl.runner.AbstractTestBehavior;
import com.consol.citrus.message.MessageType;

import java.util.HashMap;
import java.util.Map;

public class CookieProductionResultBehavior extends AbstractTestBehavior {
    private String queueName;
    private String expectedPayload;
    private Map<String, Object> expectedHeaders;
    private String messageStoreName = "cookie-production-result";

    public CookieProductionResultBehavior(String queueName) {
        this.queueName = queueName;
        this.expectedHeaders = new HashMap<>();
    }

    @Override
    public void apply() {
        receive(action -> {
            ReceiveMessageBuilder endpoint = action.endpoint(queueName)
                .name(messageStoreName)
                .messageType(MessageType.PLAINTEXT)
                .payload(expectedPayload);

            for (Map.Entry<String, Object> header : expectedHeaders.entrySet()) {
                endpoint.header(header.getKey(), header.getValue());
            }
        });
    }

    public CookieProductionResultBehavior assertPayloadWith(String expectedPayload) {
        this.expectedPayload = expectedPayload;
        return this;
    }
    public CookieProductionResultBehavior assertHeader(String headerName, Object headerValue) {
        this.expectedHeaders.put(headerName, headerValue);
        return this;
    }
    public CookieProductionResultBehavior storemessageAs(String messageName) {
        this.messageStoreName = messageName;
        return this;
    }

}
