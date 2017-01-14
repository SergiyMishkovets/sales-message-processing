package com.ontomix.smp.service;

import com.ontomix.smp.model.Sale;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementation of ISalesReportingService
 *
 * <br>1. Generating Sales Report
 * <br>2. Generating Adjustment Report
 *
 */
public class SalesReportingService implements ISalesReportingService {

    @Override
    public void reportSales(List<Sale> sales) {

        // Logging
        System.out.println("Reporting Sales...");

        // Create a mapper to compute total value of each sale
        Function<Sale, BigDecimal> subTotalMapper = sale -> sale.getValue().multiply(BigDecimal.valueOf(sale.getOccurrences()));

        // Group sales by product type
        Map<String, List<Sale>> productSales = sales.stream().collect(Collectors.groupingBy(Sale::getProduct));

        // Report computed total number and value of sales for each product
        productSales.forEach((k, v) ->
                System.out.println("Product: " + k + "; " +
                        "Number of Sales: " + v.stream().collect(Collectors.summingInt(Sale::getOccurrences)) + "; " +
                        "Total Value of Sales: " + v.stream().map(subTotalMapper).reduce(BigDecimal.ZERO, BigDecimal::add))
        );

    }

    @Override
    public void reportAdjustments(List<Sale> sales) {

        // Logging
        System.out.println("Reporting Adjustments...");

        List<Sale> saleRecords = new ArrayList<>();

        for (Sale sale : sales) {
            saleRecords.add(sale);
            if (sale.getAdjustment() != null) {

                // Log report
                System.out.println("Adjustment instruction received: "
                        + sale.getAdjustment().getAdjustOperation().toString().toLowerCase() + "ing "
                        + sale.getAdjustment().getAdjustValue() + " to each sale recorded for product " + sale.getProduct() + "...");

                // Create an adjustment mapper
                Function<Sale, BigDecimal> adjustmentMapper;

                switch (sale.getAdjustment().getAdjustOperation()) {
                    case ADD:
                        adjustmentMapper = s -> s.getValue().add(sale.getAdjustment().getAdjustValue()).multiply(BigDecimal.valueOf(s.getOccurrences()));
                        break;
                    case SUBSTRACT:
                        adjustmentMapper = s -> s.getValue().subtract(sale.getAdjustment().getAdjustValue()).multiply(BigDecimal.valueOf(s.getOccurrences()));
                        break;
                    case MULTIPLY:
                        adjustmentMapper = s -> s.getValue().multiply(sale.getAdjustment().getAdjustValue()).multiply(BigDecimal.valueOf(s.getOccurrences()));
                        break;
                    default:
                        adjustmentMapper = s -> s.getValue().multiply(BigDecimal.valueOf(s.getOccurrences()));
                }

                // Group recorded sales by product type
                Map<String, List<Sale>> tmpProductSales = saleRecords.stream().collect(Collectors.groupingBy(Sale::getProduct));

                // Apply adjustment to each product recorded and log report
                tmpProductSales.get(sale.getProduct()).forEach(s -> {

                            BigDecimal currentValue = s.getValue().multiply(BigDecimal.valueOf(s.getOccurrences()));

                            // Apply the adjustment mapper
                            BigDecimal adjustedValue = adjustmentMapper.apply(s);

                            System.out.println("Product: " + s.getProduct() + "; " +
                                    "Number of Sales: " + s.getOccurrences() + "; " +
                                    "Total Value before Adjustment: " + currentValue + "; " +
                                    "Total Value after Adjustment: " + adjustedValue);
                        }
                );

                // Compute total sale value after adjustment for the product
                BigDecimal totalAjustedValue = tmpProductSales.get(sale.getProduct()).stream().map(adjustmentMapper).reduce(BigDecimal.ZERO, BigDecimal::add);

                // Log report
                System.out.println("Total Value of Sales after " + sale.getAdjustment().getAdjustOperation() + " adjustment for product " + sale.getProduct() + ": " + totalAjustedValue);

            }
        }

    }
}
