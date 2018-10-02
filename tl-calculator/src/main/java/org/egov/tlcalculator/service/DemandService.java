package org.egov.tlcalculator.service;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tlcalculator.config.TLCalculatorConfigs;
import org.egov.tlcalculator.repository.ServiceRequestRepository;
import org.egov.tlcalculator.utils.CalculationUtils;
import org.egov.tlcalculator.web.models.*;
import org.egov.tlcalculator.web.models.TL.OwnerInfo;
import org.egov.tlcalculator.web.models.TL.TradeLicense;
import org.egov.tlcalculator.web.models.demand.Demand;
import org.egov.tlcalculator.web.models.demand.DemandDetail;
import org.egov.tlcalculator.web.models.demand.DemandRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;

public class DemandService {



    @Autowired
    private CalculationUtils utils;

    @Autowired
    private TLCalculatorConfigs config;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;




    public List<Demand> generateDemand(RequestInfo requestInfo,CalculationRes response){
        List<Calculation> calculations = response.getCalculation();
        List<Demand> demands = new LinkedList<>();

        if(!CollectionUtils.isEmpty(calculations)){
            calculations.forEach(calculation -> {
                    demands.add(createDemand(requestInfo,calculation));
            });
         StringBuilder url = new StringBuilder(config.getBillingHost());
         url.append(config.getDemandCreateEndpoint());
         DemandRequest request = new DemandRequest(requestInfo,demands);
         serviceRequestRepository.fetchResult(url,request);
        }
        return demands;
    }


    private Demand createDemand(RequestInfo requestInfo,Calculation calculation){

        TradeLicense license = utils.getTradeLicense(requestInfo,calculation.getApplicationNumber()
                ,calculation.getTenantId());

        if(license==null)
            throw new CustomException("INVALID APPLICATIONNUMBER","Demand cannot be generated for applicationNumber " +
                    calculation.getApplicationNumber()+" TradeLicense with this number does not exist ");

        String tenantId = calculation.getTenantId();
        String consumerCode = calculation.getApplicationNumber();

        OwnerInfo owner = license.getTradeLicenseDetail().getOwners().get(0);

        List<DemandDetail> demandDetails = new LinkedList<>();

        calculation.getTaxHeadEstimates().forEach(taxHeadEstimate -> {
            demandDetails.add(DemandDetail.builder().taxAmount(taxHeadEstimate.getEstimateAmount())
                    .taxHeadMasterCode(taxHeadEstimate.getTaxHeadCode())
                    .tenantId(tenantId)
                    .build());
        });

        return Demand.builder()
                .consumerCode(consumerCode)
                .owner(owner)
                .minimumAmountPayable(config.getMinimumPayableAmount())
                .tenantId(tenantId)
                .taxPeriodFrom(license.getValidFrom())
                .taxPeriodTo(license.getValidTo())
                .businessService(config.getBusinessService())
                .build();
    }











}
