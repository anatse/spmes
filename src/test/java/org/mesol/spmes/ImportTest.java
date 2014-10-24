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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import org.mesol.spmes.utils.GroovyExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.Assert;

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
        String groovyScript = "imp/Import.groovy";
        StringBuilder sb = new StringBuilder();
        InputStream is = getClass().getClassLoader().getResourceAsStream(groovyScript);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        catch (Exception ex) {
            Assert.isTrue(false, ex.toString());
        }

        groovyScript = sb.toString();
        sb = new StringBuilder();
        is = getClass().getClassLoader().getResourceAsStream("imp/eqs.xml");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        catch (Exception ex) {
            Assert.isTrue(false, ex.toString());
        }

        Object value = new GroovyExecutor()
            .bind("xmlSource", sb.toString())
            .bind("impBean", imp)
            .bind("types", imp.getTypes())
            .bind("embeddables", imp.getEmbeddables())
            .execute(groovyScript);

        System.out.println (value);
    }

    @Configuration
    @EnableJpaRepositories
    @EnableJpaAuditing
    static class Config {
    }
}
