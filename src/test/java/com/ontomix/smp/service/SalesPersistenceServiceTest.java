package com.ontomix.smp.service;

import com.ontomix.smp.model.Sale;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * Unit Test for SalesPersistenceService
 */
public class SalesPersistenceServiceTest {
    @Test
    public void testSaveAndFind() throws Exception {

        Sale sale = new Sale();
        sale.setProduct("Product");
        sale.setValue(new BigDecimal(10.00));

        ISalesPersistenceService persistenceService = new SalesPersistenceService();
        String recordId = persistenceService.save(sale);

        Assert.assertNotNull(recordId);

        Sale saleRecord = persistenceService.find(recordId);

        Assert.assertNotNull(saleRecord);
        Assert.assertEquals(saleRecord.getProduct(), sale.getProduct());
        Assert.assertEquals(saleRecord.getValue(), sale.getValue());
    }

}