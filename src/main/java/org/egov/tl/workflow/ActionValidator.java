package org.egov.tl.workflow;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class ActionValidator {




    public void validateCreateRequest(TradeLicenseRequest request){
        Map<String,String> errorMap = new HashMap<>();

        request.getLicenses().forEach(license -> {
            if(TradeLicense.ActionEnum.INITIATE.equals(license.getAction())){
                if(license.getTradeLicenseDetail().getApplicationDocuments()!=null)
                    errorMap.put("INVALID STATUS","Status should be APPLIED when application document are provided");
            }
            if(TradeLicense.ActionEnum.APPLY.equals(license.getAction())){
                if(license.getTradeLicenseDetail().getApplicationDocuments()==null)
                    errorMap.put("INVALID STATUS","Status cannot be changed to APPLIED. Application document are not provided");
            }
            if(!TradeLicense.ActionEnum.APPLY.equals(license.getAction()) &&
                    !TradeLicense.ActionEnum.INITIATE.equals(license.getAction())){
                errorMap.put("INVALID STATUS","Status can only be APPLY or INITIATE during create");
            }
        });

        if(!errorMap.isEmpty())
            throw new CustomException(errorMap);
    }


    public void validateUpdateRequest(TradeLicenseRequest request){
        validateDocumentsForUpdate(request);
        validateRole(request);
        validateAction(request);
        validateIds(request);
    }

    private void validateDocumentsForUpdate(TradeLicenseRequest request){
        Map<String,String> errorMap = new HashMap<>();
        request.getLicenses().forEach(license -> {
            if(TradeLicense.ActionEnum.INITIATE.equals(license.getAction())){
                if(license.getTradeLicenseDetail().getApplicationDocuments()!=null)
                    errorMap.put("INVALID STATUS","Status cannot be INITIATE when application document are provided");
            }
            if(TradeLicense.ActionEnum.APPLY.equals(license.getAction())){
                if(license.getTradeLicenseDetail().getApplicationDocuments()==null)
                    errorMap.put("INVALID STATUS","Status cannot be APPLY when application document are not provided");
            }
        });

        if(!errorMap.isEmpty())
            throw new CustomException(errorMap);
    }

    private void validateRole(TradeLicenseRequest request){
       Map<String,List<String>> roleActionMap = WorkflowConfig.getRoleActionMap();
       Map<String,String> errorMap = new HashMap<>();
       List<TradeLicense> licenses = request.getLicenses();
       RequestInfo requestInfo = request.getRequestInfo();
       List<Role> roles = requestInfo.getUserInfo().getRoles();

       List<String> actions = new LinkedList<>();
       roles.forEach(role -> {
           if(!roleActionMap.get(role.getCode()).isEmpty())
           {
               actions.addAll(roleActionMap.get(role.getCode()));}
       });

       licenses.forEach(license -> {
          if(!actions.contains(license.getAction().toString()))
              errorMap.put("UNAUTHORIZED UPDATE","The action cannot be performed by this user");
       });
       if(!errorMap.isEmpty())
           throw new CustomException(errorMap);
    }


    private void validateAction(TradeLicenseRequest request){
       Map<String,List<String>> actionStatusMap = WorkflowConfig.getActionCurrentStatusMap();
        Map<String,String> errorMap = new HashMap<>();

        request.getLicenses().forEach(license -> {
           if(actionStatusMap.get(license.getStatus().toString())!=null){
               if(!actionStatusMap.get(license.getStatus().toString()).contains(license.getAction().toString()))
                   errorMap.put("UNAUTHORIZED ACTION","The action "+license.getAction() +" cannot be applied on the status "+license.getStatus());
               }
       });
        if(!errorMap.isEmpty())
            throw new CustomException(errorMap);
    }

    private void validateIds(TradeLicenseRequest request){
        Map<String,String> errorMap = new HashMap<>();
        request.getLicenses().forEach(license -> {
            if(!license.getStatus().equals(TradeLicense.StatusEnum.APPLIED)
                    && !license.getStatus().equals(TradeLicense.StatusEnum.INITIATED)) {
                if (license.getId() == null)
                    errorMap.put("INVALID UPDATE", "Id of tradeLicense cannot be null");
                if(license.getTradeLicenseDetail().getId()==null)
                    errorMap.put("INVALID UPDATE", "Id of tradeLicenseDetail cannot be null");
                if(license.getTradeLicenseDetail().getAddress()==null)
                    errorMap.put("INVALID UPDATE", "Id of address cannot be null");
                license.getTradeLicenseDetail().getOwners().forEach(owner -> {
                    if(owner.getUuid()==null)
                        errorMap.put("INVALID UPDATE", "Id of owner cannot be null");
                    owner.getDocuments().forEach(document -> {
                        if(document.getId()==null)
                            errorMap.put("INVALID UPDATE", "Id of owner document cannot be null");
                    });
                });
                license.getTradeLicenseDetail().getTradeUnits().forEach(tradeUnit -> {
                    if(tradeUnit.getId()==null)
                        errorMap.put("INVALID UPDATE", "Id of tradeUnit cannot be null");
                });
                license.getTradeLicenseDetail().getAccessories().forEach(accessory -> {
                    if(accessory.getId()==null)
                        errorMap.put("INVALID UPDATE", "Id of accessory cannot be null");
                });
                license.getTradeLicenseDetail().getApplicationDocuments().forEach(document -> {
                    if(document.getId()==null)
                        errorMap.put("INVALID UPDATE", "Id of applicationDocument cannot be null");
                });
            }
        });
        if(!errorMap.isEmpty())
            throw new CustomException(errorMap);
    }





}
