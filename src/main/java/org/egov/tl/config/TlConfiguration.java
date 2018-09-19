package org.egov.tl.config;

import lombok.*;
import org.egov.tracer.config.TracerConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;


@Import({TracerConfiguration.class})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Component
public class TlConfiguration {

    // User Config
    @Value("{egov.user.host}")
    private String userHost;

    @Value("{egov.user.context.path}")
    private String userContextPath;

    @Value("{egov.user.create.path}")
    private String userCreateEndpoint;

    @Value("{egov.user.search.path}")
    private String userSearchEndpoint;

    @Value("{egov.user.update.path}")
    private String userUpdateEndpoint;

    @Value("{egov.user.username.prefix}")
    private String usernamePrefix;


    //Idgen Config
    @Value("{egov.idgen.tl.licensenumber.name}")
    private String licenseNumberIdgenName;

    @Value("{egov.idgen.tl.licensenumber.format}")
    private String licenseNumberIdgenFormat;


    //Persister Config
    @Value("{persister.save.tradelicense.topic}")
    private String saveTopic;

    @Value("{persister.update.tradelicense.topic}")
    private String updateTopic;


    //Location Config
    @Value("{egov.location.host}")
    private String locationHost;

    @Value("{egov.location.context.path}")
    private String locationContextPath;

    @Value("{egov.location.endpoint}")
    private String locationEndpoint;

    @Value("{egov.location.hierarchyTypeCode}")
    private String hierarchyTypeCode;





}
