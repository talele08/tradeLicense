package org.egov.tl.web.controllers;


import org.egov.tl.service.TradeLicenseService;
import org.egov.tl.util.ResponseInfoFactory;
import org.egov.tl.web.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

import javax.validation.constraints.*;
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;

@RestController
    @RequestMapping("/v1")
    public class TradeLicenseController {

        private final ObjectMapper objectMapper;

        private final HttpServletRequest request;

        private final TradeLicenseService tradeLicenseService;

        private final ResponseInfoFactory responseInfoFactory;

    @Autowired
    public TradeLicenseController(ObjectMapper objectMapper, HttpServletRequest request,
                                  TradeLicenseService tradeLicenseService, ResponseInfoFactory responseInfoFactory) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.tradeLicenseService = tradeLicenseService;
        this.responseInfoFactory = responseInfoFactory;
    }



        @PostMapping("/_create")
        public ResponseEntity<TradeLicenseResponse> create(@Valid @RequestBody TradeLicenseRequest tradeLicenseRequest)
        {       tradeLicenseService.create(tradeLicenseRequest);
                return new ResponseEntity<TradeLicenseResponse>(HttpStatus.OK);
        }

        @RequestMapping(value="/_search", method = RequestMethod.POST)
        public ResponseEntity<TradeLicenseResponse> search(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
                                                           @Valid @ModelAttribute TradeLicenseSearchCriteria criteria){

            List<TradeLicense> licenses = tradeLicenseService.search(criteria,requestInfoWrapper.getRequestInfo());

            TradeLicenseResponse response = TradeLicenseResponse.builder().licenses(licenses).responseInfo(
                    responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);        }

        @RequestMapping(value="/_update", method = RequestMethod.POST)
        public ResponseEntity<TradeLicenseResponse> v1UpdatePost(@ApiParam(value = "Details for the new TradeLicense(s) + RequestInfo meta data." ,required=true )  @Valid @RequestBody TradeLicenseRequest tradeLicenseRequest) {
                String accept = request.getHeader("Accept");
                    if (accept != null && accept.contains("application/json")) {
                    try {
                    return new ResponseEntity<TradeLicenseResponse>(objectMapper.readValue("{  \"licenses\" : [ {    \"oldPropertyId\" : \"oldPropertyId\",    \"oldLicenseNumber\" : \"oldLicenseNumber\",    \"issuedDate\" : 5,    \"validFrom\" : 5,    \"commencementDate\" : 1,    \"financialYear\" : \"financialYear\",    \"licenseType\" : \"TEMPORARY\",    \"tradeLicenseDetail\" : {      \"additionalDetail\" : \"additionalDetail\",      \"address\" : {        \"pincode\" : \"pincode\",        \"city\" : \"city\",        \"latitude\" : 7.061401241503109,        \"locality\" : {          \"code\" : \"code\",          \"materializedPath\" : \"materializedPath\",          \"children\" : [ null, null ],          \"latitude\" : \"latitude\",          \"name\" : \"name\",          \"label\" : \"label\",          \"longitude\" : \"longitude\"        },        \"type\" : \"type\",        \"addressId\" : \"addressId\",        \"buildingName\" : \"buildingName\",        \"street\" : \"street\",        \"tenantId\" : \"tenantId\",        \"addressNumber\" : \"addressNumber\",        \"addressLine1\" : \"addressLine1\",        \"addressLine2\" : \"addressLine2\",        \"doorNo\" : \"doorNo\",        \"detail\" : \"detail\",        \"landmark\" : \"landmark\",        \"longitude\" : 9.301444243932576      },      \"verificationDocuments\" : [ {        \"documentType\" : \"documentType\",        \"auditDetails\" : {          \"lastModifiedTime\" : 2,          \"createdBy\" : \"createdBy\",          \"lastModifiedBy\" : \"lastModifiedBy\",          \"createdTime\" : 3        },        \"tenantId\" : \"tenantId\",        \"fileStoreId\" : \"fileStoreId\"      }, {        \"documentType\" : \"documentType\",        \"auditDetails\" : {          \"lastModifiedTime\" : 2,          \"createdBy\" : \"createdBy\",          \"lastModifiedBy\" : \"lastModifiedBy\",          \"createdTime\" : 3        },        \"tenantId\" : \"tenantId\",        \"fileStoreId\" : \"fileStoreId\"      } ],      \"tradeUnits\" : [ {        \"uom\" : \"uom\",        \"auditDetails\" : {          \"lastModifiedTime\" : 2,          \"createdBy\" : \"createdBy\",          \"lastModifiedBy\" : \"lastModifiedBy\",          \"createdTime\" : 3        },        \"tenantId\" : \"tenantId\",        \"uomValue\" : \"uomValue\",        \"tradeType\" : \"tradeType\"      }, {        \"uom\" : \"uom\",        \"auditDetails\" : {          \"lastModifiedTime\" : 2,          \"createdBy\" : \"createdBy\",          \"lastModifiedBy\" : \"lastModifiedBy\",          \"createdTime\" : 3        },        \"tenantId\" : \"tenantId\",        \"uomValue\" : \"uomValue\",        \"tradeType\" : \"tradeType\"      } ],      \"accessories\" : [ {        \"uom\" : \"uom\",        \"auditDetails\" : {          \"lastModifiedTime\" : 2,          \"createdBy\" : \"createdBy\",          \"lastModifiedBy\" : \"lastModifiedBy\",          \"createdTime\" : 3        },        \"tenantId\" : \"tenantId\",        \"uomValue\" : \"uomValue\",        \"accessoryCategory\" : \"accessoryCategory\"      }, {        \"uom\" : \"uom\",        \"auditDetails\" : {          \"lastModifiedTime\" : 2,          \"createdBy\" : \"createdBy\",          \"lastModifiedBy\" : \"lastModifiedBy\",          \"createdTime\" : 3        },        \"tenantId\" : \"tenantId\",        \"uomValue\" : \"uomValue\",        \"accessoryCategory\" : \"accessoryCategory\"      } ],      \"auditDetails\" : {        \"lastModifiedTime\" : 2,        \"createdBy\" : \"createdBy\",        \"lastModifiedBy\" : \"lastModifiedBy\",        \"createdTime\" : 3      },      \"channel\" : \"COUNTER\",      \"owners\" : [ \"\", \"\" ],      \"surveyNo\" : \"surveyNo\",      \"applicationDocuments\" : [ {        \"documentType\" : \"documentType\",        \"auditDetails\" : {          \"lastModifiedTime\" : 2,          \"createdBy\" : \"createdBy\",          \"lastModifiedBy\" : \"lastModifiedBy\",          \"createdTime\" : 3        },        \"tenantId\" : \"tenantId\",        \"fileStoreId\" : \"fileStoreId\"      }, {        \"documentType\" : \"documentType\",        \"auditDetails\" : {          \"lastModifiedTime\" : 2,          \"createdBy\" : \"createdBy\",          \"lastModifiedBy\" : \"lastModifiedBy\",          \"createdTime\" : 3        },        \"tenantId\" : \"tenantId\",        \"fileStoreId\" : \"fileStoreId\"      } ],      \"subOwnerShipCategory\" : \"subOwnerShipCategory\"    },    \"auditDetails\" : {      \"lastModifiedTime\" : 2,      \"createdBy\" : \"createdBy\",      \"lastModifiedBy\" : \"lastModifiedBy\",      \"createdTime\" : 3    },    \"tenantId\" : \"tenantId\",    \"action\" : \"INITIATE\",    \"licenseNumber\" : \"licenseNumber\",    \"id\" : \"id\",    \"propertyId\" : \"propertyId\",    \"applicationDate\" : 6,    \"validTo\" : 2,    \"status\" : \"INITIATED\"  }, {    \"oldPropertyId\" : \"oldPropertyId\",    \"oldLicenseNumber\" : \"oldLicenseNumber\",    \"issuedDate\" : 5,    \"validFrom\" : 5,    \"commencementDate\" : 1,    \"financialYear\" : \"financialYear\",    \"licenseType\" : \"TEMPORARY\",    \"tradeLicenseDetail\" : {      \"additionalDetail\" : \"additionalDetail\",      \"address\" : {        \"pincode\" : \"pincode\",        \"city\" : \"city\",        \"latitude\" : 7.061401241503109,        \"locality\" : {          \"code\" : \"code\",          \"materializedPath\" : \"materializedPath\",          \"children\" : [ null, null ],          \"latitude\" : \"latitude\",          \"name\" : \"name\",          \"label\" : \"label\",          \"longitude\" : \"longitude\"        },        \"type\" : \"type\",        \"addressId\" : \"addressId\",        \"buildingName\" : \"buildingName\",        \"street\" : \"street\",        \"tenantId\" : \"tenantId\",        \"addressNumber\" : \"addressNumber\",        \"addressLine1\" : \"addressLine1\",        \"addressLine2\" : \"addressLine2\",        \"doorNo\" : \"doorNo\",        \"detail\" : \"detail\",        \"landmark\" : \"landmark\",        \"longitude\" : 9.301444243932576      },      \"verificationDocuments\" : [ {        \"documentType\" : \"documentType\",        \"auditDetails\" : {          \"lastModifiedTime\" : 2,          \"createdBy\" : \"createdBy\",          \"lastModifiedBy\" : \"lastModifiedBy\",          \"createdTime\" : 3        },        \"tenantId\" : \"tenantId\",        \"fileStoreId\" : \"fileStoreId\"      }, {        \"documentType\" : \"documentType\",        \"auditDetails\" : {          \"lastModifiedTime\" : 2,          \"createdBy\" : \"createdBy\",          \"lastModifiedBy\" : \"lastModifiedBy\",          \"createdTime\" : 3        },        \"tenantId\" : \"tenantId\",        \"fileStoreId\" : \"fileStoreId\"      } ],      \"tradeUnits\" : [ {        \"uom\" : \"uom\",        \"auditDetails\" : {          \"lastModifiedTime\" : 2,          \"createdBy\" : \"createdBy\",          \"lastModifiedBy\" : \"lastModifiedBy\",          \"createdTime\" : 3        },        \"tenantId\" : \"tenantId\",        \"uomValue\" : \"uomValue\",        \"tradeType\" : \"tradeType\"      }, {        \"uom\" : \"uom\",        \"auditDetails\" : {          \"lastModifiedTime\" : 2,          \"createdBy\" : \"createdBy\",          \"lastModifiedBy\" : \"lastModifiedBy\",          \"createdTime\" : 3        },        \"tenantId\" : \"tenantId\",        \"uomValue\" : \"uomValue\",        \"tradeType\" : \"tradeType\"      } ],      \"accessories\" : [ {        \"uom\" : \"uom\",        \"auditDetails\" : {          \"lastModifiedTime\" : 2,          \"createdBy\" : \"createdBy\",          \"lastModifiedBy\" : \"lastModifiedBy\",          \"createdTime\" : 3        },        \"tenantId\" : \"tenantId\",        \"uomValue\" : \"uomValue\",        \"accessoryCategory\" : \"accessoryCategory\"      }, {        \"uom\" : \"uom\",        \"auditDetails\" : {          \"lastModifiedTime\" : 2,          \"createdBy\" : \"createdBy\",          \"lastModifiedBy\" : \"lastModifiedBy\",          \"createdTime\" : 3        },        \"tenantId\" : \"tenantId\",        \"uomValue\" : \"uomValue\",        \"accessoryCategory\" : \"accessoryCategory\"      } ],      \"auditDetails\" : {        \"lastModifiedTime\" : 2,        \"createdBy\" : \"createdBy\",        \"lastModifiedBy\" : \"lastModifiedBy\",        \"createdTime\" : 3      },      \"channel\" : \"COUNTER\",      \"owners\" : [ \"\", \"\" ],      \"surveyNo\" : \"surveyNo\",      \"applicationDocuments\" : [ {        \"documentType\" : \"documentType\",        \"auditDetails\" : {          \"lastModifiedTime\" : 2,          \"createdBy\" : \"createdBy\",          \"lastModifiedBy\" : \"lastModifiedBy\",          \"createdTime\" : 3        },        \"tenantId\" : \"tenantId\",        \"fileStoreId\" : \"fileStoreId\"      }, {        \"documentType\" : \"documentType\",        \"auditDetails\" : {          \"lastModifiedTime\" : 2,          \"createdBy\" : \"createdBy\",          \"lastModifiedBy\" : \"lastModifiedBy\",          \"createdTime\" : 3        },        \"tenantId\" : \"tenantId\",        \"fileStoreId\" : \"fileStoreId\"      } ],      \"subOwnerShipCategory\" : \"subOwnerShipCategory\"    },    \"auditDetails\" : {      \"lastModifiedTime\" : 2,      \"createdBy\" : \"createdBy\",      \"lastModifiedBy\" : \"lastModifiedBy\",      \"createdTime\" : 3    },    \"tenantId\" : \"tenantId\",    \"action\" : \"INITIATE\",    \"licenseNumber\" : \"licenseNumber\",    \"id\" : \"id\",    \"propertyId\" : \"propertyId\",    \"applicationDate\" : 6,    \"validTo\" : 2,    \"status\" : \"INITIATED\"  } ],  \"responseInfo\" : {    \"ver\" : \"ver\",    \"resMsgId\" : \"resMsgId\",    \"msgId\" : \"msgId\",    \"apiId\" : \"apiId\",    \"ts\" : 0,    \"status\" : \"SUCCESSFUL\"  }}", TradeLicenseResponse.class), HttpStatus.NOT_IMPLEMENTED);
                    } catch (IOException e) {
                    return new ResponseEntity<TradeLicenseResponse>(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                    }

                return new ResponseEntity<TradeLicenseResponse>(HttpStatus.NOT_IMPLEMENTED);
        }

        }
