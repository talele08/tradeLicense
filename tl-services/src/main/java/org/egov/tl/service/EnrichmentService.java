package org.egov.tl.service;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.repository.IdGenRepository;
import org.egov.tl.util.TLConstants;
import org.egov.tl.util.TradeUtil;
import org.egov.tl.web.models.*;
import org.egov.tl.web.models.Idgen.IdResponse;
import org.egov.tl.web.models.user.UserDetailResponse;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EnrichmentService {

    private IdGenRepository idGenRepository;
    private TLConfiguration config;
    private TradeUtil tradeUtil;
    private BoundaryService boundaryService;

    @Autowired
    public EnrichmentService(IdGenRepository idGenRepository, TLConfiguration config, TradeUtil tradeUtil, BoundaryService boundaryService) {
        this.idGenRepository = idGenRepository;
        this.config = config;
        this.tradeUtil = tradeUtil;
        this.boundaryService = boundaryService;
    }


    /**
     * Enriches the incoming createRequest
     * @param tradeLicenseRequest The create request for the tradeLicense
     */
    public void enrichTLCreateRequest(TradeLicenseRequest tradeLicenseRequest) {
        RequestInfo requestInfo = tradeLicenseRequest.getRequestInfo();
        AuditDetails auditDetails = tradeUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
        tradeLicenseRequest.getLicenses().forEach(tradeLicense -> {
            tradeLicense.setAuditDetails(auditDetails);
            tradeLicense.setId(UUID.randomUUID().toString());
            tradeLicense.setApplicationDate(auditDetails.getCreatedTime());
            tradeLicense.getTradeLicenseDetail().setId(UUID.randomUUID().toString());
            tradeLicense.getTradeLicenseDetail().setAuditDetails(auditDetails);

            Map<String,Long> taxPeriods = tradeUtil.getTaxPeriods(requestInfo,tradeLicense);
            if(tradeLicense.getValidTo()==null)
                tradeLicense.setValidTo(taxPeriods.get(TLConstants.MDMS_ENDDATE));
            

            tradeLicense.getTradeLicenseDetail().getAddress().setTenantId(tradeLicense.getTenantId());
            tradeLicense.getTradeLicenseDetail().getAddress().setId(UUID.randomUUID().toString());

            if(!CollectionUtils.isEmpty(tradeLicense.getTradeLicenseDetail().getAccessories()))
                tradeLicense.getTradeLicenseDetail().getAccessories().forEach(accessory -> {
                    accessory.setTenantId(tradeLicense.getTenantId());
                    accessory.setId(UUID.randomUUID().toString());
                    accessory.setActive(true);
                });


            tradeLicense.getTradeLicenseDetail().getTradeUnits().forEach(tradeUnit -> {
                tradeUnit.setTenantId(tradeLicense.getTenantId());
                tradeUnit.setId(UUID.randomUUID().toString());
                tradeUnit.setActive(true);
            });

            if(tradeLicense.getAction().equals(TradeLicense.ActionEnum.APPLY))
            {
                tradeLicense.getTradeLicenseDetail().getApplicationDocuments().forEach(document -> {
                    document.setId(UUID.randomUUID().toString());
                    document.setActive(true);
                });
            }

            tradeLicense.getTradeLicenseDetail().getOwners().forEach(owner -> {
                owner.setUserActive(true);
                if(!CollectionUtils.isEmpty(owner.getDocuments()))
                    owner.getDocuments().forEach(document -> {
                        document.setId(UUID.randomUUID().toString());
                        document.setActive(true);
                    });
            });

            if(tradeLicense.getTradeLicenseDetail().getSubOwnerShipCategory().contains(config.getInstitutional())){
                tradeLicense.getTradeLicenseDetail().getInstitution().setId(UUID.randomUUID().toString());
                tradeLicense.getTradeLicenseDetail().getInstitution().setActive(true);
                tradeLicense.getTradeLicenseDetail().getInstitution().setTenantId(tradeLicense.getTenantId());
                tradeLicense.getTradeLicenseDetail().getOwners().forEach(owner -> {
                    owner.setInstitutionId(tradeLicense.getTradeLicenseDetail().getInstitution().getId());
                });
            }

            if(requestInfo.getUserInfo().getType().equalsIgnoreCase("CITIZEN"))
                tradeLicense.setAccountId(requestInfo.getUserInfo().getUuid());

        });
        setIdgenIds(tradeLicenseRequest);
        setStatusForCreate(tradeLicenseRequest);
        boundaryService.getAreaType(tradeLicenseRequest,config.getHierarchyTypeCode());
    }


    /**
     * Returns a list of numbers generated from idgen
     *
     * @param requestInfo RequestInfo from the request
     * @param tenantId    tenantId of the city
     * @param idKey       code of the field defined in application properties for which ids are generated for
     * @param idformat    format in which ids are to be generated
     * @param count       Number of ids to be generated
     * @return List of ids generated using idGen service
     */
    private List<String> getIdList(RequestInfo requestInfo, String tenantId, String idKey,
                                   String idformat, int count) {
        List<IdResponse> idResponses = idGenRepository.getId(requestInfo, tenantId, idKey, idformat, count).getIdResponses();

        if (CollectionUtils.isEmpty(idResponses))
            throw new CustomException("IDGEN ERROR", "No ids returned from idgen Service");

        return idResponses.stream()
                .map(IdResponse::getId).collect(Collectors.toList());
    }


    /**
     * Sets the ApplicationNumber for given TradeLicenseRequest
     *
     * @param request TradeLicenseRequest which is to be created
     */
    private void setIdgenIds(TradeLicenseRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        String tenantId = request.getLicenses().get(0).getTenantId();
        List<TradeLicense> licenses = request.getLicenses();

        List<String> applicationNumbers = getIdList(requestInfo, tenantId, config.getApplicationNumberIdgenName(), config.getApplicationNumberIdgenFormat(), request.getLicenses().size());
        ListIterator<String> itr = applicationNumbers.listIterator();

        Map<String, String> errorMap = new HashMap<>();
        if (applicationNumbers.size() != request.getLicenses().size()) {
            errorMap.put("IDGEN ERROR ", "The number of LicenseNumber returned by idgen is not equal to number of TradeLicenses");
        }

        if (!errorMap.isEmpty())
            throw new CustomException(errorMap);

        licenses.forEach(tradeLicense -> {
            tradeLicense.setApplicationNumber(itr.next());
        });
    }


    /**
     * Adds the ownerIds from userSearchReponse to search criteria
     * @param criteria The TradeLicense search Criteria
     * @param userDetailResponse The response of user search
     */
    public void enrichTLCriteriaWithOwnerids(TradeLicenseSearchCriteria criteria, UserDetailResponse userDetailResponse){
        if(CollectionUtils.isEmpty(criteria.getOwnerIds())){
            List<String> ownerids = new ArrayList<>();
            userDetailResponse.getUser().forEach(owner -> ownerids.add(owner.getUuid()));
            criteria.setOwnerIds(ownerids);
        }
    }


    /**
     * Creates search criteria of only the tradeLicense ids
     * @param licenses The licenses whose ids are to extracted
     * @return The search criteria
     */
    public TradeLicenseSearchCriteria getTLSearchCriteriaFromTLIds(List<TradeLicense> licenses){
        TradeLicenseSearchCriteria criteria = new TradeLicenseSearchCriteria();
        List<String> ids = new ArrayList<>();
        licenses.forEach(license -> ids.add(license.getId()));
        criteria.setIds(ids);
        criteria.setTenantId(licenses.get(0).getTenantId());
        return criteria;
    }


    /**
     * Enriches search criteria with ownerIds from license
     * @param criteria TradeLicense search criteria
     * @param licenses The tradeLicense whose owners are to be enriched
     */
    public void enrichTLSearchCriteriaWithOwnerids(TradeLicenseSearchCriteria criteria, List<TradeLicense> licenses){
        List<String> ownerids = new ArrayList<>();
        licenses.forEach(license -> {
            license.getTradeLicenseDetail().getOwners().forEach(owner -> ownerids.add(owner.getUuid()));
        });

      /*  licenses.forEach(tradeLicense -> {
            ownerids.add(tradeLicense.getCitizenInfo().getUuid());
            });*/

        criteria.setOwnerIds(ownerids);
    }


    /**
     * Enriches the boundary object in address
     * @param tradeLicenseRequest The create request
     */
    public void enrichBoundary(TradeLicenseRequest tradeLicenseRequest){
        boundaryService.getAreaType(tradeLicenseRequest,config.getHierarchyTypeCode());
    }


    /**
     * Enriches the owner fields from user search response
     * @param userDetailResponse user search response
     * @param licenses licenses whose owners are to be enriches
     */
    public void enrichOwner(UserDetailResponse userDetailResponse, List<TradeLicense> licenses){
        List<OwnerInfo> users = userDetailResponse.getUser();
        Map<String,OwnerInfo> userIdToOwnerMap = new HashMap<>();
        users.forEach(user -> userIdToOwnerMap.put(user.getUuid(),user));
        licenses.forEach(license -> {
            license.getTradeLicenseDetail().getOwners().forEach(owner -> {
                    if(userIdToOwnerMap.get(owner.getUuid())==null)
                        throw new CustomException("OWNER SEARCH ERROR","The owner of the tradeCategoryDetail "+license.getTradeLicenseDetail().getId()+" is not coming in user search");
                    else
                        owner.addUserDetail(userIdToOwnerMap.get(owner.getUuid()));
                 });

           /* if(userIdToOwnerMap.get(license.getCitizenInfo().getUuid())!=null)
                license.getCitizenInfo().addCitizenDetail(userIdToOwnerMap.get(license.getCitizenInfo().getUuid()));
            else
                throw new CustomException("CITIZENINFO ERROR","The citizenInfo of trade License with ApplicationNumber: "+license.getApplicationNumber()+" cannot be found");
*/
        });
    }


    /**
     * Sets status for create request
     * @param tradeLicenseRequest The create request
     */
    private void setStatusForCreate(TradeLicenseRequest tradeLicenseRequest){
        tradeLicenseRequest.getLicenses().forEach(license -> {
            if(license.getAction().equals(TradeLicense.ActionEnum.INITIATE))
                license.setStatus(TradeLicense.StatusEnum.INITIATED);
            if(license.getAction().equals(TradeLicense.ActionEnum.APPLY))
                license.setStatus(TradeLicense.StatusEnum.APPLIED);
        });
    }


    /**
     * Enriches the update request
     * @param tradeLicenseRequest The input update request
     */
    public void enrichTLUpdateRequest(TradeLicenseRequest tradeLicenseRequest){
        RequestInfo requestInfo = tradeLicenseRequest.getRequestInfo();
        AuditDetails auditDetails = tradeUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), false);
        tradeLicenseRequest.getLicenses().forEach(tradeLicense -> {
            tradeLicense.setAuditDetails(auditDetails);
            if(tradeLicense.getAction().equals(TradeLicense.ActionEnum.APPLY)
                    || tradeLicense.getAction().equals(TradeLicense.ActionEnum.INITIATE)) {
                tradeLicense.getTradeLicenseDetail().setAuditDetails(auditDetails);

                if(!CollectionUtils.isEmpty(tradeLicense.getTradeLicenseDetail().getAccessories())){
                    tradeLicense.getTradeLicenseDetail().getAccessories().forEach(accessory -> {
                        if (accessory.getId() == null) {
                            accessory.setTenantId(tradeLicense.getTenantId());
                            accessory.setId(UUID.randomUUID().toString());
                            accessory.setActive(true);
                        }
                    });
                }

                tradeLicense.getTradeLicenseDetail().getTradeUnits().forEach(tradeUnit -> {
                    if (tradeUnit.getId() == null) {
                        tradeUnit.setTenantId(tradeLicense.getTenantId());
                        tradeUnit.setId(UUID.randomUUID().toString());
                        tradeUnit.setActive(true);
                    }
                });

                if (tradeLicense.getAction().equals(TradeLicense.ActionEnum.APPLY)) {
                    tradeLicense.getTradeLicenseDetail().getApplicationDocuments().forEach(document -> {
                        if (document.getId() == null)
                        {document.setId(UUID.randomUUID().toString());
                            document.setActive(true);}
                    });
                }

                tradeLicense.getTradeLicenseDetail().getOwners().forEach(owner -> {
                    if(owner.getUuid()==null || owner.getUserActive()==null)
                        owner.setUserActive(true);
                    if (!CollectionUtils.isEmpty(owner.getDocuments()))
                        owner.getDocuments().forEach(document -> {
                            if (document.getId() == null) {
                                document.setId(UUID.randomUUID().toString());
                                document.setActive(true);
                            }
                        });
                });

                if(tradeLicense.getTradeLicenseDetail().getSubOwnerShipCategory().contains(config.getInstitutional())
                        && tradeLicense.getTradeLicenseDetail().getInstitution().getId()==null){
                    tradeLicense.getTradeLicenseDetail().getInstitution().setId(UUID.randomUUID().toString());
                    tradeLicense.getTradeLicenseDetail().getInstitution().setActive(true);
                    tradeLicense.getTradeLicenseDetail().getInstitution().setTenantId(tradeLicense.getTenantId());
                    tradeLicense.getTradeLicenseDetail().getOwners().forEach(owner -> {
                        owner.setInstitutionId(tradeLicense.getTradeLicenseDetail().getInstitution().getId());
                    });
                }
            }
            else {
                if(!CollectionUtils.isEmpty(tradeLicense.getTradeLicenseDetail().getVerificationDocuments())){
                    tradeLicense.getTradeLicenseDetail().getVerificationDocuments().forEach(document -> {
                        if(document.getId()==null){
                            document.setId(UUID.randomUUID().toString());
                            document.setActive(true);
                        }
                    });
                }
            }
        });
        setLicenseNumber(tradeLicenseRequest);
    }

    /**
     * Sets the licenseNumber generated by idgen
     * @param request The update request
     */
    private void setLicenseNumber(TradeLicenseRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        String tenantId = request.getLicenses().get(0).getTenantId();
        List<TradeLicense> licenses = request.getLicenses();
        int count=0;
        for(TradeLicense license : licenses){
           if(license.getAction().equals(TradeLicense.ActionEnum.APPROVE))
               count++;
        }
        if(count!=0) {
            List<String> licenseNumbers = getIdList(requestInfo, tenantId, config.getLicenseNumberIdgenName(), config.getLicenseNumberIdgenFormat(), count);
            ListIterator<String> itr = licenseNumbers.listIterator();

            Map<String, String> errorMap = new HashMap<>();
            if (licenseNumbers.size() != count) {
                errorMap.put("IDGEN ERROR ", "The number of LicenseNumber returned by idgen is not equal to number of TradeLicenses");
            }

            if (!errorMap.isEmpty())
                throw new CustomException(errorMap);

            licenses.forEach(license -> {
                if (license.getAction().equals(TradeLicense.ActionEnum.APPROVE))
                    license.setLicenseNumber(itr.next());
            });
        }
    }


    /**
     * Adds accountId of the logged in user to search criteria
     * @param requestInfo The requestInfo of searhc request
     * @param criteria The tradeLicenseSearch criteria
     */
    public void enrichSearchCriteriaWithAccountId(RequestInfo requestInfo,TradeLicenseSearchCriteria criteria){
        if(criteria.isEmpty() && requestInfo.getUserInfo().getType().equalsIgnoreCase("CITIZEN"))
            criteria.setAccountId(requestInfo.getUserInfo().getUuid());

    }





}
