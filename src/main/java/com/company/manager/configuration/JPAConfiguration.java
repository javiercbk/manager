package com.company.manager.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration 
@ComponentScan("com.company.manager")
@EnableTransactionManagement
public class JPAConfiguration {
 
	@Bean
	public JpaTransactionManager jpaTransMan(){
		JpaTransactionManager transactionManager = new JpaTransactionManager(getEntityManagerFactoryBean().getObject());
		return transactionManager;
	}
	@Bean
	public LocalEntityManagerFactoryBean getEntityManagerFactoryBean() {
		LocalEntityManagerFactoryBean lemfb = new LocalEntityManagerFactoryBean();
		lemfb.setPersistenceUnitName("dev");
		return lemfb;
	}
}