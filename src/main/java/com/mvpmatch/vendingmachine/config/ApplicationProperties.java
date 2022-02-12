package com.mvpmatch.vendingmachine.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Vending Machine.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 */
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties {

}
