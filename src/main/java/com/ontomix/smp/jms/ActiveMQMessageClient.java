package com.ontomix.smp.jms;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.concurrent.CountDownLatch;

/**
 * Implementation of IMessageClient for consuming messages from ActiveMQ
 */
public class ActiveMQMessageClient implements IMessageClient {

    private String brokerUrl;
    private String username;
    private String password;

    public ActiveMQMessageClient(String brokerUrl, String username, String password) {
        this.brokerUrl = brokerUrl;
        this.username = username;
        this.password = password;
    }

    @Override
    public void receiveMessages() throws JMSException, InterruptedException {

        Connection connection = null;
        Session session = null;

        CountDownLatch latch = new CountDownLatch(1);

        try {

            // Create a ConnectionFactory
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(username, password,
                    brokerUrl);

            // Create a Connection
            connection = connectionFactory.createConnection();

            // Create a Session
            session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);

            // Create the Queue
            Queue queue = session.createQueue(MessageQueueConstants.SALES_QUEUE);

            // Create a MessageConsumer
            MessageConsumer consumer = session.createConsumer(queue);

            // Create a SalesMessageListener
            javax.jms.MessageListener messageListener = new SalesMessageListener(latch);

            // Register the SalesMessageListener
            consumer.setMessageListener(messageListener);

            // Start the Connection
            connection.start();

            System.out.println("App started and is waiting for messages...");

            // Wait for message processing complete
            latch.await();

        } finally {
            if (session != null) {
                session.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }
}
