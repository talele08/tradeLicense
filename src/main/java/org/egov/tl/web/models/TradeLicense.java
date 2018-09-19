package org.egov.tl.web.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.egov.tl.web.models.AuditDetails;
import org.egov.tl.web.models.TradeLicenseDetail;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

/**
 * A Object holds the basic data for a Trade License
 */
@ApiModel(description = "A Object holds the basic data for a Trade License")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2018-09-18T17:06:11.263+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class  TradeLicense   {
        @JsonProperty("id")
        private String id = null;

        @JsonProperty("tenantId")
        private String tenantId = null;

              /**
   * Unique Identifier of the Trade License (UUID)
   */
  public enum LicenseTypeEnum {
    TEMPORARY("TEMPORARY"),
    
    PERMANENT("PERMANENT");

    private String value;

    LicenseTypeEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static LicenseTypeEnum fromValue(String text) {
      for (LicenseTypeEnum b : LicenseTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

        @JsonProperty("licenseType")
        private LicenseTypeEnum licenseType = null;

        @JsonProperty("licenseNumber")
        private String licenseNumber = null;

        @JsonProperty("oldLicenseNumber")
        private String oldLicenseNumber = null;

        @JsonProperty("propertyId")
        private String propertyId = null;

        @JsonProperty("oldPropertyId")
        private String oldPropertyId = null;

        @JsonProperty("applicationDate")
        private Long applicationDate = null;

        @JsonProperty("commencementDate")
        private Long commencementDate = null;

        @JsonProperty("issuedDate")
        private Long issuedDate = null;

        @JsonProperty("financialYear")
        private String financialYear = null;

        @JsonProperty("validFrom")
        private Long validFrom = null;

        @JsonProperty("validTo")
        private Long validTo = null;

              /**
   * 1. Perform action to change the state of the trade license. 2. INITIATE, if application is getting submitted without required document. 3. APPLY, if application is getting submitted with application documents, in that case api will validate all the required application document. 4. APPROVE action is only applicable for specific role, that role has to be configurable at service level. Employee can approve a application only if application is in APPLIED state and Licesance fees is paid.
   */
  public enum ActionEnum {
    INITIATE("INITIATE"),
    
    APPLY("APPLY"),
    
    APPROVE("APPROVE"),
    
    REJECT("REJECT");

    private String value;

    ActionEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static ActionEnum fromValue(String text) {
      for (ActionEnum b : ActionEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

        @JsonProperty("action")
        private ActionEnum action = null;

              /**
   * Unique identifier (code) of the Trade license Status
   */
  public enum StatusEnum {
    INITIATED("INITIATED"),
    
    APPLIED("APPLIED"),
    
    PAID("PAID"),
    
    APPROVED("APPROVED"),
    
    REJECTED("REJECTED");

    private String value;

    StatusEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static StatusEnum fromValue(String text) {
      for (StatusEnum b : StatusEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

        @JsonProperty("status")
        private StatusEnum status = null;

        @JsonProperty("tradeLicenseDetail")
        private TradeLicenseDetail tradeLicenseDetail = null;

        @JsonProperty("auditDetails")
        private AuditDetails auditDetails = null;


}

