package org.geektimes.projects.user.web.listener;

import org.apache.activemq.command.ActiveMQTextMessage;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.MessageProducer;
import javax.jms.Topic;

public class TestingComponent {
    @Resource(name = "jms/activemq-topic")
    private Topic topic;

    @Resource(name = "jms/message-producer")
    private MessageProducer messageProducer;

    @PostConstruct
    public void init() {
        System.out.println("topic is " + topic);
    }

    @PostConstruct
    public void sendMessage() throws  Throwable {
        //Create a message
        String text = "Hello World! From :" + Thread.currentThread().getName() + ":" + this.hashCode();
        ActiveMQTextMessage message = new ActiveMQTextMessage();
        message.setText(text);

        //Tell the producer to send the message
        messageProducer.send(message);
        System.out.printf("[Thread : %s] Sent message : %s\n", Thread.currentThread().getName(), message.getText());
    }

}
