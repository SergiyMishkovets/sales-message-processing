package com.ontomix.smp.service;

import com.ontomix.smp.model.Adjustment;
import com.ontomix.smp.model.OperationType;
import com.ontomix.smp.model.Sale;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Unit Test for SalesReportingService
 */
public class SalesReportingServiceTest {

    private static final int SCALE = 2;

    @Test
    public void reportSales() throws Exception {

        // Create test data
        List<Sale> sales = new ArrayList<>();

        Sale appleSale1 = new Sale();
        appleSale1.setProduct("Apple");
        appleSale1.setValue(new BigDecimal(12.22));

        Sale appleSale2 = new Sale();
        appleSale2.setProduct("Apple");
        appleSale2.setValue(new BigDecimal(24.00));

        Sale appleSale3 = new Sale();
        appleSale3.setProduct("Apple");
        appleSale3.setValue(new BigDecimal(8.00));
        appleSale3.setOccurrences(4);

        Sale pearSale1 = new Sale();
        pearSale1.setProduct("Pear");
        pearSale1.setValue(new BigDecimal(60.00));
        pearSale1.setOccurrences(4);

        sales.add(appleSale1);
        sales.add(appleSale2);
        sales.add(appleSale3);
        sales.add(pearSale1);

        // Group sales by product type
        Map<String, List<Sale>> productSales = sales.stream().collect(Collectors.groupingBy(Sale::getProduct));

        Assert.assertTrue(productSales.size() == 2);
        Assert.assertTrue(productSales.containsKey("Apple".toUpperCase()));
        Assert.assertTrue(productSales.containsKey("Pear".toUpperCase()));
        Assert.assertTrue(productSales.get("Apple".toUpperCase()).size() == 3);
        Assert.assertTrue(productSales.get("Pear".toUpperCase()).size() == 1);

        // Create a mapper to compute total value of each sale
        Function<Sale, BigDecimal> subTotalMapper = sale -> sale.getValue().multiply(BigDecimal.valueOf(sale.getOccurrences()));

        // Report computed total number and value of sales for each product
        productSales.forEach((k, v) -> {
            int numOfSales = v.stream().collect(Collectors.summingInt(Sale::getOccurrences));
            BigDecimal subTotalValue = v.stream().map(subTotalMapper).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(SCALE, RoundingMode.FLOOR);
            System.out.println("Product: " + k + "; " +
                    "Number of Sales: " + numOfSales + "; " +
                    "Total Value of Sales: " + subTotalValue);

            // Assert
            if (k.equalsIgnoreCase("Apple")) {
                Assert.assertTrue(numOfSales == (appleSale1.getOccurrences() + appleSale2.getOccurrences() + appleSale3.getOccurrences()));

                BigDecimal appleSale1TotalValue = appleSale1.getValue().multiply(BigDecimal.valueOf(appleSale1.getOccurrences())).setScale(SCALE, RoundingMode.FLOOR);
                BigDecimal appleSale2TotalValue = appleSale2.getValue().multiply(BigDecimal.valueOf(appleSale2.getOccurrences())).setScale(SCALE, RoundingMode.FLOOR);
                BigDecimal appleSale3TotalValue = appleSale3.getValue().multiply(BigDecimal.valueOf(appleSale3.getOccurrences())).setScale(SCALE, RoundingMode.FLOOR);

                Assert.assertEquals(appleSale1TotalValue.add(appleSale2TotalValue).add(appleSale3TotalValue), subTotalValue);
            }
            if (k.equalsIgnoreCase("Pear")) {
                Assert.assertTrue(numOfSales == pearSale1.getOccurrences());

                BigDecimal pearSale1TotalValue = pearSale1.getValue().multiply(BigDecimal.valueOf(pearSale1.getOccurrences())).setScale(SCALE, RoundingMode.FLOOR);

                Assert.assertEquals(subTotalValue, pearSale1TotalValue);
            }
        });
    }

    @Test
    public void reportAdjustments() throws Exception {

        // Create test data
        List<Sale> sales = new ArrayList<>();

        Sale appleSale1 = new Sale();
        appleSale1.setProduct("Apple");
        appleSale1.setValue(new BigDecimal(12.22));

        Sale appleSale2 = new Sale();
        appleSale2.setProduct("Apple");
        appleSale2.setValue(new BigDecimal(24.00));

        Sale appleSale3 = new Sale();
        appleSale3.setProduct("Apple");
        appleSale3.setValue(new BigDecimal(8.00));
        appleSale3.setOccurrences(4);

        Sale appleSale4 = new Sale();
        appleSale4.setProduct("Apple");
        appleSale4.setValue(new BigDecimal(8.00));
        appleSale4.setOccurrences(4);
        Adjustment appleSale4Adjust = new Adjustment();
        appleSale4Adjust.setAdjustOperation(OperationType.ADD);
        appleSale4Adjust.setAdjustValue(new BigDecimal("10.00"));
        appleSale4.setAdjustment(appleSale4Adjust);

        Sale pearSale1 = new Sale();
        pearSale1.setProduct("Pear");
        pearSale1.setValue(new BigDecimal(60.00));
        pearSale1.setOccurrences(4);

        sales.add(appleSale1);
        sales.add(appleSale2);
        sales.add(appleSale3);
        sales.add(appleSale4);
        sales.add(pearSale1);

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

                // Group recorded sales by product type for adjustment
                Map<String, List<Sale>> tmpProductSales = adjustSales.stream().collect(Collectors.groupingBy(Sale::getProduct));

                Assert.assertTrue(adjustSales.size() == 4);
                Assert.assertTrue(tmpProductSales.size() == 1);
                Assert.assertTrue(tmpProductSales.containsKey("Apple".toUpperCase()));
                Assert.assertTrue(tmpProductSales.get("Apple".toUpperCase()).size() == 4);

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

                // Assert
                BigDecimal adjustedApple1Sale = appleSale1.getValue().add(appleSale4.getAdjustment().getAdjustValue()).multiply(BigDecimal.valueOf(appleSale1.getOccurrences())).setScale(SCALE, RoundingMode.FLOOR);
                BigDecimal adjustedApple2Sale = appleSale2.getValue().add(appleSale4.getAdjustment().getAdjustValue()).multiply(BigDecimal.valueOf(appleSale2.getOccurrences())).setScale(SCALE, RoundingMode.FLOOR);
                BigDecimal adjustedApple3Sale = appleSale3.getValue().add(appleSale4.getAdjustment().getAdjustValue()).multiply(BigDecimal.valueOf(appleSale3.getOccurrences())).setScale(SCALE, RoundingMode.FLOOR);
                BigDecimal adjustedApple4Sale = appleSale4.getValue().add(appleSale4.getAdjustment().getAdjustValue()).multiply(BigDecimal.valueOf(appleSale4.getOccurrences())).setScale(SCALE, RoundingMode.FLOOR);
                Assert.assertEquals(adjustedApple1Sale.add(adjustedApple2Sale).add(adjustedApple3Sale).add(adjustedApple4Sale).setScale(SCALE, RoundingMode.FLOOR), totalAdjustedValue);

                // Log report
                System.out.println("Total Value of Sales after " + sale.getAdjustment().getAdjustOperation() + " adjustment for product " + sale.getProduct() + ": " + totalAdjustedValue);

            }
        }
    }

}