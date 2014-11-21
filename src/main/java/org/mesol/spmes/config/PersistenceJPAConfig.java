/*
 * Copyright 2014 Mes Solutions.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mesol.spmes.config;

import java.lang.invoke.MethodHandles;
import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import org.mesol.spmes.model.security.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.Assert;

/**
 * $Rev:$ $Author:$ $Date:$
 *
 * @version 1.0.0
 * @author ASementsov
 */
@Configuration
@EnableTransactionManagement
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = {"org.mesol.spmes.repo"})
@PropertySource("classpath:/" + PersistenceJPAConfig.CONFIG)
public class PersistenceJPAConfig 
{
    public static final String CONFIG = "ds/jdbc.properties";
    private static final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass());

    public static String getRevisionNumber() {
        return "$Revision:$";
    }
    
    @Autowired
    private Environment                             env;

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return null;
            }

            return ((User) authentication.getPrincipal()).getUsername();
        };
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan(new String[]{"org.mesol.spmes.model"});

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(additionalProperties());

        return em;
    }

    @Bean
    public DataSource dataSource() {
        if (logger.isDebugEnabled())
            logger.debug("call datasource");

        DriverManagerDataSource ds = new DriverManagerDataSource();

        String driver = env.getProperty("jdbc.driver");
        String url = env.getProperty("jdbc.url");
        String user = env.getProperty("jdbc.user");
        String password = env.getProperty("jdbc.password");

        Assert.notNull(driver, "Driver cannot be empty");
        Assert.notNull(url, "URL cannot be empty");

        Properties props = new Properties();
        props.setProperty("autoCommit", "false");
        props.setProperty("defaultAutoCommit", "false");

        ds.setConnectionProperties(props);
        ds.setDriverClassName(driver);
        ds.setUrl(url);
        ds.setUsername(user);
        ds.setPassword(password);

        return ds;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    private void readAndSetProperty(Properties properties, String property) {
        String propValue = env.getProperty(property);
        if (propValue != null)
            properties.setProperty(property, env.getProperty(property));
    }
    
    private Properties additionalProperties () {
        Properties properties = new Properties();
        readAndSetProperty(properties, "hibernate.generateDDL");
        readAndSetProperty(properties, "hibernate.dialect");
        readAndSetProperty(properties, "hibernate.hbm2ddl.auto");
        readAndSetProperty(properties, "hibernate.default_schema");
        readAndSetProperty(properties, "hibernate.cache.region.factory_class");
        readAndSetProperty(properties, "hibernate.cache.use_second_level_cache");
        readAndSetProperty(properties, "hibernate.cache.use_query_cache");
        readAndSetProperty(properties, "net.sf.ehcache.configurationResourceName");
        return properties;
    }
    
}
