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
package org.mesol.spmes.service;

import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.apache.log4j.Logger;
import org.mesol.spmes.consts.BasicConstants;
import org.mesol.spmes.model.graph.OperEdge;
import org.mesol.spmes.model.graph.PerformanceType;
import org.mesol.spmes.model.graph.Router;
import org.mesol.spmes.model.graph.RouterStep;
import org.mesol.spmes.model.graph.exceptions.ManySequentalOperationException;
import org.mesol.spmes.model.graph.exceptions.MultipleOperationsException;
import org.mesol.spmes.model.graph.exceptions.NoRuleException;
import org.mesol.spmes.model.graph.exceptions.NonParallelOperationException;
import org.mesol.spmes.model.graph.exceptions.OperEntryPointChanged;
import org.mesol.spmes.repo.RoutingRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Service
public class RouteService 
{
    private static final Logger     logger = Logger.getLogger(MethodHandles.lookup().lookupClass());

    @PersistenceContext
    private EntityManager           em;

    @Autowired
    private RoutingRepo             repo;

    @Transactional
    public Iterable<Router> findAllRouters () {
        return repo.findAll();
    }

    @Transactional
    public Router findRouterByName (String routerName) {
        return repo.findRouterByName(routerName);
    }

    @Transactional
    public List<OperEdge> findAllOperation (Long routerId) {
        return repo.findAllRouterOpers(routerId);
    }

    /**
     * Function used to find last router step for given router.
     * For simple sequential routers this function is very easy. However if router has many forks this function 
     * may not work properly. Therefore if there are multiple outgoing edges (operations) exists on the same
     * router step this function throws an MultipleOperationsException
     * 
     * @param router - router
     * @return last router step
     * @throws org.mesol.spmes.model.graph.exceptions.MultipleOperationsException
     */
    @Transactional
    public RouterStep getLastStep (Router router) throws MultipleOperationsException {
        RouterStep rs = null;
        OperEdge oper = router.getFirstOper();
        while (oper != null) {
            rs = oper.getTo();
            Collection<OperEdge> opers = rs.getOut();
            if (opers != null && opers.size() > 1) {
                throw (new MultipleOperationsException(
                    String.format ("There are more than one operation on router step id: %s. "
                        + "You can't use this function to determine last step in the router", 
                        rs.getId())
                ));
            }

            oper = (opers == null || opers.isEmpty()) ? null : opers.iterator().next();
        }

        return rs;
    }
    
    /**
     * Function used to find last router step for given router.
     * For simple sequential routers this function is very easy. However if router has many forks this function 
     * may not work properly. Therefore if there are multiple outgoing edges (operations) exists on the same
     * router step this function throws an MultipleOperationsException
     * 
     * @param routerId -router identifier
     * @return last router step
     * @throws org.mesol.spmes.model.graph.exceptions.MultipleOperationsException
     */
    @Transactional
    public RouterStep getLastStep (Long routerId) throws MultipleOperationsException {
        Router router = repo.findOne(routerId);
        return getLastStep(router);
    }

    @Transactional
    private void checkPerformanceType (RouterStep rs, OperEdge oper) throws ManySequentalOperationException, 
                                                                            NonParallelOperationException, 
                                                                            NoRuleException {
        OperEdge firstOper = rs.getOut().iterator().next();

        // It is necessary to check type of the first operation.
        switch (firstOper.getPerformanceType()) {
            case SEQUENTIAL:
                throw (new ManySequentalOperationException("Trying to add more than one sequental operation. Please use other operation type such as Parallel or Rule based"));

            case PARALLEL:
                // Check if all operatio types are parallel
                for (OperEdge op : rs.getOut()) {
                    if (op.getPerformanceType() != PerformanceType.PARALLEL)
                        throw (new NonParallelOperationException("One of operation parallel other is not."));
                }

                if (oper.getPerformanceType() != PerformanceType.PARALLEL)
                    throw (new NonParallelOperationException("Trying to add non parallel operation with parallel one."));

                break;

            case RULE_BASED:
                // Check if router step has rule
                if (rs.getRule() == null || rs.getRule().isEmpty())
                    throw (new NoRuleException("Trying to add rule based oepration to rouser step with no rule defined."));

                break;

            case ALTERNATIVE:
                oper.setPerformanceType(PerformanceType.ALTERNATIVE);
                break;
        }
    }

    @Transactional
    public RouterStep saveRouterStep (RouterStep rs) {
        return em.merge(rs);
    }
    
    /**
     * Function finds and retrieves next route operation for the given router step. Algorithm depends on type
     * of outgoing edges for current router step. E.g. for sequential operation function should return only one
     * operation, whereas for parallel or alternative type may return several. For rule based operations this 
     * function will compute rule expression, which written on groovy language, and after comparing result with
     * rule value for each operation this function returns one or more appropriate operations.
     * 
     * @param rs - router step
     * @return list of allowed operation edges
     * @throws ManySequentalOperationException
     * @throws NoRuleException 
     */
    public Collection<OperEdge> getNextOperation (RouterStep rs) throws ManySequentalOperationException, NoRuleException {
        final Collection<OperEdge> ops = new LinkedList<>();
        if (rs.getOut().size() > 1) {
            // Check type of operationns
            if (rs.getRule() != null && !rs.getRule().isEmpty()) {
                // Execute groovy script
                ScriptEngineManager factory = new ScriptEngineManager();
                ScriptEngine engine = factory.getEngineByName(BasicConstants.RULE_SCRIPT_ENGINE);
                if (engine instanceof Compilable) {
                    Compilable compilingEngine = (Compilable)engine;
                    try {
                        Bindings bnd = engine.createBindings();
                        bnd.put("curStep", rs);
                        CompiledScript script = rs.getRuleScript();
                        if (script == null) {
                            script = compilingEngine.compile(rs.getRule());
                            rs.setRuleScript(script);
                        }

                        Object value = script.eval(bnd);
                        final Object ret = value == null ? "" : value.toString();
                        rs.getOut().stream().filter(w -> ret.equals(w.getRuleValue())).forEach((op) -> {
                            ops.add(op);
                        });
                    }
                    catch (ScriptException ex) {
                        logger.error(ex, ex);
                    }
                }
            }
            else {
                OperEdge first = rs.getOut().iterator().next();
                switch (first.getPerformanceType()) {
                    case PARALLEL:
                    case ALTERNATIVE:
                        ops.addAll(rs.getOut());
                        break;

                    case SEQUENTIAL:
                        throw (new ManySequentalOperationException("Trying to add more than one sequental operation. Please use other operation type such as Parallel or Rule based"));

                    case RULE_BASED:
                        throw (new NoRuleException("Trying to add rule based oepration to rouser step with no rule defined."));
                }
            }
        }
        else {
            ops.addAll(rs.getOut());
        }

        return ops;
    }

    /**
     * Function add edge operation from one router step to another. Function throws 
     * 
     * @param oper - operation data
     * @param rsFrom - outgoing router step
     * @param rsTo - incoming router step
     * @return new cerated edge (operation)
     * @throws org.mesol.spmes.model.graph.exceptions.ManySequentalOperationException
     * @throws org.mesol.spmes.model.graph.exceptions.NonParallelOperationException
     * @throws org.mesol.spmes.model.graph.exceptions.NoRuleException
     */
    @Transactional
    public OperEdge addOperation (OperEdge oper, RouterStep rsFrom, RouterStep rsTo) 
            throws ManySequentalOperationException, 
                   NonParallelOperationException, 
                   NoRuleException {
        // Check if rsFrom router step already has incoming edges
        if (rsFrom.getOut() != null && !rsFrom.getOut().isEmpty()) {
            checkPerformanceType (rsFrom, oper);
        }

        // Persist operation if operation is this is a new edge
        if (oper.getId() == null)
            em.persist(oper);

        // Set start and end point of the oepration edge
        oper.setFrom(rsFrom);
        oper.setTo(rsTo);

        // Save operation
        em.merge(oper);

        return oper;
    }

    /**
     * Function update operation data. This data should not include change start or end point of edge
     * @param edge - operation to change
     * @return changed operation
     * @throws org.mesol.spmes.model.graph.exceptions.OperEntryPointChanged
     */
    public OperEdge saveOperation (OperEdge edge) throws OperEntryPointChanged {
        OperEdge oldOper = em.find(OperEdge.class, edge.getId());
        if (!oldOper.getFrom().getId().equals(edge.getFrom().getId()) || 
            !oldOper.getTo().getId().equals(edge.getTo().getId())) {
            throw (new OperEntryPointChanged("Cannot change entry point for the operation during save."));
        }
        
        return em.merge(edge);
    }
    
    /**
     * Function removes operation edge. Due to operation always has 
     * 
     * @param oper 
     */
    public void deleteOperation (OperEdge oper) {
        // Check if operation
        if (!em.contains(oper)) {
            oper = em.find(OperEdge.class, oper.getId());
        }

        if (oper.getTo().getOut() == null || oper.getTo().getOut().isEmpty()) {
            em.remove(oper.getTo());
            oper.getFrom().getOut().remove(oper);
            em.remove(oper);
        }
    }
    
    @Transactional
    public Router findRouter(Long routerId) {
        return em.find(Router.class, routerId);
    }
}
