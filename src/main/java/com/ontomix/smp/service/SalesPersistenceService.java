package com.ontomix.smp.service;

import com.ontomix.smp.model.Sale;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation of ISalesPersistenceService
 * <p>
 * In memory Sale records store implemented by LinkedHashMap
 * </p>
 */
public class SalesPersistenceService implements ISalesPersistenceService {

    private static final Map<String, Sale> SALE_RECORDS = new LinkedHashMap<>();

    @Override
    public String save(Sale sale) {
        // Generate an unique record id
        String id = UUID.randomUUID().toString();
        SALE_RECORDS.put(id, sale);
        return id;
    }

    @Override
    public Sale find(String recordId) {
        return SALE_RECORDS.get(recordId);
    }
}
