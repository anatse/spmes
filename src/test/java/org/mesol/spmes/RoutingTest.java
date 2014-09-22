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

import javax.script.ScriptException;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mesol.spmes.config.PersistenceJPAConfig;
import org.mesol.spmes.config.RootConfiguration;
import org.mesol.spmes.config.WebMvcConfiguration;
import org.mesol.spmes.config.WebMvcSecurityConfig;
import org.mesol.spmes.model.graph.exceptions.ManySequentalOperationException;
import org.mesol.spmes.model.graph.exceptions.MultipleOperationsException;
import org.mesol.spmes.model.graph.exceptions.NoRuleException;
import org.mesol.spmes.model.graph.exceptions.NonParallelOperationException;
import org.mesol.spmes.model.graph.OperEdge;
import org.mesol.spmes.model.graph.PerformanceType;
import org.mesol.spmes.model.graph.Router;
import org.mesol.spmes.model.graph.RouterStep;
import org.mesol.spmes.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
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
@TransactionConfiguration(defaultRollback = true)
@WebAppConfiguration
public class RoutingTest 
{
    private static final Logger     logger = Logger.getLogger(RoutingTest.class);

    @Configuration
	@EnableJpaRepositories
	@EnableJpaAuditing
	static class Config {

	}

    @Autowired
    private RouteService        service;

    @Test
    @Transactional
    public void testRouter() throws ScriptException, NoSuchMethodException {
        Router rt = service.findRouterByName("R_TEST_1");
        Assert.notNull(rt, "Router not found");
        
        try {
            System.out.println ("Get last router step");
            RouterStep rs = service.getLastStep(rt);
            Assert.isTrue(rs.getName().equals("sixthStep"), "Wrong step name, should be sixStep");

            System.out.println ("Adding operation");
            RouterStep rsNew = new RouterStep();
            rsNew.setName("seventhStep");
            OperEdge opNew = new OperEdge();
            opNew.setName("newOper");
            opNew.setPerformanceType(PerformanceType.SEQUENTIAL);
            opNew.setWeight(100.0);
            
            try {
                opNew = service.addOperation(opNew, rs, rsNew);
                Assert.notNull(opNew, "Operation not create");
                
                rt = service.findRouterByName("R_TEST_1");
                rs = service.getLastStep(rt);
                Assert.isTrue(rs.getName().equals("seventhStep"), "Wrong step name, should be seventhStep, now " + rs.getName());

//        ScriptEngineManager factory = new ScriptEngineManager();
////        List<ScriptEngineFactory> engines = factory.getEngineFactories();
////        for (ScriptEngineFactory f : engines) {
////            System.out.println (f.getEngineName());
////        }
//        
//        ScriptEngine engine = factory.getEngineByName("groovy");
//        String fact = "def factorial(n) { n == 1 ? 1 : n * factorial(n - 1) }";
//        engine.eval(fact);
//        Invocable inv = (Invocable) engine;
//        Object[] params = { new Integer(5) };
//        Object result = inv.invokeFunction("factorial", params);
//        System.out.println(result);
            }
            catch (ManySequentalOperationException | NonParallelOperationException | NoRuleException ex) {
                logger.error(ex, ex);
            }
        }
        catch (MultipleOperationsException ex) {
            Assert. isTrue(false, ex.toString());
        }
    }
}
