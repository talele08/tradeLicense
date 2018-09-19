package org.egov.tl.web.controllers;


import org.egov.tl.service.TradeLicenseService;
import org.egov.tl.web.models.TradeLicenseRequest;
import org.egov.tl.web.models.TradeLicenseResponse;
    import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import java.io.IOException;
import java.util.*;

import javax.validation.constraints.*;
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;

@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2018-09-18T17:06:11.263+05:30")

@Controller
    @RequestMapping("/tl-service")
    public class TradeLicenseController {

        private final ObjectMapper objectMapper;

        private final HttpServletRequest request;

        private final TradeLicenseService tradeLicenseService;

        @Autowired
        public TradeLicenseController(ObjectMapper objectMapper, HttpServletRequest request,
                                      TradeLicenseService tradeLicenseService) {
            this.objectMapper = objectMapper;
            this.request = request;
            this.tradeLicenseService = tradeLicenseService;
        }

        @RequestMapping(value="/v1/_create", method = RequestMethod.POST)
        public ResponseEntity<TradeLicenseResponse> create(@Valid @RequestBody TradeLicenseRequest tradeLicenseRequest)
        {       tradeLicenseService.create(tradeLicenseRequest);
                return new ResponseEntity<TradeLicenseResponse>(HttpStatus.NOT_IMPLEMENTED);
        }

        @RequestMapping(value="/v1/_search", method = RequestMethod.POST)
        public ResponseEntity<TradeLicenseResponse> v1SearchPost(@NotNull @ApiParam(value = "Unique id for a tenant.", required = true) @Valid @RequestParam(value = "tenantId", required = true) String tenantId,@ApiParam(value = "Parameter to carry Request metadata in the request body"  )  @Valid @RequestBody RequestInfo requestInfo,@ApiParam(value = "Page number", defaultValue = "1") @Valid @RequestParam(value = "pageNumber", required = false, defaultValue="1") Integer pageNumber,@ApiParam(value = "Search based on status.") @Valid @RequestParam(value = "status", required = false) Boolean status,@Size(max=50) @ApiParam(value = "unique identifier of trade licence") @Valid @RequestParam(value = "ids", required = false) List<Long> ids,@Size(min=2,max=64) @ApiParam(value = "Unique application number for a trade license application.") @Valid @RequestParam(value = "applicationNumber", required = false) String applicationNumber,@Size(min=2,max=64) @ApiParam(value = "The unique  license number  for a Trade license.") @Valid @RequestParam(value = "licenseNumber", required = false) String licenseNumber,@Size(min=2,max=64) @ApiParam(value = "The unique  Old license number for a Trade license.") @Valid @RequestParam(value = "oldLicenseNumber", required = false) String oldLicenseNumber,@Pattern(regexp="[0-9]") @ApiParam(value = "The mobile number of a Trade owner.") @Valid @RequestParam(value = "mobileNumber", required = false) String mobileNumber) {
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

        @RequestMapping(value="/v1/_update", method = RequestMethod.POST)
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
