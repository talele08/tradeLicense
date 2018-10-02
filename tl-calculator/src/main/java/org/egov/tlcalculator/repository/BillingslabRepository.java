package org.egov.tlcalculator.repository;

import lombok.extern.slf4j.Slf4j;
import org.egov.tlcalculator.web.models.BillingSlab;
import org.egov.tlcalculator.web.models.BillingSlabSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public class BillingslabRepository {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private BillingSlabRowMapper billingSlabRowMapper;
	
	public List<BillingSlab> getDataFromDB(String query, List<Object> preparedStmtList){
		List<BillingSlab> slabs = new ArrayList<>();
		try {
			slabs = jdbcTemplate.query(query, preparedStmtList.toArray(), billingSlabRowMapper);
			if(CollectionUtils.isEmpty(slabs))
				return new ArrayList<>();
		}catch(Exception e) {
			log.error("Exception while fetching from DB: " + e);
			return slabs;
		}
		
		return slabs;
	}



}
