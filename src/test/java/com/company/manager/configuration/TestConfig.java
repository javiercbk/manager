package com.company.manager.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.company.manager.controllers", "com.company.manager.models", "com.company.manager.repositories"})
public class TestConfig {

}
