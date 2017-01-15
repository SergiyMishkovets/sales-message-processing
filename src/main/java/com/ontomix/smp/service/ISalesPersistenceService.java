package com.ontomix.smp.service;

import com.ontomix.smp.model.Sale;

/**
 * A Sales Persistence Service Interface
 */
public interface ISalesPersistenceService {

    /**
     * Save a Sale record
     * @param sale
     */
    void save(Sale sale);

}
