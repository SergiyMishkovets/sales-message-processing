package com.ontomix.smp.jms;

import com.ontomix.smp.model.Sale;
import com.ontomix.smp.service.ISalesMessageProcessingService;
import com.ontomix.smp.service.SalesMessageProcessingService;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBException;
import java.util.concurrent.CountDownLatch;

/**
 * Implementation of MessageListener
 *
 */
class SalesMessageListener implements javax.jms.MessageListener {

    private final ISalesMessageProcessingService salesMessageProcessingService;

    public SalesMessageListener(CountDownLatch latch) {
        this.salesMessageProcessingService = new SalesMessageProcessingService(latch);
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            try {
                // Get the message type
                String msgType = textMessage.getJMSType();

                // Get the message text
                String msgText = textMessage.getText();

                // Logging
                System.out.println(msgType + " received: " + msgText);

                // Unmarshal to a Sale object
                Sale sale = salesMessageProcessingService.unmarshalSaleMessage(msgText);

                // Process the Sale
                salesMessageProcessingService.processSaleRecord(sale);
            } catch (JMSException | JAXBException e) {
                System.out.println("Caught " + e);
                e.printStackTrace();
            }
        }

    }
}
