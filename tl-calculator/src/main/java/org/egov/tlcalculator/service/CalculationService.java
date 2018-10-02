package org.egov.tlcalculator.service;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tlcalculator.config.TLCalculatorConfigs;
import org.egov.tlcalculator.repository.BillingslabQueryBuilder;
import org.egov.tlcalculator.repository.BillingslabRepository;
import org.egov.tlcalculator.repository.ServiceRequestRepository;
import org.egov.tlcalculator.utils.CalculationUtils;
import org.egov.tlcalculator.web.models.*;
import org.egov.tlcalculator.web.models.TL.TradeLicense;
import org.egov.tlcalculator.web.models.TL.TradeUnit;
import org.egov.tlcalculator.web.models.demand.Category;
import org.egov.tlcalculator.web.models.demand.TaxHeadEstimate;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;



public class CalculationService {


    @Autowired
    private BillingslabRepository repository;

    @Autowired
    private BillingslabQueryBuilder queryBuilder;

    @Autowired
    private TLCalculatorConfigs config;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private CalculationUtils utils;



  public Calculation calculate(RequestInfo requestInfo,CalulationCriteria criteria){
      TradeLicense license;
      if(criteria.getApplicationNumber()!=null)
          license = utils.getTradeLicense(requestInfo,criteria.getApplicationNumber(),criteria.getTenantId());

      Calculation calculation = new Calculation();
      calculation.setApplicationNumber(criteria.getApplicationNumber());
      calculation.setTenantId(criteria.getTenantId());
      calculation.setTaxHeadEstimates(getTaxHeadEstimates(criteria));

      return calculation;
  }

  private List<TaxHeadEstimate> getTaxHeadEstimates(CalulationCriteria calulationCriteria){
      List<TaxHeadEstimate> estimates = new LinkedList<>();

      estimates.add(getBaseTax(calulationCriteria));

      return estimates;
  }



  public TaxHeadEstimate getBaseTax(CalulationCriteria calulationCriteria){
      TradeLicense license = calulationCriteria.getTradelicense();

      List<TradeUnit> tradeUnits = new LinkedList<>();
      List<Accessory> accessories = new LinkedList<>();

      BillingSlabSearchCriteria searchCriteria = new BillingSlabSearchCriteria();
      searchCriteria.setTenantId(license.getTenantId());
      searchCriteria.setStructureType(license.getTradeLicenseDetail().getStructureType());
      searchCriteria.setLicenseType(license.getLicenseType().toString());

      BigDecimal tradeUnitFee = getTradeUnitFee(license,searchCriteria);
      BigDecimal accessoryFee = new BigDecimal(0);

      if(!CollectionUtils.isEmpty(license.getTradeLicenseDetail().getAccessories())){
           accessoryFee = getAccessoryFee(license,searchCriteria);
      }

      TaxHeadEstimate estimate = new TaxHeadEstimate();
      estimate.setEstimateAmount(tradeUnitFee.add(accessoryFee));
      estimate.setCategory(Category.TAX);
      estimate.setTaxHeadCode(config.getBaseTaxHead());
      return estimate;
  }




  private BigDecimal getTradeUnitFee(TradeLicense license,BillingSlabSearchCriteria searchCriteria){
      BigDecimal tradeUnitTotalFee = new BigDecimal(0);

      license.getTradeLicenseDetail().getTradeUnits().forEach(tradeUnit -> {
          List<Object> preparedStmtList = new ArrayList<>();
          searchCriteria.setTradeType(tradeUnit.getTradeType());
          if(tradeUnit.getUomValue()!=null)
          {
              searchCriteria.setUomValue(Double.parseDouble(tradeUnit.getUomValue()));
              searchCriteria.setUom(tradeUnit.getUom());
          }
          // Call the Search
          String query = queryBuilder.getSearchQuery(searchCriteria, preparedStmtList);
          List<BillingSlab> billingSlabs = repository.getDataFromDB(query, preparedStmtList);

          if(billingSlabs.size()>1)
              throw new CustomException("BILLINGSLAB ERROR","Found multiple BillingSlabs for the given calculation critera");
          if(CollectionUtils.isEmpty(billingSlabs))
              throw new CustomException("BILLINGSLAB ERROR","No BillingSlab Found");

          tradeUnitTotalFee.add(new BigDecimal(Double.toString(billingSlabs.get(0).getRate())));
      });

      return tradeUnitTotalFee;
  }


  private BigDecimal getAccessoryFee(TradeLicense license,BillingSlabSearchCriteria searchCriteria){
      BigDecimal accessoryTotalFee = new BigDecimal(0);
      license.getTradeLicenseDetail().getAccessories().forEach(accessory -> {
          List<Object> preparedStmtList = new ArrayList<>();
          searchCriteria.setAccessoryCategory(accessory.getAccessoryCategory());
          if(accessory.getUomValue()!=null)
          {
              searchCriteria.setUomValue(Double.parseDouble(accessory.getUomValue()));
              searchCriteria.setUom(accessory.getUom());
          }
          // Call the Search
          String query = queryBuilder.getSearchQuery(searchCriteria, preparedStmtList);
          List<BillingSlab> billingSlabs = repository.getDataFromDB(query, preparedStmtList);

          if(billingSlabs.size()>1)
              throw new CustomException("BILLINGSLAB ERROR","Found multiple BillingSlabs for the given calculation critera");
          if(CollectionUtils.isEmpty(billingSlabs))
              throw new CustomException("BILLINGSLAB ERROR","No BillingSlab Found");

          accessoryTotalFee.add(new BigDecimal(Double.toString(billingSlabs.get(0).getRate())));
      });

      return accessoryTotalFee;
  }








}
