package org.egov.tl.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.config.TlConfiguration;
import org.egov.tl.repository.ServiceRequestRepository;
import org.egov.tl.web.models.*;
import org.egov.tl.web.models.user.CreateUserRequest;
import org.egov.tl.web.models.user.UserDetailResponse;
import org.egov.tl.web.models.user.UserSearchRequest;
import org.egov.tracer.config.TracerConfiguration;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@Slf4j
@Service
public class UserService{


    private ObjectMapper mapper;

    private ServiceRequestRepository serviceRequestRepository;

    private TlConfiguration config;


    @Autowired
    public UserService(ObjectMapper mapper, ServiceRequestRepository serviceRequestRepository, TlConfiguration config) {
        this.mapper = mapper;
        this.serviceRequestRepository = serviceRequestRepository;
        this.config = config;
    }







    public void createUser(TradeLicenseRequest request){
        List<TradeLicense> licenses = request.getLicenses();
        RequestInfo requestInfo = request.getRequestInfo();
        Role role = getCitizenRole();
        licenses.forEach(tradeLicense -> {
            Set<String> listOfMobileNumbers = getMobileNumbers(tradeLicense.getTradeLicenseDetail().getOwners()
                    ,requestInfo,tradeLicense.getTenantId());
            tradeLicense.getTradeLicenseDetail().getOwners().forEach(owner ->
            {
                if(owner.getUuid()==null)
                    {
                        addUserDefaultFields(tradeLicense.getTenantId(),role,owner);
                        // Checks if the user is already present based on name of the owner and mobileNumber
                        UserDetailResponse userDetailResponse = userExists(owner,requestInfo);
                        // If user not present new user is created
                        if(CollectionUtils.isEmpty(userDetailResponse.getUser()))
                        {   /* Sets userName equal to mobileNumber if mobileNumber already assigned as username
                          random number is assigned as username */
                            StringBuilder uri = new StringBuilder(config.getUserHost()).append(config.getUserContextPath())
                                    .append(config.getUserCreateEndpoint());
                            setUserName(owner,listOfMobileNumbers);

                            userDetailResponse = userCall(new CreateUserRequest(requestInfo,owner),uri);
                            if(userDetailResponse.getUser().get(0).getUuid()==null){
                                throw new CustomException("INVALID USER RESPONSE","The user created has uuid as null");
                            }
                            log.info("owner created --> "+userDetailResponse.getUser().get(0).getUuid());

                        }
                        // Assigns value of fields from user got from userDetailResponse to owner object
                        setOwnerFields(owner,userDetailResponse,requestInfo);
                    }
            });
        });
    }


    private UserDetailResponse userExists(OwnerInfo owner,RequestInfo requestInfo){
        UserSearchRequest userSearchRequest =new UserSearchRequest();
        userSearchRequest.setTenantId(owner.getTenantId());
        userSearchRequest.setMobileNumber(owner.getMobileNumber());
        userSearchRequest.setName(owner.getName());
        userSearchRequest.setRequestInfo(requestInfo);
        userSearchRequest.setActive(true);
        userSearchRequest.setUserType(owner.getType());
        if(owner.getUuid()!=null)
            userSearchRequest.setUuid(Arrays.asList(owner.getUuid()));
        StringBuilder uri = new StringBuilder(config.getUserHost()).append(config.getUserSearchEndpoint());
        return userCall(userSearchRequest,uri);
    }



    private void setUserName(OwnerInfo owner,Set<String> listOfMobileNumber){
        if(listOfMobileNumber.contains(owner.getMobileNumber())){
            owner.setUserName(config.getUsernamePrefix()+owner.getMobileNumber());
            // Once mobileNumber is set as userName it is removed from the list
            listOfMobileNumber.remove(owner.getMobileNumber());
        }
        else {
            String username = UUID.randomUUID().toString();
            owner.setUserName(config.getUsernamePrefix()+username);
        }
    }



    private Set<String> getMobileNumbers(List<OwnerInfo> owners,RequestInfo requestInfo,String tenantId){
        Set<String> listOfMobileNumbers = new HashSet<>();
        owners.forEach(owner -> {listOfMobileNumbers.add(owner.getMobileNumber());});
        StringBuilder uri = new StringBuilder(config.getUserHost()).append(config.getUserSearchEndpoint());
        UserSearchRequest userSearchRequest = new UserSearchRequest();
        userSearchRequest.setRequestInfo(requestInfo);
        userSearchRequest.setTenantId(tenantId);
        userSearchRequest.setUserType("CITIZEN");
        Set<String> availableMobileNumbers = new HashSet<>();

        listOfMobileNumbers.forEach(mobilenumber -> {
            userSearchRequest.setMobileNumber(mobilenumber);
            UserDetailResponse userDetailResponse =  userCall(userSearchRequest,uri);
            if(CollectionUtils.isEmpty(userDetailResponse.getUser()))
                availableMobileNumbers.add(mobilenumber);
        });
        return availableMobileNumbers;
    }


    private void setOwnerFields(OwnerInfo owner, UserDetailResponse userDetailResponse,RequestInfo requestInfo){
        owner.setUuid(userDetailResponse.getUser().get(0).getUuid());
        owner.setId(userDetailResponse.getUser().get(0).getId());
        owner.setUserName((userDetailResponse.getUser().get(0).getUserName()));
        owner.setCreatedBy(requestInfo.getUserInfo().getUuid());
        owner.setCreatedDate(System.currentTimeMillis());
        owner.setLastModifiedBy(requestInfo.getUserInfo().getUuid());
        owner.setLastModifiedDate(System.currentTimeMillis());
        owner.setActive(userDetailResponse.getUser().get(0).getActive());
    }


    /**
     * Sets the role,type,active and tenantId for a Citizen
     * @param tenantId TenantId of the property
     * @param role The role of the user set in this case to CITIZEN
     * @param owner The user whose fields are to be set
     */
    private void addUserDefaultFields(String tenantId, Role role, OwnerInfo owner){
        owner.setActive(true);
        owner.setTenantId(tenantId);
        owner.setRoles(Collections.singletonList(role));
        owner.setType("CITIZEN");
    }

    private Role getCitizenRole(){
        Role role = new Role();
        role.setCode("CITIZEN");
        role.setName("Citizen");
        return role;
    }



    /**
     * Returns UserDetailResponse by calling user service with given uri and object
     * @param userRequest Request object for user service
     * @param uri The address of the endpoint
     * @return Response from user service as parsed as userDetailResponse
     */
    private UserDetailResponse userCall(Object userRequest, StringBuilder uri) {
        String dobFormat = null;
        if(uri.toString().contains(config.getUserSearchEndpoint()) || uri.toString().contains(config.getUserUpdateEndpoint()))
            dobFormat="yyyy-MM-dd";
        else if(uri.toString().contains(config.getUserCreateEndpoint()))
            dobFormat = "dd/MM/yyyy";
        try{
            LinkedHashMap responseMap = (LinkedHashMap)serviceRequestRepository.fetchResult(uri, userRequest);
            parseResponse(responseMap,dobFormat);
            UserDetailResponse userDetailResponse = mapper.convertValue(responseMap,UserDetailResponse.class);
            return userDetailResponse;
        }
        // Which Exception to throw?
        catch(IllegalArgumentException  e)
        {
            throw new CustomException("IllegalArgumentException","ObjectMapper not able to convertValue in userCall");
        }
    }



    /**
     * Parses date formats to long for all users in responseMap
     * @param responeMap LinkedHashMap got from user api response
     * @param dobFormat dob format (required because dob is returned in different format's in search and create response in user service)
     */
    private void parseResponse(LinkedHashMap responeMap,String dobFormat){
        List<LinkedHashMap> users = (List<LinkedHashMap>)responeMap.get("user");
        String format1 = "dd-MM-yyyy HH:mm:ss";
        if(users!=null){
            users.forEach( map -> {
                        map.put("createdDate",dateTolong((String)map.get("createdDate"),format1));
                        if((String)map.get("lastModifiedDate")!=null)
                            map.put("lastModifiedDate",dateTolong((String)map.get("lastModifiedDate"),format1));
                        if((String)map.get("dob")!=null)
                            map.put("dob",dateTolong((String)map.get("dob"),dobFormat));
                        if((String)map.get("pwdExpiryDate")!=null)
                            map.put("pwdExpiryDate",dateTolong((String)map.get("pwdExpiryDate"),format1));
                    }
            );
        }
    }

    /**
     * Converts date to long
     * @param date date to be parsed
     * @param format Format of the date
     * @return Long value of date
     */
    private Long dateTolong(String date,String format){
        SimpleDateFormat f = new SimpleDateFormat(format);
        Date d = null;
        try {
            d = f.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  d.getTime();
    }



    public UserDetailResponse getUser(TradeLicenseSearchCriteria criteria,RequestInfo requestInfo){
        UserSearchRequest userSearchRequest = getUserSearchRequest(criteria,requestInfo);
        StringBuilder uri = new StringBuilder(config.getUserHost()).append(config.getUserSearchEndpoint());
        UserDetailResponse userDetailResponse = userCall(userSearchRequest,uri);
        return userDetailResponse;
    }


    private UserSearchRequest getUserSearchRequest(TradeLicenseSearchCriteria criteria, RequestInfo requestInfo){
        UserSearchRequest userSearchRequest = new UserSearchRequest();
        userSearchRequest.setRequestInfo(requestInfo);
        userSearchRequest.setTenantId(criteria.getTenantId());
        userSearchRequest.setMobileNumber(criteria.getMobileNumber());
        userSearchRequest.setActive(true);
        userSearchRequest.setUserType("CITIZEN");
        if(!CollectionUtils.isEmpty(criteria.getOwnerids()))
            userSearchRequest.setUuid(criteria.getOwnerids());
        return userSearchRequest;
    }


    public void createCitizen(TradeLicenseRequest request){
        StringBuilder uriCreate = new StringBuilder(config.getUserHost()).append(config.getUserHost()).append(config.getUserCreateEndpoint());
        RequestInfo requestInfo = request.getRequestInfo();

        Role role = getCitizenRole();
        // If user is creating assessment, userInfo object from requestInfo is assigned as citizenInfo
        if(requestInfo.getUserInfo().getType().equalsIgnoreCase("CITIZEN"))
        {   request.getLicenses().forEach(license -> {
                license.setCitizenInfo(new OwnerInfo(requestInfo.getUserInfo()));
                log.debug("userInfo---> "+requestInfo.getUserInfo().toString());
           });
        }
        else{
            // In case of employee login it checks if the citizenInfo object is present else it creates it
            request.getLicenses().forEach(license -> {
                    addUserDefaultFields(license.getTenantId(),role,license.getCitizenInfo());
                    // Send MobileNumber as the userName in search
                    UserDetailResponse userDetailResponse = searchByUserName(license.getCitizenInfo().getMobileNumber(),license.getCitizenInfo().getTenantId());
                    // If user not present new user is created
                    if(CollectionUtils.isEmpty(userDetailResponse.getUser()))
                    {
                        license.getCitizenInfo().setUserName(license.getCitizenInfo().getMobileNumber());
                        userDetailResponse = userCall(new CreateUserRequest(requestInfo,license.getCitizenInfo()),uriCreate);
                        log.info("citizen created --> "+userDetailResponse.getUser().get(0).getUuid());
                    }
                    license.setCitizenInfo(userDetailResponse.getUser().get(0));
                    if(userDetailResponse.getUser().get(0).getUuid()==null){
                        throw new CustomException("INVALID CITIZENINFO","CitizenInfo cannot have uuid equal to null");
                    }
                });
            }

    }


    private UserDetailResponse searchByUserName(String userName,String tenantId){
        UserSearchRequest userSearchRequest = new UserSearchRequest();
        userSearchRequest.setUserType("CITIZEN");
        userSearchRequest.setUserName(userName);
        userSearchRequest.setTenantId(tenantId);
        StringBuilder uri = new StringBuilder(config.getUserHost()).append(config.getUserSearchEndpoint());
        return userCall(userSearchRequest,uri);

    }


    /**
     * Updates user if present else creates new user
     * @param request TradeLicenseRequest received from update
     */
    public void updateUser(TradeLicenseRequest request){
        List<TradeLicense> licenses = request.getLicenses();
        RequestInfo requestInfo = request.getRequestInfo();
        licenses.forEach(license -> {
                license.getTradeLicenseDetail().getOwners().forEach(owner -> {
                    UserDetailResponse userDetailResponse = userExists(owner,requestInfo);
                    StringBuilder uri  = new StringBuilder(config.getUserHost());
                    if(CollectionUtils.isEmpty(userDetailResponse.getUser())) {
                        uri = uri.append(config.getUserContextPath()).append(config.getUserCreateEndpoint());
                        userDetailResponse = userCall( new CreateUserRequest(requestInfo,owner),uri);
                    }
                    else
                    {   owner.setUuid(userDetailResponse.getUser().get(0).getUuid());
                        uri=uri.append(config.getUserContextPath()).append(config.getUserUpdateEndpoint());
                        OwnerInfo user = new OwnerInfo();
                        user.addUserWithoutAuditDetail(owner);
                        userDetailResponse = userCall( new CreateUserRequest(requestInfo,user),uri);
                    }
                    setOwnerFields(owner,userDetailResponse,requestInfo);
                });
            });
    }





}
