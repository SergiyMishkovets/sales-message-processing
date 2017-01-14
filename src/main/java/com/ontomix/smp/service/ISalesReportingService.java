package com.ontomix.smp.service;

import com.ontomix.smp.model.Sale;

import java.util.List;

/**
 * A Sale Reporting Service Interface
 */
public interface ISalesReportingService {

    /**
     * Report the number of sales of each product and their total value
     * @param sales
     */
    void reportSales(List<Sale> sales);

    /**
     * Report the adjustments that have been made to each sale type
     * @param sales
     */
    void reportAdjustments(List<Sale> sales);
}
