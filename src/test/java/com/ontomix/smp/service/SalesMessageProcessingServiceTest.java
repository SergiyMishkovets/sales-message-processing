package com.ontomix.smp.service;

import com.ontomix.smp.model.OperationType;
import com.ontomix.smp.model.Sale;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit Test for SalesMessageProcessingService
 */
public class SalesMessageProcessingServiceTest {
    @Test
    public void unmarshalSaleMessage() throws Exception {

        // Create test data
        String type1Payload = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<Sale>" +
                "<Product>Apple</Product>" +
                "<Value>30.00</Value>" +
                "</Sale>";

        String type2Payload = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<Sale>" +
                "<Product>Orange</Product>" +
                "<Value>10.00</Value>" +
                "<Occurrences>5</Occurrences>" +
                "</Sale>";

        String type3Payload = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<Sale>" +
                "<Product>Pear</Product>" +
                "<Value>50.00</Value>" +
                "<Adjustment>" +
                "<AdjustmentOperation>" + OperationType.ADD + "</AdjustmentOperation>" +
                "<AdjustmentValue>10.99</AdjustmentValue>" +
                "</Adjustment>" +
                "</Sale>";

        CountDownLatch latch = new CountDownLatch(1);
        ISalesMessageProcessingService processingService = new SalesMessageProcessingService(latch);

        Sale sale1 = processingService.unmarshalSaleMessage(type1Payload);
        Sale sale2 = processingService.unmarshalSaleMessage(type2Payload);
        Sale sale3 = processingService.unmarshalSaleMessage(type3Payload);

        Assert.assertNotNull(sale1);
        Assert.assertEquals("APPLE", sale1.getProduct());
        Assert.assertEquals(new BigDecimal("30.00"), sale1.getValue());

        Assert.assertNotNull(sale2);
        Assert.assertNotNull(sale2.getOccurrences());
        Assert.assertEquals("ORANGE", sale2.getProduct());
        Assert.assertEquals(new BigDecimal("10.00"), sale2.getValue());
        Assert.assertEquals(5, sale2.getOccurrences());

        Assert.assertNotNull(sale3);
        Assert.assertNotNull(sale3.getAdjustment());
        Assert.assertEquals("PEAR", sale3.getProduct());
        Assert.assertEquals(new BigDecimal("50.00"), sale3.getValue());
        Assert.assertEquals(OperationType.ADD, sale3.getAdjustment().getAdjustOperation());
        Assert.assertEquals(new BigDecimal("10.99"), sale3.getAdjustment().getAdjustValue());
    }

    @Test
    public void processSaleRecord() throws Exception {

        // Create SalesPersistenceService
        ISalesPersistenceService persistenceService = new SalesPersistenceService();
        // Mock SalesReportingService
        ISalesReportingService reportingService = mock(SalesReportingService.class);
        doNothing().when(reportingService).reportSales(any(ArrayList.class));
        doNothing().when(reportingService).reportAdjustments(any(ArrayList.class));

        ISalesMessageProcessingService processingService = new SalesMessageProcessingService(null, persistenceService, reportingService);

        /*
         * Store some sales and test that reportingService.reportSales() and
         * reportingService.reportAdjustments() are getting called
         */
        List<Sale> reportSales = new ArrayList<>();
        List<Sale> reportAdjustments = new ArrayList<>();
        int countReportSalesInvocation = 0;
        int countReportAdjustmentsInvocation = 0;
        for (int i = 0; i < 100; i++) {
            Sale sale = new Sale();
            sale.setProduct("Product" + i);
            sale.setValue(new BigDecimal(10.00).add(BigDecimal.valueOf(1.00)));

            // Hold for reporting
            reportSales.add(sale);
            reportAdjustments.add(sale);

            processingService.processSaleRecord(sale);

            int reportSalesSize = reportSales.size();
            int reportAdjustmentsSize = reportAdjustments.size();

            if (reportSalesSize == 10) {
                countReportSalesInvocation++;
                verify(reportingService, times(countReportSalesInvocation)).reportSales(any(ArrayList.class));
                reportSales.clear();
            } else {
                verify(reportingService, times(countReportSalesInvocation)).reportSales(any(ArrayList.class));
            }

            if (reportAdjustmentsSize >= 50) {
                countReportAdjustmentsInvocation++;
                verify(reportingService, times(countReportAdjustmentsInvocation)).reportAdjustments(any(ArrayList.class));
                reportAdjustments.clear();
            } else {
                verify(reportingService, times(countReportAdjustmentsInvocation)).reportAdjustments(any(ArrayList.class));
            }
        }
    }

}