package cookiefactory.it.citrus.cucumber.dry.configuration;

import com.consol.citrus.dsl.endpoint.CitrusEndpoints;
import com.consol.citrus.http.server.HttpServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
public class HttpConfiguration {
    @Value("${endpoint.http.order}")
    private String httpEndpointOrder;

    @Bean
    public com.consol.citrus.http.client.HttpClient httpClient() {
        return CitrusEndpoints.http()
                .client()
                .requestUrl(httpEndpointOrder)
                .requestMethod(HttpMethod.POST)
                .charset("UTF-8")
                .build();
    }

    @Bean
    public HttpServer fortuneServer() throws Exception {
        return CitrusEndpoints.http()
                .server()
                .port(8088)
                .timeout(3000)
                .autoStart(true)
                .build();
    }
}
