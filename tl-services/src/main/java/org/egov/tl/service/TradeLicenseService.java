package org.egov.tl.service;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.producer.Producer;
import org.egov.tl.repository.TLRepository;
import org.egov.tl.validator.TLValidator;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseRequest;
import org.egov.tl.web.models.TradeLicenseSearchCriteria;
import org.egov.tl.web.models.user.UserDetailResponse;
import org.egov.tl.workflow.ActionValidator;
import org.egov.tl.workflow.TLWorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class TradeLicenseService {

    private EnrichmentService enrichmentService;

    private UserService userService;

    private TLRepository repository;

    private ActionValidator actionValidator;

    private TLValidator tlValidator;

    private TLWorkflowService workflowService;

    private CalculationService calculationService;


    @Autowired
    public TradeLicenseService(EnrichmentService enrichmentService, UserService userService,
                               TLRepository repository, ActionValidator actionValidator,
                               TLValidator tlValidator, TLWorkflowService workflowService,
                               CalculationService calculationService) {
        this.enrichmentService = enrichmentService;
        this.userService = userService;
        this.repository = repository;
        this.actionValidator = actionValidator;
        this.tlValidator = tlValidator;
        this.workflowService = workflowService;
        this.calculationService = calculationService;
    }


    /**
     * creates the tradeLicense for the given request
     * @param tradeLicenseRequest The TradeLicense Create Request
     * @return The list of created traddeLicense
     */
    public List<TradeLicense> create(TradeLicenseRequest tradeLicenseRequest){
        tlValidator.validateCreate(tradeLicenseRequest);
        actionValidator.validateCreateRequest(tradeLicenseRequest);
        enrichmentService.enrichTLCreateRequest(tradeLicenseRequest);
        userService.createUser(tradeLicenseRequest);
        calculationService.addCalculation(tradeLicenseRequest);
        repository.save(tradeLicenseRequest);
        return tradeLicenseRequest.getLicenses();
    }


    /**
     *  Searches the tradeLicense for the given criteria if search is on owner paramter then first user service
     *  is called followed by query to db
     * @param criteria The object containing the paramters on which to search
     * @param requestInfo The search request's requestInfo
     * @return List of tradeLicense for the given criteria
     */
    public List<TradeLicense> search(TradeLicenseSearchCriteria criteria, RequestInfo requestInfo){
        List<TradeLicense> licenses;
        tlValidator.validateSearch(requestInfo,criteria);
        enrichmentService.enrichSearchCriteriaWithAccountId(requestInfo,criteria);
         if(criteria.getMobileNumber()!=null){
             UserDetailResponse userDetailResponse = userService.getUser(criteria,requestInfo);
             // If user not found with given user fields return empty list
             if(userDetailResponse.getUser().size()==0){
                 return Collections.emptyList();
             }
             enrichmentService.enrichTLCriteriaWithOwnerids(criteria,userDetailResponse);
             licenses = repository.getLicenses(criteria);
             // If property not found with given propertyId or oldPropertyId or address fields return empty list
             if(licenses.size()==0){
                 return Collections.emptyList();
             }
             criteria = enrichmentService.getTLSearchCriteriaFromTLIds(licenses);
             licenses = getLicensesWithOwnerInfo(criteria,requestInfo);
         }
         else {
             licenses = getLicensesWithOwnerInfo(criteria,requestInfo);
         }
       return licenses;
    }


    /**
     * Returns the tradeLicense with enrivhed owners from user servise
     * @param criteria The object containing the paramters on which to search
     * @param requestInfo The search request's requestInfo
     * @return List of tradeLicense for the given criteria
     */
    public List<TradeLicense> getLicensesWithOwnerInfo(TradeLicenseSearchCriteria criteria,RequestInfo requestInfo){
        List<TradeLicense> licenses = repository.getLicenses(criteria);
        if(licenses.isEmpty())
            return Collections.emptyList();
        enrichmentService.enrichTLSearchCriteriaWithOwnerids(criteria,licenses);
        enrichmentService.enrichBoundary(new TradeLicenseRequest(requestInfo,licenses));
        UserDetailResponse userDetailResponse = userService.getUser(criteria,requestInfo);
        enrichmentService.enrichOwner(userDetailResponse,licenses);
        return licenses;
    }


    /**
     * Updates the tradeLicenses
     * @param tradeLicenseRequest The update Request
     * @return Updated TradeLcienses
     */
    public List<TradeLicense> update(TradeLicenseRequest tradeLicenseRequest){
        tlValidator.validateUpdate(tradeLicenseRequest);
        actionValidator.validateUpdateRequest(tradeLicenseRequest);
        enrichmentService.enrichTLUpdateRequest(tradeLicenseRequest);
        workflowService.updateStatus(tradeLicenseRequest);
        userService.createUser(tradeLicenseRequest);
        calculationService.addCalculation(tradeLicenseRequest);
        repository.update(tradeLicenseRequest);
        return tradeLicenseRequest.getLicenses();
    }


}
