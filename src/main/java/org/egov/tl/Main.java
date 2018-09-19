package org.egov.tl;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "org.egov.tl", "org.egov.tl.web.controllers" , "org.egov.tl.config"})
public class org.egov.codegen.Main {


    public static void main(String[] args) throws Exception {
        SpringApplication.run(org.egov.codegen.Main.class, args);
    }

}
