package com.ontomix.smp.service;

import com.ontomix.smp.model.Sale;

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
 * <p>
 * <br>1. Unmarshal XML sale message to sale object
 * <br>2. Store sale record
 * <br>3. Call SalesReportingService to do reporting
 */
public class SalesMessageProcessingService implements ISalesMessageProcessingService {

    private final CountDownLatch latch;
    private final List<Sale> sales = new ArrayList<>();

    private ISalesReportingService reportingService;

    public SalesMessageProcessingService() {
        super();
        this.latch = new CountDownLatch(1);
    }

    public SalesMessageProcessingService(CountDownLatch latch) {
        this.latch = latch;
        this.reportingService = new SalesReportingService();
    }

    @Override
    public Sale unmarshalSaleMessage(String msgText) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Sale.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        StringBuffer msgTextStrBuf = new StringBuffer(msgText);
        return (Sale) jaxbUnmarshaller.unmarshal(new StreamSource(new StringReader(msgText)));
    }

    @Override
    public void storeSaleRecord(Sale sale) {
        // Store sale record
        sales.add(sale);

        int size = sales.size();

        /*
         * After every 10th message received, log a report detailing the
         * number of sales of each product and their total value.
         */
        if (size % 10 == 0 && size != 0) {
            int fromIdx = 0;
            int toIdx = size - 1;
            if (size > 10) {
                fromIdx = size - 1;
            }
            reportingService.reportSales(sales.subList(fromIdx, toIdx));
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
