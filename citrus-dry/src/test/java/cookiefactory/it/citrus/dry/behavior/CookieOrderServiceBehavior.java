package cookiefactory.it.citrus.dry.behavior;

import com.consol.citrus.dsl.builder.ReceiveMessageBuilder;
import com.consol.citrus.dsl.runner.AbstractTestBehavior;
import com.consol.citrus.message.MessageType;

import java.util.HashMap;
import java.util.Map;

public class CookieOrderServiceBehavior extends AbstractTestBehavior {
    private String httpClient;
    private String requestPayload;
    private String responsePayload;
    private Map<String, Object> expectedHeaders;
    private String requestMessageStoreName = "cookie-order-request";
    private String responseMessageStoreName = "cookie-order-response";

    public CookieOrderServiceBehavior(String httpClient) {
        this.httpClient = httpClient;
        this.expectedHeaders = new HashMap<>();
    }

    @Override
    public void apply() {
        send(action -> action.endpoint(httpClient)
                .name(requestMessageStoreName)
                .messageType(MessageType.JSON)
                .payload(requestPayload)
        );

        receive(action -> {
            ReceiveMessageBuilder endpoint = action.endpoint(httpClient)
                .name(responseMessageStoreName)
                .messageType(MessageType.PLAINTEXT)
                .payload(responsePayload);

            for (Map.Entry<String, Object> header : expectedHeaders.entrySet()) {
                endpoint.header(header.getKey(), header.getValue());
            }
        });
    }

    public CookieOrderServiceBehavior withRequestPayload(String payload) {
        this.requestPayload = payload;
        return this;
    }

    public CookieOrderServiceBehavior assertResponseWith(String payload) {
        this.responsePayload = payload;
        return this;
    }

    public CookieOrderServiceBehavior assertResponseHeader(String headerName, Object headerValue) {
        this.expectedHeaders.put(headerName, headerValue);
        return this;
    }

    public CookieOrderServiceBehavior storeRequestMessageAs(String requestMessageName) {
        this.requestMessageStoreName = requestMessageName;
        return this;
    }

    public CookieOrderServiceBehavior storeResponseMessageAs(String responseMessageName) {
        this.responseMessageStoreName = responseMessageName;
        return this;
    }
}
