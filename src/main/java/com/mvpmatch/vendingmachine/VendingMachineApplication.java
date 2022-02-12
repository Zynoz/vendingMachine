package com.mvpmatch.vendingmachine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.core.GrantedAuthorityDefaults;

@SpringBootApplication
public class VendingMachineApplication {

    /**
     * Setting the default prefix to be empty for when checking for roles.
     * If this is not done, then spring will check for the roles with ROLE_ prefix.
     * Since we have SELLER and BUYER without the ROLE_ prefix, we need to do this
     */
    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }

    public static void main(String[] args) {
        SpringApplication.run(VendingMachineApplication.class, args);
    }

}
