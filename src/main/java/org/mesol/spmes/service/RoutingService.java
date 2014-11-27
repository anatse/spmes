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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import static org.hibernate.criterion.Order.*;
import org.mesol.spmes.model.factory.EquipmentClass;
import org.mesol.spmes.model.graph.ObjectState;
import org.mesol.spmes.model.graph.Router;
import org.mesol.spmes.model.graph.RouterOperation;
import org.mesol.spmes.model.graph.RouterStep;
import org.mesol.spmes.model.graph.Vertex;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static org.hibernate.criterion.Restrictions.*;
import org.mesol.spmes.consts.BasicConstants;
import org.mesol.spmes.model.abs.NamingRuleConstants;
import org.mesol.spmes.model.graph.Edge;
import org.mesol.spmes.model.graph.IRouterElement;
import static org.mesol.spmes.model.graph.PerformType.ALTERNATIVE;
import static org.mesol.spmes.model.graph.PerformType.PARALLEL;
import static org.mesol.spmes.model.graph.PerformType.RULE_BASED;
import static org.mesol.spmes.model.graph.PerformType.SEQUENTIAL;
import org.mesol.spmes.model.graph.RoutingUtils;
import org.mesol.spmes.model.graph.attr.RouterAttribute;
import org.mesol.spmes.model.graph.exceptions.LoopException;
import org.mesol.spmes.model.graph.exceptions.ManySequentalOperationException;
import org.mesol.spmes.model.graph.exceptions.NoRuleException;
import org.mesol.spmes.model.refs.Duration;
import org.mesol.spmes.service.abs.AbstractServiceWithAttributes;
import org.springframework.util.Assert;

/**
 * Class represents routing service
 * @version 1.0.0
 * @author ASementsov
 */
@Service
public class RoutingService extends AbstractServiceWithAttributes
{
    private static final Logger         logger = Logger.getLogger(MethodHandles.lookup().lookupClass());

    @PersistenceContext
    private EntityManager               em;

    /**
     * Find all routers with status = active
     * @return list of routers
     */
    @Transactional
    public Collection<Router> findActiveRouters () {
        return findRoutersByStatus(ObjectState.RELEASED);
    }
    
    @Transactional
    public Collection<Router> findRouterByName (String name) {
        Session session = getHibernateSession();
        return session.createCriteria(Router.class)
            .add(eq(NamingRuleConstants.NAME, name))
            .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
            .list();
    }

    @Transactional
    public List<Router> findRoutersByAttributes (Set<RouterAttribute> attrs) {
        if (attrs.size() == 1)
            return findByAttribute(attrs.iterator().next(), Router.class);
        else
            return findByAttributes(attrs, Router.class);
    }

    /**
     * Find routers with specified status
     * @param status router status
     * @return list of routers
     */
    @Transactional
    public Collection<Router> findRoutersByStatus (ObjectState status) {
        Session session = getHibernateSession();
        return session.createCriteria(Router.class)
            .add(eq("status", status))
            .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
            .list();
    }
    
    /**
     * Function find all routers with status != DISABLED
     * @return list of found routers
     */
    @Transactional
    public Collection<Router> findAllRouters () {
        Session session = getHibernateSession();
        return session.createCriteria(Router.class)
            .add(not(eq("status", ObjectState.DISABLED)))
            .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
            .list();
    }

    /**
     * Function find all router operations, ordered by startTime
     * @param router router
     * @return list of router operations
     */
    @Transactional
    public Collection<RouterOperation> finaAllOperations (Router router) {
        Session session = getHibernateSession();
        return session.createCriteria(RouterOperation.class)
            .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
            .add(eq("router", router))
//            .setFetchMode("router", FetchMode.JOIN)
//            .setFetchMode("from", FetchMode.SELECT)
//            .setFetchMode("to", FetchMode.SELECT)
            .addOrder(asc("startTime"))
            .list();
    }

    /**
     * Function calculated start time for all operations chain. Loop for operations
     * chain is not allowed
     * @param start start vertex
     * @param to to vertex
     * @throws LoopException 
     */
    private void calcStartTime (final Vertex start, final Vertex to) throws LoopException {
        if (to == null || to.getOutEdges().isEmpty())
            return;

        if (start.equals(to))
            throw (new LoopException("Router operations cycle detected"));

        final long startTime = RoutingUtils.computeStart(to.getInEdges());
        for (Edge edge : to.getOutEdges()) {
            edge.setStartTime(startTime);
            em.merge(edge);
            calcStartTime(start, edge.getTo());
        }
    }

    /**
     * Insert operation into router
     * @param after vertex after which operation should be inserted
     * @param element vertex to which operation should be connected
     * @param cons lambda function which set parameters for operation
     * @return new created operation
     * @throws Exception 
     */
    @Transactional
    public RouterOperation insertOperation (Vertex after, Vertex element, Consumer<RouterOperation> cons) throws Exception {
        RouterOperation ro = new RouterOperation();
        if (cons != null) {
            cons.accept(ro);
        }

        return insertOperation(after, element, ro);
    }
    
    /**
     * Insert operation into router
     * @param after vertex after which operation should be inserted
     * @param element vertex to which operation should be connected
     * @param ro Router operation object
     * @return new created operation
     * @throws Exception 
     */
    @Transactional
    public RouterOperation insertOperation (Vertex after, Vertex element, RouterOperation ro) throws Exception {
        Assert.isInstanceOf(IRouterElement.class, after, "After element should be instance of IRouterElement");
        Assert.isInstanceOf(IRouterElement.class, element, "To element should be instance of IRouterElement");

        final Vertex _after = after.getId() != null ? em.find(after.getClass(), after.getId()) : after;
        final Vertex _element = element.getId() != null ? em.find(element.getClass(), element.getId()) : element;

        // move next from after vertex to element 
        if (!after.getOutEdges().isEmpty()) {
            after.getOutEdges().forEach(edge -> {
                _after.undockEdge(edge);
                _element.addOutEdge(edge);
                em.merge(edge);
            });
        }

        IRouterElement re = (IRouterElement)_after;
        ro = re.addEdgeTo(_element, ro);

        // Save new operation
        em.merge(ro);

        // Recalculate startTime for all following operations
        calcStartTime(_after, ro.getTo());

        return ro;
    }

    /**
     * Save router
     * @param router
     * @return 
     */
    @Transactional
    public Router save (Router router) {
        return em.merge(router);
    }

    /**
     * Function marks router as disabled
     * @param router router
     */
    @Transactional
    public void deleteRouter (Router router) {
        router.setStatus(ObjectState.DISABLED);
        save (router);
    }

    /**
     * Function get next operation from current router step
     * @param rs current router step
     * @return next operation or operations depends on operation type
     * @throws Exception 
     */
    @Transactional
    public Set<RouterOperation> getNextOperation (RouterStep rs) throws Exception {
        final Set<RouterOperation> ops = new HashSet<>();
        
        rs = em.find(RouterStep.class, rs.getId());
        if (rs.getOutEdges().size() > 1) {
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
                        rs.getOutEdges().stream().filter(w -> ret.equals(w.getRuleValue())).forEach((op) -> {
                            ops.add((RouterOperation)op);
                        });
                    }
                    catch (ScriptException ex) {
                        logger.error(ex, ex);
                    }
                }
            }
            else {
                RouterOperation first = (RouterOperation) rs.getOutEdges().iterator().next();
                switch (first.getPerformType()) {
                    case PARALLEL:
                    case ALTERNATIVE:
                        ops.addAll((Collection)rs.getOutEdges());
                        break;

                    case SEQUENTIAL:
                        throw (new ManySequentalOperationException("Trying to add more than one sequental operation. Please use other operation type such as Parallel or Rule based"));

                    case RULE_BASED:
                        throw (new NoRuleException("Trying to add rule based oepration to rouser step with no rule defined."));
                }
            }
        }
        else {
            ops.addAll((Collection)rs.getOutEdges());
        }

        return ops;
    }

    /**
     * Recursive function used to remove router step from router with cascade removing children elements
     * @param rs -Router step to remove
     */
    private void cascadeRemove (Vertex rs) {
        if (rs != null && rs.getOutEdges() != null) {
            rs.getOutEdges().stream().forEach(op -> {
                if (op.getTo() != null)
                    cascadeRemove(op.getTo());

                em.remove(op);
            });
        }

        em.remove(rs);
    }
    
    /**
     * Delete router step from router
     * @param rs Router step
     * @param cascade flag indicate cascading deletion
     * @throws Exception 
     */
    @Transactional
    public void deleteRouterStep (RouterStep rs, Boolean cascade) throws Exception {
        Assert.notNull(rs, "RouterStep is null");
        Assert.notNull(rs.getId(), "RouterStep Id is null");
        Assert.isTrue(rs.getOutEdges().size() <= 1 || cascade, "Cannot remove router step with several outgoing edges. Please use cascade removing");

        rs = em.find(RouterStep.class, rs.getId());
        if (rs == null)
            return;

        if (cascade) {
            cascadeRemove (rs);
            rs.getInEdges().stream().forEach(edge -> {
                em.remove (edge);
            });
        }
        else {
            if (rs.getOutEdges().isEmpty()) {
                em.remove (rs);
                rs.getInEdges().stream().forEach(edge -> {
                    em.remove (edge);
                });
            }
            else {
                final Vertex to = rs.getOutEdges().iterator().next().getTo();
                // remap all input edges to next step
                for (Edge edge : rs.getInEdges()) {
                    rs.undockEdge(edge);
                    to.addInEdge(edge);
                    // Recalculate startTime for all following operations
                    calcStartTime(edge.getFrom(), edge.getTo());

                    em.merge(edge.getTo());
                    em.merge(edge);
                }

                em.remove (rs);
            }
        }
    }

    private void createRouter (String routerName, EquipmentClass eq) {
        Router r = new Router();
        r.setName(routerName);
        em.persist(r);

        Vertex last = null;
        for (int i=0;i<100;i++) {
            try {
                RouterStep rs = new RouterStep();
                rs.setName("RS-" + r.getName() + "." + i);
                rs.setRouter(r);
                em.persist(rs);

                String name = last != null ? ((RouterStep)last).getName() : r.getName();
                insertOperation (((last != null) ? last : r), rs, ro -> {
                    ro.setDuration(new Duration(101));
                    ro.setName("OP: " + name + " -> " + rs.getName());
                    ro.setEquipmentClass(eq);
                });

                last = rs;
            } 
            catch (Exception ex) {
                logger.error(ex, ex);
            }
        }
    }

    @Transactional
    public void test () {
        EquipmentClass eq = new EquipmentClass();
        eq.setName("DEFAULT");
        em.persist(eq);
        
        createRouter ("RT-1", eq);
        createRouter ("RT-2", eq);
        createRouter ("RT-3", eq);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
