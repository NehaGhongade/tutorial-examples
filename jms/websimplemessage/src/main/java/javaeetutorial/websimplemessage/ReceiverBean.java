package javaeetutorial.websimplemessage;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSRuntimeException;
import javax.jms.Queue;

@Named
@RequestScoped
public class ReceiverBean {

    static final Logger logger = Logger.getLogger("ReceiverBean");

    @Resource(lookup = "jms/MyWMQConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "jms/MyWMQQueue")
    private Queue queue;

    public ReceiverBean() {
    }

    public void getMessage() {
        try (JMSContext context = connectionFactory.createContext()) {
            JMSConsumer receiver = context.createConsumer(queue);
            String text = receiver.receiveBody(String.class, 1000);

            if (text != null) {
                FacesMessage facesMessage =
                    new FacesMessage("Reading message: " + text);
                FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            } else {
                FacesMessage facesMessage =
                    new FacesMessage("No message received after 1 second");
                FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            }
        } catch (JMSRuntimeException t) {
            logger.log(Level.SEVERE,
                "ReceiverBean.getMessage: Exception: {0}",
                t.toString());
        }
    }
}

