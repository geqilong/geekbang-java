package org.geektimes.projects.user.message;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import java.util.Hashtable;

public class MessageProducerFactory implements ObjectFactory {
    private String queueName;
    private String connectionFactoryJndiName;

    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
        if (obj == null || !(obj instanceof Reference)) {
            return null;
        }
        Reference reference = (Reference) obj;
        String queueName = getAttribute(reference, "queueName");
        String connectionFactoryJndiName = getAttribute(reference, "connectionFactoryJndiName");
        //nameCtx发现同级目录的Context
        ConnectionFactory connectionFactory = (ConnectionFactory) nameCtx.lookup(connectionFactoryJndiName);
        Connection connection = connectionFactory.createConnection();
        connection.start();
        //create a session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //Create the destination(Topic or Queue)
        Destination destination = session.createQueue(queueName);
        //Create a MessageProducer from the Session to the Topic or Queue
        MessageProducer producer = session.createProducer(destination);
        return producer;
    }

    private String getAttribute(Reference reference, String attributeName) {
        RefAddr refAddr = reference.get(attributeName);
        return refAddr == null ? null : String.valueOf(refAddr.getContent());
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public void setConnectionFactoryJndiName(String connectionFactoryJndiName) {
        this.connectionFactoryJndiName = connectionFactoryJndiName;
    }
}
