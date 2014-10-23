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
import java.util.Collection;
import javax.script.ScriptException;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mesol.spmes.config.PersistenceJPAConfig;
import org.mesol.spmes.config.RootConfiguration;
import org.mesol.spmes.config.WebMvcConfiguration;
import org.mesol.spmes.config.WebMvcSecurityConfig;
import org.mesol.spmes.model.factory.EquipmentClass;
import org.mesol.spmes.model.graph.OperEdge;
import org.mesol.spmes.model.graph.PerformanceType;
import org.mesol.spmes.model.graph.Router;
import org.mesol.spmes.model.graph.RouterStep;
import org.mesol.spmes.model.graph.exceptions.ManySequentalOperationException;
import org.mesol.spmes.model.graph.exceptions.MultipleOperationsException;
import org.mesol.spmes.model.graph.exceptions.NoRuleException;
import org.mesol.spmes.model.graph.exceptions.NonParallelOperationException;
import org.mesol.spmes.model.graph.exceptions.OperEntryPointChanged;
import org.mesol.spmes.model.refs.Unit;
import org.mesol.spmes.model.refs.UnitConverter;
import org.mesol.spmes.service.EquipmentService;
import org.mesol.spmes.service.RouteService;
import org.mesol.spmes.service.UnitService;
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
 * Class for testing router functionality
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
    private static final Logger     logger = Logger.getLogger(MethodHandles.lookup().lookupClass());


    @Autowired
    private RouteService        service;
    
    @Autowired
    private EquipmentService    eqService;
    
    @Autowired
    private UnitService         unitService;

    @Test
    @Transactional
    public void testUnitConverter () {
        if (logger.isInfoEnabled())
            logger.info("Test unit converter");

        Unit celsius = new Unit();
        celsius.setCode("ºC");
        celsius.setName("celsius");
        celsius = unitService.save (celsius);

        Unit fahrenheit = new Unit();
        fahrenheit.setCode("ºF");
        fahrenheit.setName("celsius");
        fahrenheit = unitService.save (fahrenheit);

        UnitConverter uc = new UnitConverter();
        uc.setFromUnit(celsius);
        uc.setToUnit(fahrenheit);
        uc.setFormula("(#qty * 9 / 5) + 32");
        unitService.saveUnitConverter (uc);

        uc = unitService.findFor(celsius.getCode(), fahrenheit.getCode());

        Double val = uc.convert(36.6);
        Assert.isTrue(val == (36.6 * 9 / 5) + 32, "Wrong value calculated");

        if (logger.isInfoEnabled())
            logger.info("Unit converter passed");
    }

    @Test
    @Transactional
    public void testRouter() throws ScriptException, NoSuchMethodException {
//        Router rt = service.findRouterByName("R_TEST_1");
//        Assert.notNull(rt, "Router not found");
//
//        try {
//            EquipmentClass eqc = new EquipmentClass();
//            eqc.setName("DEFAULT");
//            eqc = eqService.saveEquipmentClass(eqc);
//            
//            System.out.println ("Get last router step");
//            RouterStep rs = service.getLastStep(rt);
//            Assert.isTrue(rs.getName().equals("sixthStep"), "Wrong step name, should be sixStep");
//
//            System.out.println ("Adding operation");
//            RouterStep rsNew = new RouterStep();
//            rsNew.setName("seventhStep");
//            rsNew.setRouter(rt);
//            
//            OperEdge opNew = new OperEdge();
//            opNew.setName("newOper");
//            opNew.setPerformanceType(PerformanceType.SEQUENTIAL);
//            opNew.setWeight(100.0);
//            opNew.setEquipmentClass(eqc);
//            
//            try {
//                opNew = service.addOperation(opNew, rs, rsNew);
//                Assert.notNull(opNew, "Operation not create");
//
//                rs = service.getLastStep(rt);
//                Assert.isTrue(rs.getName().equals("seventhStep"), "Wrong step name, should be seventhStep, now " + rs.getName());
//
//                opNew.setWeight(300.0);
//                service.saveOperation(opNew);
//
//                Assert.isTrue(opNew.getWeight() == 300.0, "Wrong weight for operation opNew");
//                service.deleteOperation(opNew);
//
//                rs = service.getLastStep(rt);
//                Assert.isTrue(rs.getName().equals("sixthStep"), "Wrong step name, should be sixStep, now " + rs.getName());
//
//                System.out.println ("Check rule based operations");
//                rs.setRule("if (curStep.name == \"sixthStep\") {true} else {false}");
//                rs = service.saveRouterStep(rs);
//                
//                opNew.setId(null);
//                opNew.setPerformanceType(PerformanceType.RULE_BASED);
//                opNew.setRuleValue("true");
//                rsNew.setId(null);
//                service.addOperation(opNew, rs, rsNew);
//
//                RouterStep rsNew2 = new RouterStep();
//                rsNew2.setName("eigthStep");
//                rsNew2.setRouter(rt);
//            
//                OperEdge opNew2 = new OperEdge();
//                opNew2.setName("newOper2");
//                opNew2.setPerformanceType(PerformanceType.RULE_BASED);
//                opNew2.setWeight(200.0);
//                opNew2.setEquipmentClass(eqc);
//                opNew2.setRuleValue("false");
//                service.addOperation(opNew2, rs, rsNew2);
//                
//                Collection<OperEdge> opers = service.getNextOperation(rs);
//                Assert.notEmpty(opers, "Not found next rule based operation");
//                Assert.isTrue(opers.size() == 1, "Must be only ope oepration");
//                Assert.isTrue(opers.iterator().next().getName().equals("newOper"), "Operation name must be newOper ");
//            }
//            catch (ManySequentalOperationException | NonParallelOperationException | NoRuleException | MultipleOperationsException | OperEntryPointChanged ex) {
//                logger.error(ex, ex);
//                Assert. isTrue(false, ex.toString());
//            }
//        }
//        catch (MultipleOperationsException ex) {
//            Assert. isTrue(false, ex.toString());
//        }
    }

    @Configuration
    @EnableJpaRepositories
    @EnableJpaAuditing
    static class Config {
    }
}
