package com.ontomix.smp.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.math.BigDecimal;

/**
 * Adjustment Data Model
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Adjustment {

    @XmlElement(name="AdjustmentOperation", required=true)
    private OperationType adjustOperation;
    @XmlElement(name="AdjustmentValue", required=true)
    private BigDecimal adjustValue;

    public OperationType getAdjustOperation() {
        return adjustOperation;
    }

    public void setAdjustOperation(OperationType adjustOperation) {
        this.adjustOperation = adjustOperation;
    }

    public BigDecimal getAdjustValue() {
        return adjustValue;
    }

    public void setAdjustValue(BigDecimal adjustValue) {
        this.adjustValue = adjustValue;
    }
}
