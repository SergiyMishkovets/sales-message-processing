package com.ontomix.smp.service;

import com.ontomix.smp.model.Sale;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation of ISalesPersistenceService
 * <p>
 * A sale records store implemented by a LinkedHashMap
 * </p>
 */
public class SalesPersistenceService implements ISalesPersistenceService {

    private static final Map<String, Sale> SALE_RECORDS = new LinkedHashMap<>();

    @Override
    public void save(Sale sale) {
        SALE_RECORDS.put(UUID.randomUUID().toString(), sale);
    }
}
