package com.ontomix.smp.service;

import com.ontomix.smp.model.Sale;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Implementation of ISalesMessageProcessingService
 *
 * <br>1. Unmarshall XML Sale Message to Sale Object
 * <br>2. Call SalesReportingService to do reporting
 *
 */
public class SalesMessageProcessingService implements ISalesMessageProcessingService {

    private final CountDownLatch latch;
    private final List<Sale> sales = new ArrayList<>();
    private final List<Sale> reportSales = new ArrayList<>();
    private final ISalesReportingService reportingService;

    public SalesMessageProcessingService(CountDownLatch latch) {
        this.latch = latch;
        this.reportingService = new SalesReportingService();
    }

    @Override
    public void process(TextMessage textMessage) throws JMSException, JAXBException {

        Sale sale = null;

        // Get the message type
        String msgType = textMessage.getJMSType();
        // Get the message text
        String msgText = textMessage.getText();

        // Logging
        System.out.println(msgType + " received: " + msgText);

        // Unmarshal the XML TextMessage to Sale object
        JAXBContext jaxbContext = JAXBContext.newInstance(Sale.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        StringBuffer msgTextStrBuf = new StringBuffer(msgText);
        sale = (Sale) jaxbUnmarshaller.unmarshal(new StreamSource(new StringReader(msgText)));

        sales.add(sale);
        reportSales.add(sale);

        int size = sales.size();

        /*
         * After every 10th message received, log a report detailing the
         * number of sales of each product and their total value.
         */
        if (size % 10 == 0 && size != 0) {
            reportingService.reportSales(reportSales);
            // Clears the report sales list for next round
            reportSales.clear();
        }

        /*
         * After 50 messages, log that it is pausing, stop accepting new
         * messages and log a report of the adjustments that have been
         * made to each sale type while the application was running.
         */
        if (size == 50) {
            reportingService.reportAdjustments(sales);
            // Logging
            System.out.println("App is pausing...");
            System.out.println("App is Stopping...");
            latch.countDown();
        }

    }
}
