package org.egov.tlcalculator.repository;

import lombok.extern.slf4j.Slf4j;
import org.egov.tlcalculator.repository.rowmapper.BillingSlabRowMapper;
import org.egov.tlcalculator.repository.rowmapper.CalculationRowMapper;
import org.egov.tlcalculator.web.models.BillingSlab;
import org.egov.tlcalculator.web.models.BillingSlabIds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public class CalculationRepository {


    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CalculationRowMapper calculationRowMapper;

    public BillingSlabIds getDataFromDB(String query, List<Object> preparedStmtList){
        BillingSlabIds billingSlabIds = null;
        try {
            billingSlabIds = jdbcTemplate.query(query, preparedStmtList.toArray(), calculationRowMapper);
        }catch(Exception e) {
            log.error("Exception while fetching from DB: " + e);
            return billingSlabIds;
        }

        return billingSlabIds;
    }


}
