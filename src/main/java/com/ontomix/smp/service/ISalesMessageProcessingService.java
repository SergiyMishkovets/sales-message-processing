package com.ontomix.smp.service;

import com.ontomix.smp.model.Sale;

import javax.xml.bind.JAXBException;

/**
 * A Sales Message Processing Service Interface
 */
public interface ISalesMessageProcessingService {

    /**
     * Unmarshal an XML message to a Sale object
     *
     * @param msgText
     * @return An unmarshalled Sale object
     * @throws JAXBException
     */
    Sale unmarshalSaleMessage(String msgText) throws JAXBException;

    /**
     * Process a Sale record
     *
     * @param sale
     */
    void processSaleRecord(Sale sale);
}
