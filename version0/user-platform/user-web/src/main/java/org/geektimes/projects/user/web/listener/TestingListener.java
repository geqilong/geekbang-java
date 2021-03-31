package org.geektimes.projects.user.web.listener;

import org.geektimes.projects.context.ClassicComponentContext;
import org.geektimes.projects.function.ThrowableAction;
import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.management.UserManager;
import org.geektimes.projects.user.sql.DBConnectionManager;

import javax.jms.*;
import javax.management.*;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.lang.management.ManagementFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestingListener implements ServletContextListener {
    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ClassicComponentContext context = ClassicComponentContext.getInstance();
        DBConnectionManager dbConnectionManager = context.getComponent("bean/DBConnectionManager");
        logger.info("DB Connection:" + dbConnectionManager.createConnection());
        logger.info("所有的 JNDI 组件名称：[");
        context.getComponentNames().forEach(logger::info);
        logger.info("]");
        registerMBean();
        ConnectionFactory connectionFactory = context.getComponent("jms/activemq-factory");
        testJms(connectionFactory);
    }

    private void testJms(ConnectionFactory connectionFactory) {
        ThrowableAction.execute(() -> {
            testMessageProducer(connectionFactory);
            testMessageConsumer(connectionFactory);
        });
    }

    private void testMessageProducer(ConnectionFactory connectionFactory) throws JMSException {
        // Create a Connection
        Connection connection = connectionFactory.createConnection();
        connection.start();
        //Create a Session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //Create the destination(Topic or Queue)
        Destination destination = session.createQueue("TEST.FOO");
        //Create a MessageProducer from the Session to the Topic or Queue
        MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        //Create a message
        String text = "Hello World! From: " + Thread.currentThread().getName() + " : " + this.hashCode();
        TextMessage textMessage = session.createTextMessage(text);

        //Tell the producer to send the message
        producer.send(textMessage);
        //Clean up
        session.close();
        connection.close();
    }

    private void testMessageConsumer(ConnectionFactory connectionFactory) throws JMSException {
        // Create a Connection
        Connection connection = connectionFactory.createConnection();
        connection.start();
        //Create a Session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //Create the destination(Topic or Queue)
        Destination destination = session.createQueue("TEST.FOO");

        // Create a MessageConsumer from the Session to the Topic or Queue
        MessageConsumer consumer = session.createConsumer(destination);
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                TextMessage tm = (TextMessage) message;
                try {
                    System.out.printf("[Thread : %s] Received: %s\n", Thread.currentThread().getName(), tm.getText());
                } catch (JMSException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void registerMBean() {
        //获取MBean平台Server
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        ObjectName objectName = null;
        try {
            objectName = new ObjectName("org.geektimes.projects.user.management:type=User");
            mBeanServer.registerMBean(new UserManager(new User()), objectName);
        } catch (MalformedObjectNameException | NotCompliantMBeanException | InstanceAlreadyExistsException | MBeanRegistrationException e) {
            logger.log(Level.SEVERE, "Error registering MBean", e.getCause());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
