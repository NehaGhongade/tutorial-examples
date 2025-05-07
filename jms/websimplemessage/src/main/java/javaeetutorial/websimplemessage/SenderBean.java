package javaeetutorial.websimplemessage;

import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSRuntimeException;
import javax.jms.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@RequestScoped
public class SenderBean {

    static final Logger logger = Logger.getLogger("SenderBean");

    @Resource(lookup = "jms/MyWMQConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "jms/MyWMQQueue")
    private Queue queue;

    private String messageText;

    public SenderBean() {
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public void sendMessage() {
        try (JMSContext context = connectionFactory.createContext()) {
            String text = "Message from producer: " + messageText;
            context.createProducer().send(queue, text);

            FacesMessage facesMessage =
                new FacesMessage("Sent message: " + text);
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);

        } catch (JMSRuntimeException e) {
            logger.log(Level.SEVERE, "SenderBean.sendMessage: Exception: {0}", e.toString());
        }
    }
}

