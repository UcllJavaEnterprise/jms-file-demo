package be.ucll.java.ent.jms.config;

import be.ucll.java.ent.jms.JMSSender;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

@Configuration
@EnableJms
public class SenderConfig {

    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;

    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        ActiveMQConnectionFactory activeMQConnectionFactory =  new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL(brokerUrl);

        return activeMQConnectionFactory;
    }

    @Bean
    public CachingConnectionFactory cachingConnectionFactory() {
        return new CachingConnectionFactory(activeMQConnectionFactory());
    }

    @Bean
    public JmsTemplate jmsTemplateForQueues() {
        JmsTemplate jmsTemplate =  new JmsTemplate(cachingConnectionFactory());

        // For QUEUES set PubSub domain to false (default)
        jmsTemplate.setPubSubDomain(false);

        return jmsTemplate;
    }

    @Bean
    public JMSSender sender() {
        return new JMSSender();
    }
}
