package be.ucll.java.ent.jms.config;

import be.ucll.java.ent.jms.JMSReceiver;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableJms
public class ReceiverConfig {

    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;

    @Bean
    public ActiveMQConnectionFactory receiverActiveMQConnectionFactory() {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL(brokerUrl);

        RedeliveryPolicy policy = activeMQConnectionFactory.getRedeliveryPolicy();
        policy.setRedeliveryDelay(TimeUnit.SECONDS.toMillis(20));
        policy.setMaximumRedeliveries(3);

        return activeMQConnectionFactory;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory =  new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(receiverActiveMQConnectionFactory());
        // For QUEUES set PubSub domain to false (default)
        factory.setPubSubDomain(false);

        return factory;
    }

    @Bean
    public JMSReceiver queueReceiver() {
        return new JMSReceiver();
    }

}


