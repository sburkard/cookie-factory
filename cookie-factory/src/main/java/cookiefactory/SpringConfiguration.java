package cookiefactory;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.ConnectionFactory;

@Configuration
public class SpringConfiguration {
    @Bean(name = "activemq")
    public ActiveMQComponent createComponent(ConnectionFactory factory) {
        ActiveMQComponent activeMQComponent = new ActiveMQComponent();
        activeMQComponent.setConnectionFactory(factory);
        activeMQComponent.setLazyCreateTransactionManager(false);
        activeMQComponent.setTransacted(true);
        return activeMQComponent;
    }
}
