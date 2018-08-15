package cookiefactory.it.citrus.dry.configuration;

import com.consol.citrus.dsl.endpoint.CitrusEndpoints;
import com.consol.citrus.jms.endpoint.JmsEndpoint;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.ConnectionFactory;

@Configuration
public class JmsConfiguration {
    @Value("${activemq.brokerUrl}")
    private String brokerUrl;
    @Value("${endpoint.jms.queue.output}")
    private String jmsEndpointOutput;


    @Bean
    public ConnectionFactory connectionFactory() throws Exception {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        return connectionFactory;
    }

    @Bean
    public JmsEndpoint outputQueue() throws Exception {
        return CitrusEndpoints.jms()
                .asynchronous()
                .connectionFactory(connectionFactory())
                .destination(jmsEndpointOutput)
                .timeout(5000)
                .build();
    }


}
