package org.egov.tl.repository;

import org.egov.tl.repository.builder.TLQueryBuilder;
import org.egov.tl.repository.rowmapper.TLRowMapper;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class TLRepository {

    private JdbcTemplate jdbcTemplate;

    private TLQueryBuilder queryBuilder;

    private TLRowMapper rowMapper;

    @Autowired
    public TLRepository(JdbcTemplate jdbcTemplate, TLQueryBuilder queryBuilder, TLRowMapper rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.queryBuilder = queryBuilder;
        this.rowMapper = rowMapper;
    }

    public List<TradeLicense> getLicenses(TradeLicenseSearchCriteria criteria){
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getTLSearchQuery(criteria, preparedStmtList);
        //log.info("Query: "+query);
        return jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
    }

}
