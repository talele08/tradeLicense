package org.egov.tl.repository;

import org.egov.tl.repository.builder.TLQueryBuilder;
import org.egov.tl.repository.rowmapper.TLRowMapper;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

public class TLRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TLQueryBuilder queryBuilder;

    @Autowired
    private TLRowMapper rowMapper;

    public List<TradeLicense> getLicenses(TradeLicenseSearchCriteria criteria){
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getTLSearchQuery(criteria, preparedStmtList);
        //log.info("Query: "+query);
        return jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
    }

}
