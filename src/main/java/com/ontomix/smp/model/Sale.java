package com.ontomix.smp.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

/**
 * Sale Data Model
 */
@XmlRootElement(name="Sale")
@XmlAccessorType(XmlAccessType.FIELD)
public class Sale {

    @XmlElement(name="Product", required=true)
    private String product;
    @XmlElement(name="Value", required=true)
    private BigDecimal value;
    @XmlElement(name="Occurrences")
    private int occurrences = 1; // Default number of sales
    @XmlElement(name="Adjustment")
    private Adjustment adjustment;

    public String getProduct() {
        return product.toUpperCase();
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public int getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(int occurrences) {
        this.occurrences = occurrences;
    }

    public Adjustment getAdjustment() {
        return adjustment;
    }

    public void setAdjustment(Adjustment adjustment) {
        this.adjustment = adjustment;
    }

    @Override
    public String toString() {
        return "Sale{" +
                "product='" + product + '\'' +
                ", value=" + value +
                ", occurrences=" + occurrences +
                ", adjustment=" + adjustment +
                '}';
    }
}
