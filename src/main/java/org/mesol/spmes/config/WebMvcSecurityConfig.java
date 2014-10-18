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
import org.apache.log4j.Logger;
import org.mesol.spmes.model.security.DBAuthProvider;
import org.mesol.spmes.model.security.DBUserDetailsManager;
import org.mesol.spmes.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;

/**
 * $Rev:$
 * $Author:$
 * $Date:$
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Configuration
@EnableWebMvcSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, mode = AdviceMode.ASPECTJ)
public class WebMvcSecurityConfig extends WebSecurityConfigurerAdapter
{

    private static final Logger         logger = Logger.getLogger(MethodHandles.lookup().lookupClass());
    @Autowired(required = true)
    private DBUserDetailsManager userDetailsManager;
    
    @Bean
    @Override 
    public AuthenticationManager authenticationManagerBean() throws Exception {  
       return super.authenticationManagerBean(); 
    }
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        DBAuthProvider np = new DBAuthProvider();
        np.setUserDetailsService(userDetailsManager);
        auth.authenticationProvider(np);
    }

    @Bean
    @Order(2)
    public DBUserDetailsManager userDetailsManager () {
        return new DBUserDetailsManager();
    }
    
    @Bean
    @Order(10)
    public UserService userService () {
        return new UserService();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .anyRequest().authenticated()
            .and()
            .formLogin()
            .loginPage("/login") 
            .permitAll()
            .and()
            .logout()
            .logoutUrl("/j_spring_security_logout")
            .logoutSuccessUrl("/");        
    }
}
