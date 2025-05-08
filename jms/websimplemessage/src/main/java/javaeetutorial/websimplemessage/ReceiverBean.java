package javaeetutorial.websimplemessage;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@Named("receiverBean")
@RequestScoped
public class ReceiverBean {

    static final Logger logger = Logger.getLogger("ReceiverBean");

    private ConnectionFactory connectionFactory;
    private Queue queue;

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

    public void getMessage() {
        Connection connection = null;
        Session session = null;

        try {
           
           
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageConsumer consumer = session.createConsumer(queue);
            connection.start();

            Message message = consumer.receive(1000); // 1 second timeout
            if (message instanceof TextMessage) {
                String text = ((TextMessage) message).getText();
                FacesMessage facesMessage =
                    new FacesMessage("Reading message: " + text);
                FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            } else {
                FacesMessage facesMessage =
                    new FacesMessage("No message received after 1 second");
                FacesContext.getCurrentInstance().addMessage(null, facesMessage);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, "ReceiverBean.getMessage: Exception", e);
        } finally {
            try {
                if (session != null) session.close();
                if (connection != null) connection.close();
            } catch (JMSException ignored) {}
        }
    }
}
