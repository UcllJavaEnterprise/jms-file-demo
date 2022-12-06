package be.ucll.java.ent.jms;

import be.ucll.java.ent.domain.ChatMessageDTO;
import be.ucll.java.ent.soap.model.v1.ChatRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.StringWriter;

public class JMSSender {
    private static final Logger LOG = LoggerFactory.getLogger(JMSSender.class);

    @Value("${jms.queue-name}")
    private String queueName;
    @Autowired
    private JmsTemplate jmsTemplate;
    public void sendOnQueue(ChatMessageDTO msg) {
        try {
            JAXBContext contextObj = JAXBContext.newInstance(ChatRequest.class);
            Marshaller marshaller = contextObj.createMarshaller();

            ChatRequest req = new ChatRequest();
            req.setSender(msg.getSender());
            req.setMessage(msg.getMessage());

            StringWriter sw = new StringWriter();
            marshaller.marshal(req, sw);

            jmsTemplate.convertAndSend(queueName, sw.toString());
            LOG.info("Message sent: " + sw);
        } catch (Exception e) {
            LOG.error("JMS Sending message on Queue error", e);
        }
    }

}