package org.egov.tlcalculator.repository.rowmapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.tlcalculator.web.models.BillingSlabIds;
import org.egov.tlcalculator.web.models.FeeAndBillingSlabIds;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;


@Component
    public class CalculationRowMapper implements ResultSetExtractor<BillingSlabIds> {


    @Autowired
    private ObjectMapper mapper;

        /**
         * Rowmapper that maps every column of the search result set to a key in the model.
         */
        @Override
        public BillingSlabIds extractData(ResultSet rs) throws SQLException, DataAccessException {
           BillingSlabIds billingSlabIds = new BillingSlabIds();

            while (rs.next()) {

                String consumerCode = rs.getString("tp_consumercode");
                PGobject pgObjTradeType = (PGobject) rs.getObject("tradetypefeeandbillingslabids");
                PGobject pgObjAccessory = (PGobject) rs.getObject("tradetypefeeandbillingslabids");

                FeeAndBillingSlabIds tradeTypeBillingSlabIds = mapper.convertValue(pgObjTradeType,FeeAndBillingSlabIds.class);
                FeeAndBillingSlabIds accessoryBillingSlabIds = null;

                if(pgObjAccessory!=null)
                     accessoryBillingSlabIds = mapper.convertValue(pgObjAccessory,FeeAndBillingSlabIds.class);

                billingSlabIds.setConsumerCode(consumerCode);
                billingSlabIds.setTradeTypeBillingSlabIds(tradeTypeBillingSlabIds.getBillingSlabIds());
                if(accessoryBillingSlabIds!=null)
                    billingSlabIds.setAccesssoryBillingSlabIds(accessoryBillingSlabIds.getBillingSlabIds());
            }
            return billingSlabIds;
        }

}
