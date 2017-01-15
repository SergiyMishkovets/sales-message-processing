package com.ontomix.smp.service;

import com.ontomix.smp.model.Sale;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.util.*;
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

    private ISalesPersistenceService persistenceService;
    private ISalesReportingService reportingService;
    private List<Sale> reportSales = new ArrayList<>();
    private List<Sale> reportAdjustments = new ArrayList<>();

    public SalesMessageProcessingService(CountDownLatch latch) {
        this.latch = latch;
        this.persistenceService = new SalesPersistenceService();
        this.reportingService = new SalesReportingService();
    }

    public SalesMessageProcessingService(CountDownLatch latch, ISalesPersistenceService persistenceService, ISalesReportingService reportingService) {
        this.latch = latch;
        this.persistenceService = persistenceService;
        this.reportingService = reportingService;
    }

    @Override
    public Sale unmarshalSaleMessage(String msgText) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Sale.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        StringBuffer msgTextStrBuf = new StringBuffer(msgText);
        return (Sale) jaxbUnmarshaller.unmarshal(new StreamSource(new StringReader(msgText)));
    }

    @Override
    public void processSaleRecord(Sale sale) {
        // Store the Sale record
        String recordId = persistenceService.save(sale);

        // Logging
        System.out.println("Sale recorded - Record Id: " + recordId + ", Sale: " + sale);

        // Hold record to report sales
        reportSales.add(sale);

        // Hold record to report adjustments
        reportAdjustments.add(sale);

        int reportSalesSize = reportSales.size();
        int reportAdjustmentsSize = reportAdjustments.size();

        /*
         * After every 10th message received, log a report detailing the
         * number of sales of each product and their total value.
         */
        if (reportSalesSize == 10) {
            reportingService.reportSales(reportSales);
            reportSales.clear();
        }

        /*
         * After 50 messages, log that it is pausing, stop accepting new
         * messages and log a report of the adjustments that have been
         * made to each sale type while the application was running.
         */
        if (reportAdjustmentsSize >= 50) {
            reportingService.reportAdjustments(reportAdjustments);
            reportAdjustments.clear();

            if (latch != null) {
                // Logging
                System.out.println("App is pausing...");
                System.out.println("App is Stopping...");
                latch.countDown();
            }
        }
    }

}
