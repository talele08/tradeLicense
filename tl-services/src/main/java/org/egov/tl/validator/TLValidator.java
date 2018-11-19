package org.egov.tl.validator;

import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.repository.TLRepository;
import org.egov.tl.service.TradeLicenseService;
import org.egov.tl.util.TLConstants;
import org.egov.tl.util.TradeUtil;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseRequest;
import org.egov.tl.web.models.TradeLicenseSearchCriteria;
import org.egov.tl.web.models.TradeUnit;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Component
public class TLValidator {


    private TLRepository tlRepository;

    private TLConfiguration config;

    private PropertyValidator propertyValidator;

    private MDMSValidator mdmsValidator;

    private TradeUtil tradeUtil;


    @Autowired
    public TLValidator(TLRepository tlRepository, TLConfiguration config, PropertyValidator propertyValidator,
                       MDMSValidator mdmsValidator, TradeUtil tradeUtil) {
        this.tlRepository = tlRepository;
        this.config = config;
        this.propertyValidator = propertyValidator;
        this.mdmsValidator = mdmsValidator;
        this.tradeUtil = tradeUtil;
    }


    /**
     *  Validate the create Requesr
     * @param request The input TradeLicenseRequest Object
     */
    public void validateCreate(TradeLicenseRequest request){
        valideDates(request);
        validateInstitution(request);
        propertyValidator.validateProperty(request);
        mdmsValidator.validateMdmsData(request);
    }


    /**
     *  Validates the fromDate and toDate of the request
     * @param request The input TradeLicenseRequest Object
     */
    private void valideDates(TradeLicenseRequest request){
        request.getLicenses().forEach(license -> {
            Map<String,Long> taxPeriods = tradeUtil.getTaxPeriods(request.getRequestInfo(),license);
            if(license.getValidTo()!=null && license.getValidTo()>taxPeriods.get(TLConstants.MDMS_ENDDATE)){
                Date expiry = new Date(license.getValidTo());
                throw new CustomException("INVALID TO DATE"," Validto cannot be greater than: "+expiry);
            }
            if(license.getLicenseType().toString().equalsIgnoreCase(TradeLicense.LicenseTypeEnum.TEMPORARY.toString())) {
                Long startOfDay = getStartOfDay();
                if (!config.getIsPreviousTLAllowed() && license.getValidFrom() != null
                        && license.getValidFrom() < startOfDay)
                    throw new CustomException("INVALID FROM DATE", "The validFrom date cannot be less than CurrentDate");
                if ((license.getValidFrom() != null && license.getValidTo() != null) && (license.getValidTo() - license.getValidFrom()) < config.getMinPeriod())
                    throw new CustomException("INVALID PERIOD", "The license should be applied for minimum of 30 days");
            }
        });
    }

    /**
     * Returns the start of the current day in millis
     * @return time in millis
     */
    private Long getStartOfDay(){
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("IST"));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND,0);
        return cal.getTimeInMillis();
    }


    /**
     *  Validates the details if subOwnersipCategory is institutional
     * @param request The input TradeLicenseRequest Object
     */
    private void validateInstitution(TradeLicenseRequest request){
        List<TradeLicense> licenses = request.getLicenses();
        licenses.forEach(license -> {
            if(license.getTradeLicenseDetail().getInstitution()!=null &&
                    !license.getTradeLicenseDetail().getSubOwnerShipCategory().contains(config.getInstitutional()))
                throw new CustomException("INVALID REQUEST","The institution object should be null for ownershipCategory "
                        +license.getTradeLicenseDetail().getSubOwnerShipCategory());

            if(license.getTradeLicenseDetail().getInstitution()==null &&
                    license.getTradeLicenseDetail().getSubOwnerShipCategory().contains(config.getInstitutional()))
                throw new CustomException("INVALID REQUEST","The institution object cannot be null for ownershipCategory "
                        +license.getTradeLicenseDetail().getSubOwnerShipCategory());

        });
    }


    /**
     *  Validates the update request
     * @param request The input TradeLicenseRequest Object
     */
    public void validateUpdate(TradeLicenseRequest request){
      List<TradeLicense> licenses = request.getLicenses();

      Map<String,List<String>> tenantIdToIds= new HashMap<>();

      // Map of tenantId to tradeLicenses created
      request.getLicenses().forEach(license -> {
          if(tenantIdToIds.containsKey(license.getTenantId()))
              tenantIdToIds.get(license.getTenantId()).add(license.getId());
          else {
              List<String> perTenantIds = new LinkedList<>();
              perTenantIds.add(license.getId());
              tenantIdToIds.put(license.getTenantId(),perTenantIds);
          }
      });

      List<TradeLicense> searchResult = new LinkedList<>();
        tenantIdToIds.keySet().forEach(key -> {
                addTradeLicenseFromSearch(key,tenantIdToIds.get(key),searchResult);
      });

      if(searchResult.size()!=licenses.size())
          throw new CustomException("INVALID UPDATE","The license to be updated is not in database");

      validateAllIds(searchResult,licenses);
      mdmsValidator.validateMdmsData(request);
      validateTradeUnits(request);
      valideDates(request);
      setFieldsFromSearch(request,searchResult);
   }


    /**
     * Validates that atleast one tradeUnit is active equal true or new tradeUnit
     * @param request The input TradeLicenseRequest Object
     */
   private void validateTradeUnits(TradeLicenseRequest request){
        Map<String,String> errorMap = new HashMap<>();
        List<TradeLicense> licenses = request.getLicenses();

        for(TradeLicense license : licenses)
        {
            Boolean flag = false;
            List<TradeUnit> units = license.getTradeLicenseDetail().getTradeUnits();
            for(TradeUnit unit : units) {
                if(unit.getId()!=null && unit.getActive())
                    flag = true;
                else if(unit.getId()==null)
                    flag = true;
            }
            if(!flag)
                errorMap.put("INVALID UPDATE","All TradeUnits are inactive in the tradeLicense: "+license.getApplicationNumber());
        }

        if(!errorMap.isEmpty())
            throw new CustomException(errorMap);
   }


    /**
     *  Adds tradeLicense for the particular tenantId in the list
     * @param tenantId tenantId of the license
     * @param ids ids of licenses with the given tenantId
     * @param searchResult The list containing multitenant licenses
     */
   private void addTradeLicenseFromSearch(String tenantId,List<String> ids,
                                          List<TradeLicense> searchResult){
         TradeLicenseSearchCriteria criteria = new TradeLicenseSearchCriteria();
         criteria.setTenantId(tenantId);
         criteria.setIds(ids);
         searchResult.addAll(tlRepository.getLicenses(criteria));
   }


    /**
     * Returns the list of ids of all owners as list for the given tradelicense
     * @param license TradeLicense whose ownerIds are to be extracted
     * @return list od OwnerIds
     */
    private List<String> getOwnerIds(TradeLicense license){
        List<String> ownerIds = new LinkedList<>();
        if(!CollectionUtils.isEmpty(license.getTradeLicenseDetail().getOwners())){
            license.getTradeLicenseDetail().getOwners().forEach(owner -> {
                if(owner.getUserActive()!=null)
                    ownerIds.add(owner.getUuid());
            });
        }
        return ownerIds;
    }

    /**
     * Returns the list of ids of all tradeUnits as list for the given tradelicense
     * @param license TradeLicense whose tradeUnitIds are to be extracted
     * @return list od tradeUnitIdss
     */
    private List<String> getTradeUnitIds(TradeLicense license){
        List<String> tradeUnitIds = new LinkedList<>();
        if(!CollectionUtils.isEmpty(license.getTradeLicenseDetail().getTradeUnits())){
            license.getTradeLicenseDetail().getTradeUnits().forEach(tradeUnit -> {
                tradeUnitIds.add(tradeUnit.getId());
            });
        }
        return tradeUnitIds;
    }

    /**
     * Returns the list of ids of all accessories as list for the given tradelicense
     * @param license TradeLicense whose accessoryIds are to be extracted
     * @return list od accessoryIds
     */
    private List<String> getAccessoryIds(TradeLicense license){
        List<String> accessoryIds = new LinkedList<>();
        if(!CollectionUtils.isEmpty(license.getTradeLicenseDetail().getAccessories())){
            license.getTradeLicenseDetail().getAccessories().forEach(accessory -> {
                accessoryIds.add(accessory.getId());
            });
        }
        return accessoryIds;
    }

    /**
     * Returns the list of ids of all ownerDocs as list for the given tradelicense
     * @param license TradeLicense whose ownerDocIds are to be extracted
     * @return list od ownerDocIds
     */
    private List<String> getOwnerDocIds(TradeLicense license){
        List<String> ownerDocIds = new LinkedList<>();
        if(!CollectionUtils.isEmpty(license.getTradeLicenseDetail().getOwners())){
            license.getTradeLicenseDetail().getOwners().forEach(owner -> {
                if(!CollectionUtils.isEmpty(owner.getDocuments())){
                    owner.getDocuments().forEach(document -> {
                        ownerDocIds.add(document.getId());
                    });
                }
            });
        }
        return ownerDocIds;
    }

    /**
     * Returns the list of ids of all applicationDoc as list for the given tradelicense
     * @param license TradeLicense whose applicationDocIds are to be extracted
     * @return list od applicationDocIds
     */
    private List<String> getApplicationDocIds(TradeLicense license){
        List<String> applicationDocIds = new LinkedList<>();
        if(!CollectionUtils.isEmpty(license.getTradeLicenseDetail().getApplicationDocuments())){
            license.getTradeLicenseDetail().getApplicationDocuments().forEach(document -> {
                applicationDocIds.add(document.getId());
            });
        }
        return applicationDocIds;
    }

    /**
     * Returns the list of ids of all verficationDoc as list for the given tradelicense
     * @param license TradeLicense whose VerficationDocIds are to be extracted
     * @return list od VerficationDocIds
     */
    private List<String> getVerficationDocIds(TradeLicense license){
        List<String> verficationDocIds = new LinkedList<>();
        if(!CollectionUtils.isEmpty(license.getTradeLicenseDetail().getVerificationDocuments())) {
            license.getTradeLicenseDetail().getVerificationDocuments().forEach(document -> {
                verficationDocIds.add(document.getId());
            });
        }
        return verficationDocIds;
    }


    /**
     * Enriches the immutable fields from database
     * @param request The input TradeLicenseRequest
     * @param searchResult The list of searched licenses
     */
    private void setFieldsFromSearch(TradeLicenseRequest request,List<TradeLicense> searchResult){
        Map<String,TradeLicense> idToTradeLicenseFromSearch = new HashMap<>();
        searchResult.forEach(tradeLicense -> {
            idToTradeLicenseFromSearch.put(tradeLicense.getId(),tradeLicense);
        });
        request.getLicenses().forEach(license -> {
            license.setStatus(idToTradeLicenseFromSearch.get(license.getId()).getStatus());
            license.setLicenseNumber(idToTradeLicenseFromSearch.get(license.getId()).getLicenseNumber());
        });
    }

    /**
     * Validates if all ids are same as obtained from search result
     * @param searchResult The license from search
     * @param licenses The licenses from the update Request
     */
    private void validateAllIds(List<TradeLicense> searchResult,List<TradeLicense> licenses){

        Map<String,TradeLicense> idToTradeLicenseFromSearch = new HashMap<>();
        searchResult.forEach(tradeLicense -> {
            idToTradeLicenseFromSearch.put(tradeLicense.getId(),tradeLicense);
        });

        Map<String,String> errorMap = new HashMap<>();
        licenses.forEach(license -> {
            TradeLicense searchedLicense = idToTradeLicenseFromSearch.get(license.getId());
            if(!searchedLicense.getTradeLicenseDetail().getId().
                    equalsIgnoreCase(license.getTradeLicenseDetail().getId()))
                errorMap.put("INVALID UPDATE","The id "+license.getTradeLicenseDetail().getId()+" does not exist");

            if(!searchedLicense.getTradeLicenseDetail().getAddress().getId().
                    equalsIgnoreCase(license.getTradeLicenseDetail().getAddress().getId()))
                errorMap.put("INVALID UPDATE","The id "+license.getTradeLicenseDetail().getAddress().getId()+" does not exist");

            compareIdList(getTradeUnitIds(searchedLicense),getTradeUnitIds(license),errorMap);
            compareIdList(getAccessoryIds(searchedLicense),getAccessoryIds(license),errorMap);
            compareIdList(getOwnerIds(searchedLicense),getOwnerIds(license),errorMap);
            compareIdList(getOwnerDocIds(searchedLicense),getOwnerDocIds(license),errorMap);
            compareIdList(getApplicationDocIds(searchedLicense),getApplicationDocIds(license),errorMap);
            compareIdList(getVerficationDocIds(searchedLicense),getVerficationDocIds(license),errorMap);
        });

        if(!CollectionUtils.isEmpty(errorMap))
            throw new CustomException(errorMap);
    }


    /**
     * Checks if the ids are present in the searchedIds
     * @param searchIds Ids got from search
     * @param updateIds The ids received from update Request
     * @param errorMap The map for collecting errors
     */
    private void compareIdList(List<String> searchIds,List<String> updateIds,Map<String,String> errorMap){
        if(!CollectionUtils.isEmpty(searchIds))
            updateIds.forEach(id -> {
                if(id!=null && !searchIds.contains(id))
                    errorMap.put("INVALID UPDATE","The id "+id+" does not exist in database");
            });
    }


    /**
     * Validates if the search parameters are valid
     * @param requestInfo The requestInfo of the incoming request
     * @param criteria The TradeLicenseSearch Criteria
     */
    public void validateSearch(RequestInfo requestInfo,TradeLicenseSearchCriteria criteria){
        if(!requestInfo.getUserInfo().getType().equalsIgnoreCase("CITIZEN" )&& criteria.isEmpty())
            throw new CustomException("INVALID SEARCH","Search without any paramters is not allowed");

        if(!requestInfo.getUserInfo().getType().equalsIgnoreCase("CITIZEN" )&& criteria.tenantIdOnly())
            throw new CustomException("INVALID SEARCH","Search based only on tenantId is not allowed");

        if(!requestInfo.getUserInfo().getType().equalsIgnoreCase("CITIZEN" )&& !criteria.tenantIdOnly()
                && criteria.getTenantId()==null)
            throw new CustomException("INVALID SEARCH","TenantId is mandatory in search");

        if(requestInfo.getUserInfo().getType().equalsIgnoreCase("CITIZEN" )&& criteria.tenantIdOnly())
            throw new CustomException("INVALID SEARCH","Search only on tenantId is not allowed");

        String allowedParamStr = null;

        if(requestInfo.getUserInfo().getType().equalsIgnoreCase("CITIZEN" ))
            allowedParamStr = config.getAllowedCitizenSearchParameters();
        else if(requestInfo.getUserInfo().getType().equalsIgnoreCase("EMPLOYEE" ))
            allowedParamStr = config.getAllowedEmployeeSearchParameters();
        else throw new CustomException("INVALID SEARCH","The userType: "+requestInfo.getUserInfo().getType()+
                    " does not have any search config");

        if(StringUtils.isEmpty(allowedParamStr) && !criteria.isEmpty())
            throw new CustomException("INVALID SEARCH","No search parameters are expected");
        else {
            List<String> allowedParams = Arrays.asList(allowedParamStr.split(","));
            validateSearchParams(criteria, allowedParams);
        }
    }


    /**
     * Validates if the paramters coming in search are allowed
     * @param criteria TradeLicense search criteria
     * @param allowedParams Allowed Params for search
     */
    private void validateSearchParams(TradeLicenseSearchCriteria criteria,List<String> allowedParams){

        if(criteria.getApplicationNumber()!=null && !allowedParams.contains("applicationNumber"))
            throw new CustomException("INVALID SEARCH","Search on applicationNumber is not allowed");

        if(criteria.getTenantId()!=null && !allowedParams.contains("tenantId"))
            throw new CustomException("INVALID SEARCH","Search on tenantId is not allowed");

        if(criteria.getToDate()!=null && !allowedParams.contains("toDate"))
            throw new CustomException("INVALID SEARCH","Search on toDate is not allowed");

        if(criteria.getFromDate()!=null && !allowedParams.contains("fromDate"))
            throw new CustomException("INVALID SEARCH","Search on fromDate is not allowed");

        if(criteria.getStatus()!=null && !allowedParams.contains("status"))
            throw new CustomException("INVALID SEARCH","Search on Status is not allowed");

        if(criteria.getIds()!=null && !allowedParams.contains("ids"))
            throw new CustomException("INVALID SEARCH","Search on ids is not allowed");

        if(criteria.getMobileNumber()!=null && !allowedParams.contains("mobileNumber"))
            throw new CustomException("INVALID SEARCH","Search on mobileNumber is not allowed");

        if(criteria.getLicenseNumber()!=null && !allowedParams.contains("licenseNumber"))
            throw new CustomException("INVALID SEARCH","Search on licenseNumber is not allowed");

        if(criteria.getOldLicenseNumber()!=null && !allowedParams.contains("oldLicenseNumber"))
            throw new CustomException("INVALID SEARCH","Search on oldLicenseNumber is not allowed");

        if(criteria.getOffset()!=null && !allowedParams.contains("offset"))
            throw new CustomException("INVALID SEARCH","Search on offset is not allowed");

        if(criteria.getLimit()!=null && !allowedParams.contains("limit"))
            throw new CustomException("INVALID SEARCH","Search on limit is not allowed");

    }



}


