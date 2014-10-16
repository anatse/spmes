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
import javax.transaction.Transactional;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mesol.spmes.config.PersistenceJPAConfig;
import org.mesol.spmes.config.RootConfiguration;
import org.mesol.spmes.config.WebMvcConfiguration;
import org.mesol.spmes.config.WebMvcSecurityConfig;
import org.mesol.spmes.service.Import;
import org.springframework.beans.factory.annotation.Autowired;
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
@Transactional
@TransactionConfiguration(defaultRollback = true)
@WebAppConfiguration
public class ImportTest 
{
    private static final Logger     logger = Logger.getLogger(MethodHandles.lookup().lookupClass());
    
    
    @Autowired
    private Import      imp;
    
    @Test
    @Ignore
    public void impTest () {
        imp.parse(getClass().getClassLoader().getResourceAsStream("imp/router.xml"));
    }

    @Configuration
    @EnableJpaRepositories
    @EnableJpaAuditing
    static class Config {
    }
}
