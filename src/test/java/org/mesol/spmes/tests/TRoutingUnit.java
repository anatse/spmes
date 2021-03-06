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
package org.mesol.spmes.tests;

import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.logging.Level;
import javax.persistence.EntityManager;
import javax.script.ScriptException;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mesol.spmes.config.PersistenceJPAConfig;
import org.mesol.spmes.config.RootConfiguration;
import org.mesol.spmes.config.WebMvcConfiguration;
import org.mesol.spmes.config.WebMvcSecurityConfig;
import org.mesol.spmes.model.factory.EquipmentClass;
import org.mesol.spmes.model.graph.ObjectState;
import org.mesol.spmes.model.graph.Router;
import org.mesol.spmes.model.graph.RouterOperation;
import org.mesol.spmes.model.graph.RouterStep;
import org.mesol.spmes.model.graph.Vertex;
import org.mesol.spmes.model.refs.Duration;
import org.mesol.spmes.model.refs.Unit;
import org.mesol.spmes.model.refs.UnitConverter;
import org.mesol.spmes.service.EquipmentService;
import org.mesol.spmes.service.RoutingService;
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
public class TRoutingUnit 
{
    private static final Logger     logger = Logger.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private EquipmentService    eqService;
    
    @Autowired
    private UnitService         unitService;
    
    @Autowired
    private RoutingService            gService;

//    private void createRouter (String routerName, EquipmentClass eq) {
//        Router r = new Router();
//        r.setName(routerName);
//        em.persist(r);
//
//        Vertex last = null;
//        for (int i=0;i<100;i++) {
//            try {
//                RouterStep rs = new RouterStep();
//                rs.setName("RS-" + r.getName() + "." + i);
//                rs.setRouter(r);
//                em.persist(rs);
//
//                String name = last != null ? ((RouterStep)last).getName() : r.getName();
//                insertOperation (((last != null) ? last : r), rs, ro -> {
//                    ro.setDuration(new Duration(101));
//                    ro.setName("OP: " + name + " -> " + rs.getName());
//                    ro.setEquipmentClass(eq);
//                });
//
//                last = rs;
//            } 
//            catch (Exception ex) {
//                logger.error(ex, ex);
//            }
//        }
//    }
    
    
    @Transactional
    public void test () {
//        EquipmentClass eq = new EquipmentClass();
//        eq.setName("DEFAULT");
//        em.persist(eq);
//        
//        createRouter ("RT-1", eq);
//        createRouter ("RT-2", eq);
//        createRouter ("RT-3", eq);
    }

    
    @Test
    @Transactional
    public void testGRouting () {
//        gService.test();
        
        Iterable<Router> rs = gService.findActiveRouters();
        Iterable<Router> rs2 = gService.findRoutersByStatus(ObjectState.DEVELOPMENT);

        rs2.forEach(r -> {
            System.out.println (r.getName());
            
            Collection<RouterOperation> opers = gService.finaAllOperations(r);
            opers.forEach(o -> {
                System.out.println ("    " + o.getName() + " startTime: " + o.getStartTime());
                if (o.getStartTime() % 2 != 0) {
                    try {
                        gService.deleteRouterStep((RouterStep)o.getTo(), Boolean.TRUE);
                    } 
                    catch (Exception ex) {
                        logger.error(ex, ex);
                    }
                }
            });
        });
    }
    
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
//            opNew.setPerformanceType(PerformType.SEQUENTIAL);
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
//                opNew.setPerformanceType(PerformType.RULE_BASED);
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
//                opNew2.setPerformanceType(PerformType.RULE_BASED);
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
