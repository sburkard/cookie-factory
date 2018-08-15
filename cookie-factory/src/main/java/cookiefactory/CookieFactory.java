package cookiefactory;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jsonvalidator.JsonValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.ConnectException;

@Component
public class CookieFactory extends RouteBuilder {
    public static final String ROUTE_INPUT_HTTP_ADAPTER_ID = "inputHttp-route";
    public static final String ROUTE_INPUT_HTTP_ADAPTER_ENDPOINT_URI = "jetty:http://0.0.0.0:8080/cookiefactory/order";

    public static final String ROUTE_VALIDATION_ID = "validateOrder-route";
    public static final String ROUTE_VALIDATION_ENDPOINT_URI = "direct:validateOrder";

    public static final String ROUTE_PRODUCTION_ID = "produceCookie-route";
    public static final String ROUTE_PRODUCTION_ENDPOINT_URI = "activemq:queue:cookiefactory.production";

    public static final String ROUTE_FORTUNE_ID = "getFortune-route";
    public static final String ROUTE_FORTUNE_ENDPOINT_URI = "direct:getFortune";

    public static final String ROUTE_OUTPUT_JMS_ADAPTER_ID = "outputJms-route";
    public static final String ROUTE_OUTPUT_JMS_ADAPTER_ENDPOINT_URI = "direct:deliverCookieJms";
    public static final String ROUTE_OUTPUT_JMS_ADAPTER_OUTPUT_URI = "activemq:queue:cookiefactory.result";

    public static final String HEADER_NAME_FLAVOUR = "flavour";
    public static final String HEADER_NAME_QUANTITY = "quantity";

    public static final String FLAVOUR_VANILLA = "vanilla";
    public static final String FLAVOUR_CHOCO = "chocolate";
    public static final String FLAVOUR_FORTUNE = "fortune";

    @Value("${cookiefactory.fortuneservice.url}")
    private String fortuneserviceUrl;

    @Override
    public void configure() throws Exception {
        onException(ConnectException.class)
            .handled(true)
            .log(LoggingLevel.ERROR, "Ups, order lost because backend is not reachable");

        from(ROUTE_INPUT_HTTP_ADAPTER_ENDPOINT_URI)
                .routeId(ROUTE_INPUT_HTTP_ADAPTER_ID)
                .convertBodyTo(String.class)
                .log("*** Order received: ${body}")
                .to(ROUTE_VALIDATION_ENDPOINT_URI);

        from(ROUTE_VALIDATION_ENDPOINT_URI)
                .routeId(ROUTE_VALIDATION_ID)
                .log("Validate Order")
                .setHeader(Exchange.CONTENT_TYPE, constant("text/plain"))
                .setHeader(Exchange.CONTENT_ENCODING, constant("utf-8"))
                .doTry()
                    .to("json-validator:order-schema.json")
                    .setHeader(HEADER_NAME_FLAVOUR, jsonpath("$.flavour"))
                    .setHeader(HEADER_NAME_QUANTITY, jsonpath("$.quantity"))
                    .setBody(constant("Thank you for your order!"))
                .doCatch(JsonValidationException.class)
                    .log("*** Order is invalid. Abort processing")
                    .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(400))
                    .setBody(simple("Sorry, your order is invalid! \nError message: ${exception.message}"))
                    .stop()
                .end()
                .inOnly(ROUTE_PRODUCTION_ENDPOINT_URI);

        from(ROUTE_PRODUCTION_ENDPOINT_URI)
                .routeId(ROUTE_PRODUCTION_ID)
                .log("Take dough")
                .log("Model cookies")
                .choice()
                    .when(header(HEADER_NAME_FLAVOUR).isEqualTo(FLAVOUR_VANILLA))
                        .log("Add vanilla")
                    .when(header(HEADER_NAME_FLAVOUR).isEqualTo(FLAVOUR_CHOCO))
                        .log("Add chocolate")
                    .when(header(HEADER_NAME_FLAVOUR).isEqualTo(FLAVOUR_FORTUNE))
                        .to(ROUTE_FORTUNE_ENDPOINT_URI)
                        .log("Embed fortune")
                .end()
                .log("Bake cookies")
                .to(ROUTE_OUTPUT_JMS_ADAPTER_ENDPOINT_URI);

        from(ROUTE_FORTUNE_ENDPOINT_URI)
                .routeId(ROUTE_FORTUNE_ID)
                .log("Get fortune from China")
                .removeHeaders("CamelHttp*")
                .setHeader("CamelHttpMethod", constant("GET"))
                .to("http4://" + fortuneserviceUrl)
                .setHeader("fortune", jsonpath("$.fortune"))
                .log("Received fortune: ${in.header.fortune}");


        from(ROUTE_OUTPUT_JMS_ADAPTER_ENDPOINT_URI)
                .routeId(ROUTE_OUTPUT_JMS_ADAPTER_ID)
                .log("*** ${header.quantity} cookies of flavour ${header.flavour} are ready to deliver.")
                .to(ROUTE_OUTPUT_JMS_ADAPTER_OUTPUT_URI);
    }
}
