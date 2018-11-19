package org.egov.tl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.tl.web.models.TradeLicenseResponse;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class testing {


    @Test
    public void name() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.convertValue(null,TradeLicenseResponse.class);

        Set<String> mobileNumbers = new HashSet<>();

        mobileNumbers.add(null);
        System.out.println(mobileNumbers.toString());

            LocalDateTime localDateTime = LocalDateTime.now();
            LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
            LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);


        System.out.println("startTime: "+startOfDay+" endTime: "+endOfDay);



        Calendar cal = Calendar.getInstance();
        cal. set (Calendar .HOUR_OF_DAY, 0);
        cal. set (Calendar .MINUTE, 0);
        cal. set (Calendar .SECOND, 0);
        cal.set(Calendar.MILLISECOND,0);

        System.out.println("calender: "+cal.getTimeInMillis());


    }
}
