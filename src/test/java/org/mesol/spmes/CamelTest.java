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

package org.mesol.spmes;

import java.lang.invoke.MethodHandles;
import java.util.List;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.camel.spring.SpringRouteBuilder;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mesol.spmes.config.PersistenceJPAConfig;
import org.mesol.spmes.config.RootConfiguration;
import org.mesol.spmes.config.WebMvcConfiguration;
import org.mesol.spmes.config.WebMvcSecurityConfig;
import org.mesol.spmes.service.RoutingService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration (classes = {
    RootConfiguration.class,
    PersistenceJPAConfig.class,
    WebMvcConfiguration.class,
    WebMvcSecurityConfig.class
})
@TransactionConfiguration(defaultRollback = true)
@WebAppConfiguration
public class CamelTest implements ApplicationContextAware 
{
    private static final Logger         logger = Logger.getLogger(MethodHandles.lookup().lookupClass());

    private ApplicationContext          appContext;
    
    @Test
    public void test() throws Exception {
        SpringCamelContext ctx = new SpringCamelContext(appContext);
        SpringRouteBuilder builder = new SpringRouteBuilder() {
            @Override
            public void configure() throws Exception {
                System.out.println ("test");
                intercept().to("routingService:findActiveRouters").addOutput(new ProcessorDefinition() {
                    List outputs;
                    
                    @Override
                    public List getOutputs() {
                        System.out.println ("getOutputs: " + true);
                        return outputs;
                    }

                    @Override
                    public boolean isOutputSupported() {
                        System.out.println ("isOutputSupported: " + true);
                        return true;
                    }
                });

                RoutingService srv = appContext.getBean(RoutingService.class);
                srv.findActiveRouters();
            }
        };

        builder.addRoutesToCamelContext(ctx);
        ctx.startAllRoutes();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        appContext = applicationContext;
    }
    
    @Configuration
    @EnableJpaRepositories
    @EnableJpaAuditing
    static class Config {
    }
}
