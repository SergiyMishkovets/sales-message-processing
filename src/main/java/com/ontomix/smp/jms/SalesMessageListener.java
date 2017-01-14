package com.ontomix.smp.jms;

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

    private final ISalesMessageProcessingService salesMessageProcessingServic;

    public SalesMessageListener(CountDownLatch latch) {
        this.salesMessageProcessingServic = new SalesMessageProcessingService(latch);
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            // Processing message
            try {
                this.salesMessageProcessingServic.process(textMessage);
            } catch (JMSException | JAXBException e) {
                System.out.println("Caught " + e);
                e.printStackTrace();
            }
        }

    }
}
