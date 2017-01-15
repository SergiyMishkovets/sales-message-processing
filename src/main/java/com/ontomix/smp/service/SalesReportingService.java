package com.ontomix.smp.service;

import com.ontomix.smp.model.Sale;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementation of ISalesReportingService
 * <p>
 * <br>1. Generating Sales Report
 * <br>2. Generating Adjustment Report
 */
public class SalesReportingService implements ISalesReportingService {

    private static final int SCALE = 2;

    @Override
    public void reportSales(List<Sale> sales) {

        // Logging
        System.out.println("Reporting Sales...");

        // Group sales by product type
        Map<String, List<Sale>> productSales = sales.stream().collect(Collectors.groupingBy(Sale::getProduct));

        // Create a mapper to compute total value of each sale
        Function<Sale, BigDecimal> subTotalMapper = sale -> sale.getValue().multiply(BigDecimal.valueOf(sale.getOccurrences()));

        // Report computed total number and value of sales for each product
        productSales.forEach((k, v) -> {

            // Compute total number of sales
            int numOfSale = v.stream().collect(Collectors.summingInt(Sale::getOccurrences));

            // Compute total value of sales
            BigDecimal totalSaleValue = v.stream().map(subTotalMapper).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(SCALE, RoundingMode.FLOOR);

            // Log report
            System.out.println("Product: " + k + "; " +
                    "Number of Sales: " + numOfSale + "; " +
                    "Total Value of Sales: " + totalSaleValue);
        });

    }

    @Override
    public void reportAdjustments(List<Sale> sales) {

        // Logging
        System.out.println("Reporting Adjustments...");

        List<Sale> adjustSales = new ArrayList<>();

        for (Sale sale : sales) {

            // Hold sales for adjustment
            adjustSales.add(sale);

            if (sale.getAdjustment() != null) {

                // Log report
                System.out.println("Adjustment instruction received: "
                        + sale.getAdjustment().getAdjustOperation().toString().toLowerCase() + "ing "
                        + sale.getAdjustment().getAdjustValue() + " to each sale recorded for product " + sale.getProduct() + "...");

                // Create an adjustment mapper
                Function<Sale, BigDecimal> adjustmentMapper;

                switch (sale.getAdjustment().getAdjustOperation()) {
                    case ADD:
                        adjustmentMapper = s -> s.getValue().add(sale.getAdjustment().getAdjustValue()).multiply(BigDecimal.valueOf(s.getOccurrences())).setScale(SCALE, RoundingMode.FLOOR);
                        break;
                    case SUBSTRACT:
                        adjustmentMapper = s -> s.getValue().subtract(sale.getAdjustment().getAdjustValue()).multiply(BigDecimal.valueOf(s.getOccurrences())).setScale(SCALE, RoundingMode.FLOOR);
                        break;
                    case MULTIPLY:
                        adjustmentMapper = s -> s.getValue().multiply(sale.getAdjustment().getAdjustValue()).multiply(BigDecimal.valueOf(s.getOccurrences())).setScale(SCALE, RoundingMode.FLOOR);
                        break;
                    default:
                        adjustmentMapper = s -> s.getValue().multiply(BigDecimal.valueOf(s.getOccurrences())).setScale(SCALE, RoundingMode.FLOOR);
                }

                // Group recorded sales by product type for adjustment
                Map<String, List<Sale>> tmpProductSales = adjustSales.stream().collect(Collectors.groupingBy(Sale::getProduct));

                // Apply adjustment to each product recorded and log report
                tmpProductSales.get(sale.getProduct()).forEach(s -> {

                            // Compute current recorded sale value
                            BigDecimal currentValue = s.getValue().multiply(BigDecimal.valueOf(s.getOccurrences())).setScale(SCALE, RoundingMode.FLOOR);

                            // Apply the adjustment mapper
                            BigDecimal adjustedValue = adjustmentMapper.apply(s).setScale(SCALE, RoundingMode.FLOOR);

                            System.out.println("Product: " + s.getProduct() + "; " +
                                    "Number of Sales: " + s.getOccurrences() + "; " +
                                    "Total Value before Adjustment: " + currentValue + "; " +
                                    "Total Value after Adjustment: " + adjustedValue);
                        }
                );

                // Compute total sale value after adjustment for the product
                BigDecimal totalAdjustedValue = tmpProductSales.get(sale.getProduct()).stream().map(adjustmentMapper).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(SCALE, RoundingMode.FLOOR);

                // Log report
                System.out.println("Total Value of Sales after " + sale.getAdjustment().getAdjustOperation() + " adjustment for product " + sale.getProduct() + ": " + totalAdjustedValue);

            }
        }

    }
}
