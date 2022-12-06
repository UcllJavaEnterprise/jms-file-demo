package be.ucll.java.ent.jms;

import be.ucll.java.ent.domain.ChatMessageDTO;
import be.ucll.java.ent.soap.model.v1.ChatRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;

import javax.jms.JMSException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

public class JMSReceiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(JMSReceiver.class);
    private static final Logger msgLogger = LoggerFactory.getLogger("messagelogger");
    private JAXBContext contextObj;
    private Unmarshaller unmarshaller;
    public JMSReceiver() {
        try {
            contextObj = JAXBContext.newInstance(ChatRequest.class);
            unmarshaller = contextObj.createUnmarshaller();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    @JmsListener(destination = "${jms.queue-name}")
    public void receiveFromQueue(String message) throws JMSException {
        LOGGER.info("Received queue message: '{}'", message);

        try {
            if (message.startsWith("<?xml version")){
                StringReader reader = new StringReader(message);
                ChatRequest cr = (ChatRequest) unmarshaller.unmarshal(reader);

                msgLogger.info("JMS | " + cr.getSender() + " | " + cr.getMessage());

                ChatMessageDTO cm = new ChatMessageDTO(cr.getMessage(), cr.getSender());
            }
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}
