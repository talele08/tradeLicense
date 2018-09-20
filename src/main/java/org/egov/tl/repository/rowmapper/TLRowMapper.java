package org.egov.tl.repository.rowmapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.tl.web.models.*;
import org.egov.tracer.model.CustomException;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TLRowMapper  implements ResultSetExtractor<List<TradeLicense>> {


    @Autowired
    private ObjectMapper mapper;



    public List<TradeLicense> extractData(ResultSet rs) throws SQLException, DataAccessException {

        Map<String, TradeLicense> tradeLicenseMap = new HashMap<>();

        while (rs.next()) {
            String id = rs.getString("tl_id");
            TradeLicense currentTradeLicense = tradeLicenseMap.get(id);
            String tenantId = rs.getString("tl_tenantId");

            if(currentTradeLicense == null){
                Long lastModifiedTime = rs.getLong("tl_lastModifiedTime");
                if(rs.wasNull()){lastModifiedTime = null;}

                Long commencementDate = (Long) rs.getObject("commencementdate");
                Long issuedDate = (Long) rs.getObject("issueddate");
                Long validFrom = (Long) rs.getObject("validto");
                Long validTo = (Long) rs.getObject("validfrom");
                Long applicationDate = (Long) rs.getObject("applicationdate");

                AuditDetails auditdetails = AuditDetails.builder()
                        .createdBy(rs.getString("tl_createdBy"))
                        .createdTime(rs.getLong("tl_createdTime"))
                        .lastModifiedBy(rs.getString("tl_lastModifiedBy"))
                        .lastModifiedTime(lastModifiedTime)
                        .build();

                currentTradeLicense = TradeLicense.builder().auditDetails(auditdetails)
                        .licenseNumber(rs.getString("licensenumber"))
                        .licenseType(TradeLicense.LicenseTypeEnum.fromValue(rs.getString("licensetype")))
                        .oldLicenseNumber(rs.getString("oldlicensenumber"))
                        .applicationDate(applicationDate)
                        .commencementDate(commencementDate)
                        .issuedDate(issuedDate)
                        .financialYear(rs.getString("financialyear"))
                        .validFrom(validFrom)
                        .validTo(validTo)
                        .action(TradeLicense.ActionEnum.fromValue(rs.getString("action")))
                        .status(TradeLicense.StatusEnum.fromValue(rs.getString("status")))
                        .tenantId(tenantId)
                        .propertyId(rs.getString("propertyid"))
                        .oldPropertyId(rs.getString("oldpropertyid"))
                        .id(id)
                        .build();

                tradeLicenseMap.put(id,currentTradeLicense);
            }
            addChildrenToProperty(rs, currentTradeLicense);

        }

        return new ArrayList<>(tradeLicenseMap.values());

    }



    private void addChildrenToProperty(ResultSet rs, TradeLicense tradeLicense) throws SQLException {

        String tenantId = tradeLicense.getTenantId();
        String tradeLicenseDetailId = rs.getString("tld_id");

        if(tradeLicense.getTradeLicenseDetail()==null){

            Boundary locality = Boundary.builder().code(rs.getString("locality"))
                    .build();

            Double latitude = (Double) rs.getObject("latitude");
            Double longitude = (Double) rs.getObject("longitude");

            Address address = Address.builder().addressId(rs.getString("addressId"))
                    .addressLine1(rs.getString("addressLine1"))
                    .addressLine2(rs.getString("addressLine2"))
                    .addressNumber(rs.getString("addressNumber"))
                    .buildingName(rs.getString("buildingName"))
                    .city(rs.getString("city"))
                    .detail(rs.getString("detail"))
                    .id(rs.getString("tl_ad_id"))
                    .landmark(rs.getString("landmark"))
                    .latitude(latitude)
                    .locality(locality)
                    .longitude(longitude)
                    .pincode(rs.getString("pincode"))
                    .doorNo(rs.getString("doorno"))
                    .street(rs.getString("street"))
                    .tenantId(tenantId)
                    .type(rs.getString("type"))
                    .build();

            AuditDetails auditdetails = AuditDetails.builder()
                    .createdBy(rs.getString("tld_createdBy"))
                    .createdTime(rs.getLong("tld_createdTime"))
                    .lastModifiedBy(rs.getString("tld_lastModifiedBy"))
                    .lastModifiedTime(rs.getLong("tld_createdTime"))
                    .build();

            PGobject obj = (PGobject) rs.getObject("additionalDetails");
            try {
                JsonNode additionalDetails = mapper.readTree( obj.getValue());
                TradeLicenseDetail tradeLicenseDetail = TradeLicenseDetail.builder()
                        .surveyNo(rs.getString("surveyno"))
                        .channel(TradeLicenseDetail.ChannelEnum.valueOf(rs.getString("channel")))
                        .subOwnerShipCategory(rs.getString("subownershipcategory"))
                        .id(tradeLicenseDetailId)
                        .additionalDetail(additionalDetails)
                        .address(address)
                        .auditDetails(auditdetails)
                        .build();
                tradeLicense.setTradeLicenseDetail(tradeLicenseDetail);
            }
            catch (IOException e){
                throw new CustomException("PARSING ERROR","The additionalDetail json cannot be parsed");
            }
        }

        if(rs.getString("tl_un_id")!=null){
            TradeUnit tradeUnit = TradeUnit.builder()
                    .tradeType(rs.getString("tl_un_tradeType"))
                    .uom(rs.getString("tl_un_uom"))
                    .id(rs.getString("tl_un_id"))
                    .uomValue(rs.getString("tl_un_uomvalue"))
                    .tenantId(tenantId)
                    .build();
            tradeLicense.getTradeLicenseDetail().addTradeUnitsItem(tradeUnit);
        }

        if(rs.getString("tl_acc_id")!=null) {
            Accessory accessory = Accessory.builder()
                    .accessoryCategory(rs.getString("accessoryCategory"))
                    .uom(rs.getString("tl_acc_uom"))
                    .id(rs.getString("tl_acc_id"))
                    .uomValue(rs.getString("tl_acc_uomvalue"))
                    .tenantId(tenantId)
                    .build();
            tradeLicense.getTradeLicenseDetail().addAccessoriesItem(accessory);
        }

        Boolean isPrimaryOwner = (Boolean) rs.getObject("isprimaryowner");
        Double ownerShipPercentage = (Double) rs.getObject("ownershippercentage") ;

        OwnerInfo owner = OwnerInfo.builder()
                .uuid(rs.getString("userid"))
                .isPrimaryOwner(isPrimaryOwner)
                .ownerType(rs.getString("ownerType"))
                .ownerShipPercentage(ownerShipPercentage)
                .build();
        tradeLicense.getTradeLicenseDetail().addOwnersItem(owner);

        if(rs.getString("tl_ap_doc_id")!=null) {
            Document applicationDocument = Document.builder()
                    .documentType(rs.getString("tl_ap_doc_documenttype"))
                    .fileStoreId(rs.getString("tl_ap_doc_filestoreid"))
                    .id(rs.getString("tl_ap_doc_id"))
                    .tenantId(tenantId)
                    .build();
            tradeLicense.getTradeLicenseDetail().addApplicationDocumentsItem(applicationDocument);
        }

        if(rs.getString("tl_ver_doc_id")!=null) {
            Document verificationDocument = Document.builder()
                    .documentType(rs.getString("tl_ver_doc_documenttype"))
                    .fileStoreId(rs.getString("tl_ver_doc_filestoreid"))
                    .id(rs.getString("tl_ver_doc_id"))
                    .tenantId(tenantId)
                    .build();
            tradeLicense.getTradeLicenseDetail().addVerificationDocumentsItem(verificationDocument);
        }
    }




}
