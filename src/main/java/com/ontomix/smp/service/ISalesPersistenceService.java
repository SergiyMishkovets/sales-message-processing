package com.ontomix.smp.service;

import com.ontomix.smp.model.Sale;

/**
 * A Sales Persistence Service Interface
 */
public interface ISalesPersistenceService {

    /**
     * Store a Sale record
     *
     * @param sale
     * @return An unique record id - UUID
     */
    String save(Sale sale);

    /**
     * Retrieve a Sale record by record Id
     *
     * @param recordId
     * @return the recorded Sale
     */
    Sale find(String recordId);

}
