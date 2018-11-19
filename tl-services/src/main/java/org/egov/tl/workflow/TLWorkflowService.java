package org.egov.tl.workflow;

import org.egov.tl.config.TLConfiguration;
import org.egov.tl.producer.Producer;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class TLWorkflowService {

    private ActionValidator actionValidator;
    private Producer producer;
    private TLConfiguration config;
    private WorkflowConfig workflowConfig;

    @Autowired
    public TLWorkflowService(ActionValidator actionValidator, Producer producer, TLConfiguration config,WorkflowConfig workflowConfig) {
        this.actionValidator = actionValidator;
        this.producer = producer;
        this.config = config;
        this.workflowConfig = workflowConfig;
    }


    /**
     * Validates and updates the status
     * @param request The update tradeLicense Request
     */
    public void updateStatus(TradeLicenseRequest request){
        actionValidator.validateUpdateRequest(request);
        changeStatus(request);
    }


    /**
     * Changes the status of the tradeLicense according to action status mapping
     * @param request The update tradeLicenseRequest
     */
    private void changeStatus(TradeLicenseRequest request){
       Map<String,String> actionToStatus =  workflowConfig.getActionStatusMap();
       request.getLicenses().forEach(license -> {
             license.setStatus(TradeLicense.StatusEnum.valueOf(actionToStatus.get(license.getAction().toString())));
             if(license.getAction().toString().equalsIgnoreCase(TradeLicense.ActionEnum.APPROVE.toString())){
                 Long time = System.currentTimeMillis();
                 license.setIssuedDate(time);
                 license.setValidFrom(time);
             }
       });
    }

}
