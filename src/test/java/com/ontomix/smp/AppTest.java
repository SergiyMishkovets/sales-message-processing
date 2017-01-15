package com.ontomix.smp;

import com.ontomix.smp.jms.MessageQueueConstants;
import com.ontomix.smp.model.OperationType;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.jms.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Unit Test for Sales Message Processing App
 * <p>
 * <br>
 * This test case sends a set of randomly generated sale messages
 * to an embedded ActiveMQ message broker, and starts the app to process
 * the messages and log reports.
 */
public class AppTest {

    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin";

    private static BrokerService brokerService;

    @BeforeClass
    public static void setup() throws Exception {

        // Create a BrokerService
        brokerService = new BrokerService();
        brokerService.addConnector(BROKER_URL);
        brokerService.setBrokerName("embedded-broker");
        brokerService.setPersistent(false);
        brokerService.setUseJmx(false);
        brokerService.start();

        // Start App
        System.out.println("Starting App...");
        Runnable appRun = () -> {
            App app = new App();
            app.launch(new String[]{BROKER_URL, USERNAME, PASSWORD});
        };
        new Thread(appRun).start();

        // Wait for app started
        Thread.sleep(2000);
    }

    @AfterClass
    public static void finish() throws Exception {
        // Wait for testing completed
        Thread.sleep(4000);
        brokerService.stop();
    }

    @Test
    public void testMessageIsConsumedAndProcessed() throws Exception {
        Connection connection = null;
        try {
            // Producer
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
                    USERNAME, PASSWORD, BROKER_URL);
            connection = connectionFactory.createConnection();
            Session session = connection.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue(MessageQueueConstants.SALES_QUEUE);
            MessageProducer producer = session.createProducer(queue);

            for (int i = 0; i < 100; i++) {

                // Generate random massage
                Map<String, String> testMessages = getTestMessages();

                // Generate random message type and payload
                int idxMsgTypes = new Random().nextInt(testMessages.size());
                String randomMsgType = (String) testMessages.keySet().toArray()[idxMsgTypes];
                String randomMsgPayload = testMessages.get(randomMsgType);

                Message msg = session.createTextMessage(randomMsgPayload);
                msg.setJMSType(randomMsgType);
                System.out.println("Sending " + randomMsgType + " message: '" + randomMsgPayload + "'");
                producer.send(msg);
            }
            session.close();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    private Map<String, String> getTestMessages() {

        Map<String, String> testMessages = new HashMap<>();

        // Generate random product
        String[] products = {"Apple", "Mango", "Peach", "Banana", "Orange", "Grapes", "Watermelon", "Tomato"};
        int idxProducts = new Random().nextInt(products.length);
        String randomProduct = (products[idxProducts]);

        // Generate random value of each sale
        BigDecimal randBigDecimalSaleValue = new BigDecimal(Math.random());
        BigDecimal randomSaleValue = randBigDecimalSaleValue.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.FLOOR);

        // Generate random occurrences of sales
        int occurrences = new Random().nextInt(100);

        // Generate random adjustment operation
        OperationType[] OperationTypes = OperationType.values();
        int idxOperationType = new Random().nextInt(OperationTypes.length);
        OperationType randomOperationType = OperationTypes[idxOperationType];

        // Generate random value of each sale
        BigDecimal randBigDecimalAdjustValue = new BigDecimal(Math.random());
        BigDecimal randomAdjustValue = randBigDecimalAdjustValue.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.FLOOR);

        String type1Payload = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<Sale>" +
                "<Product>" + randomProduct + "</Product>" +
                "<Value>" + randomSaleValue + "</Value>" +
                "</Sale>";

        String type2Payload = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<Sale>" +
                "<Product>" + randomProduct + "</Product>" +
                "<Value>" + randomSaleValue + "</Value>" +
                "<Occurrences>" + occurrences + "</Occurrences>" +
                "</Sale>";

        String type3Payload = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<Sale>" +
                "<Product>" + randomProduct + "</Product>" +
                "<Value>" + randomSaleValue + "</Value>" +
                "<Adjustment>" +
                "<AdjustmentOperation>" + randomOperationType + "</AdjustmentOperation>" +
                "<AdjustmentValue>" + randomAdjustValue + "</AdjustmentValue>" +
                "</Adjustment>" +
                "</Sale>";

        testMessages.put("MsgType1", type1Payload);
        testMessages.put("MsgType2", type2Payload);
        testMessages.put("MsgType3", type3Payload);

        return testMessages;
    }

}
