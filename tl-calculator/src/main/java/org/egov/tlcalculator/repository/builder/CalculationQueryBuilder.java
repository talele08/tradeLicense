package org.egov.tlcalculator.repository.builder;

import org.egov.tlcalculator.web.models.BillingSlabSearchCriteria;
import org.egov.tlcalculator.web.models.CalculationSearchCriteria;

import java.util.List;

public class CalculationQueryBuilder {


    private static final String INNER_JOIN_STRING = " INNER JOIN ";
    private static final String LEFT_OUTER_JOIN_STRING = " LEFT OUTER JOIN ";

    private static final String QUERY = "SELECT tp.*,acc.*,tp.consumercode as tp_consumercode FROM eg_tl_calculator_tradetype tp " +
               LEFT_OUTER_JOIN_STRING +
            " eg_tl_calculator_accessory acc ON acc.consumercode = tp.consumercode " +
            " WHERE ";


    public String getSearchQuery(CalculationSearchCriteria criteria, List<Object> preparedStmtList){
        StringBuilder builder = new StringBuilder(QUERY);

        builder.append(" tp.tenantid=? ");
        preparedStmtList.add(criteria.getTenantId());

        builder.append(" tp.consumercode=? ");
        preparedStmtList.add(criteria.getAplicationNumber());

        builder.append("ORDER BY tp.lastmodifiedtime DESC LIMIT 1");

        return builder.toString();
    }


}
