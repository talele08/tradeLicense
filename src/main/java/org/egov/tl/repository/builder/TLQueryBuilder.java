package org.egov.tl.repository.builder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.tl.web.models.*;
import org.egov.tracer.model.CustomException;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component
public class TLQueryBuilder {

    private static final String INNER_JOIN_STRING = " INNER JOIN ";
    private static final String LEFT_OUTER_JOIN_STRING = " LEFT OUTER JOIN ";

    private static final String QUERY = "SELECT tl.*,tld.*,tlunit.*,tlacc.*,tlowner.*," +
            "tladdress.*,tlapldoc.*,tlverdoc.*,tlownerdoc.*,tl.id as tl_id,tl.tenantid as tl_tenantId,tl.lastModifiedTime as " +
            "tl_lastModifiedTime,tl.createdBy as tl_createdBy,tl.lastModifiedBy as tl_lastModifiedBy,tl.createdTime as " +
            "tl_createdTime,tld.id as tld_id,tladdress.id as tl_ad_id,tld.createdBy as tld_createdBy," +
            "tlowner.id as userid,tlowner.active as useractive," +
            "tld.createdTime as tld_createdTime,tld.lastModifiedBy as tld_lastModifiedBy,tld.createdTime as " +
            "tld_createdTime,tlunit.id as tl_un_id,tlunit.tradeType as tl_un_tradeType,tlunit.uom as tl_un_uom,tlunit.active as tl_un_active," +
            "tlunit.uomvalue as tl_un_uomvalue,tlacc.id as tl_acc_id,tlacc.uom as tl_acc_uom,tlacc.uomvalue as tl_acc_uomvalue,tlacc.active as tl_acc_active," +
            "tlapldoc.id as tl_ap_doc_id,tlapldoc.documenttype as tl_ap_doc_documenttype,tlapldoc.filestoreid as tl_ap_doc_filestoreid,tlapldoc.active as tl_ap_doc_active," +
            "tlverdoc.id as tl_ver_doc_id,tlverdoc.documenttype as tl_ver_doc_documenttype,tlverdoc.filestoreid as tl_ver_doc_filestoreid,tlverdoc.active as tl_ver_doc_active," +
            "tlownerdoc.userid as docuserid,tlownerdoc.tradeLicenseDetailId as doctradelicensedetailid,tlownerdoc.id as ownerdocid,"+
            "tlownerdoc.documenttype as ownerdocType,tlownerdoc.filestoreid as ownerfileStoreId,tlownerdoc.documentuid as ownerdocuid,tlownerdoc.active as ownerdocactive "+
            " FROM eg_tl_tradelicense tl"
            +INNER_JOIN_STRING
            +"eg_tl_tradelicensedetail tld ON tld.tradelicenseid = tl.id"
            +INNER_JOIN_STRING
            +"eg_tl_address tladdress ON tladdress.tradelicensedetailid = tld.id"
            +INNER_JOIN_STRING
            +"eg_tl_owner tlowner ON tlowner.tradelicensedetailid = tld.id"
            +INNER_JOIN_STRING
            +"eg_tl_tradeunit tlunit ON tlunit.tradelicensedetailid = tld.id"
            +LEFT_OUTER_JOIN_STRING
            +"eg_tl_accessory tlacc ON tlacc.tradelicensedetailid = tld.id"
            +LEFT_OUTER_JOIN_STRING
            +"eg_tl_document_owner tlownerdoc ON tlownerdoc.userid = tlowner.id"
            +LEFT_OUTER_JOIN_STRING
            +"eg_tl_applicationdocument tlapldoc ON tlapldoc.tradelicensedetailid = tld.id"
            +LEFT_OUTER_JOIN_STRING
            +"eg_tl_verificationdocument tlverdoc ON tlverdoc.tradelicensedetailid = tld.id"
            +" WHERE ";








    public String getTLSearchQuery(TradeLicenseSearchCriteria criteria, List<Object> preparedStmtList) {

        StringBuilder builder = new StringBuilder(QUERY);

        builder.append(" tl.tenantid=? ");
        preparedStmtList.add(criteria.getTenantId());

        if(criteria.getAccountId()!=null){
            builder.append(" and tl.oldlicensenumber = ? ");
            preparedStmtList.add(criteria.getOldLicenseNumber());
            return builder.toString();
        }

        List<String> ids = criteria.getIds();
        if(!CollectionUtils.isEmpty(ids)) {
            builder.append("and tl.id IN (").append(createQuery(ids)).append(")");
            addToPreparedStatement(preparedStmtList,ids);
        }

        if(criteria.getApplicationNumber()!=null){
            builder.append(" and tl.applicationnumber = ? ");
            preparedStmtList.add(criteria.getApplicationNumber());
        }

        if(criteria.getStatus()!=null){
            builder.append(" and tl.status = ? ");
            preparedStmtList.add(criteria.getStatus());
        }

        if(criteria.getLicenseNumber()!=null){
            builder.append(" and tl.licensenumber = ? ");
            preparedStmtList.add(criteria.getLicenseNumber());
        }

        if(criteria.getOldLicenseNumber()!=null){
            builder.append(" and tl.oldlicensenumber = ? ");
            preparedStmtList.add(criteria.getOldLicenseNumber());
        }

        return builder.toString();
    }




    private String createQuery(List<String> ids) {
        StringBuilder builder = new StringBuilder();
        int length = ids.size();
        for( int i = 0; i< length; i++){
            builder.append(" ?");
            if(i != length -1) builder.append(",");
        }
        return builder.toString();
    }

    private void addToPreparedStatement(List<Object> preparedStmtList,List<String> ids)
    { ids.forEach(id ->{ preparedStmtList.add(id);});
    }


    }
