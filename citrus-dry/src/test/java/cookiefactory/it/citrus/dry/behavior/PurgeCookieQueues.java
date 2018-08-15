package cookiefactory.it.citrus.dry.behavior;

import com.consol.citrus.dsl.runner.AbstractTestBehavior;

import javax.jms.ConnectionFactory;

/**
 * Custom TestBehaviour to purge all relevant queues before a test is executed.
 */
public class PurgeCookieQueues extends AbstractTestBehavior {
    private ConnectionFactory connectionFactory;

    public PurgeCookieQueues(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void apply() {
        purgeQueues(action -> action.connectionFactory(connectionFactory)
                .queue("cookiefactory.production")
                .queue("cookiefactory.result")
        );
    }

}
