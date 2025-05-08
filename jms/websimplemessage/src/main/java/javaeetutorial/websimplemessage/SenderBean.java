package javaeetutorial.websimplemessage;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@Named("senderBean")
@RequestScoped
public class SenderBean {

    static final Logger logger = Logger.getLogger("SenderBean");

    private ConnectionFactory connectionFactory;
    private Queue queue;
    private String messageText;

    @PostConstruct
    public void init() {
        try {
            Context ctx = new InitialContext();
            //connectionFactory = (ConnectionFactory) ctx.lookup("jms/ConnectionFactory");
            //queue = (Queue) ctx.lookup("jms/Queue");

            connectionFactory = (ConnectionFactory) ctx.lookup("java:comp/env/jms/ConnectionFactory");
            queue = (Queue) ctx.lookup("java:comp/env/jms/MWQueue");
        } catch (NamingException e) {
            logger.log(Level.SEVERE, "JNDI lookup failed", e);
        }
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public void sendMessage() {
        Connection connection = null;
        Session session = null;

        try {
            
            
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(queue);
            connection.start();
            String text = "Message from producer: " + messageText;
            TextMessage message = session.createTextMessage(text);
            producer.send(message);

            FacesMessage facesMessage =
                new FacesMessage("Sent message: " + text);
            FacesContext.getCurrentInstance().addMessage(null, facesMessage);

        } catch (JMSException e) {
            logger.log(Level.SEVERE, "SenderBean.sendMessage: Exception", e);
        } finally {
            try {
                if (session != null) session.close();
                if (connection != null) connection.close();
            } catch (JMSException ignored) {}
        }
    }
}
