package com.ontomix.smp.service;

import com.ontomix.smp.model.OperationType;
import com.ontomix.smp.model.Sale;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

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

        ISalesMessageProcessingService processingService = new SalesMessageProcessingService();
        Sale sale1 = processingService.unmarshalSaleMessage(type1Payload);
        Sale sale2 = processingService.unmarshalSaleMessage(type2Payload);
        Sale sale3 = processingService.unmarshalSaleMessage(type3Payload);

        Assert.assertNotNull(sale1);
        Assert.assertEquals(sale1.getProduct(), "APPLE");
        Assert.assertEquals(sale1.getValue(), new BigDecimal("30.00"));

        Assert.assertNotNull(sale2);
        Assert.assertNotNull(sale2.getOccurrences());
        Assert.assertEquals(sale2.getProduct(), "ORANGE");
        Assert.assertEquals(sale2.getValue(), new BigDecimal("10.00"));
        Assert.assertEquals(sale2.getOccurrences(), 5);

        Assert.assertNotNull(sale3);
        Assert.assertNotNull(sale3.getAdjustment());
        Assert.assertEquals(sale3.getProduct(), "PEAR");
        Assert.assertEquals(sale3.getValue(), new BigDecimal("50.00"));
        Assert.assertEquals(sale3.getAdjustment().getAdjustOperation(), OperationType.ADD);
        Assert.assertEquals(sale3.getAdjustment().getAdjustValue(), new BigDecimal("10.99"));
    }

    @Test
    public void storeSaleRecord() throws Exception {

    }

}