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
import java.util.function.BiFunction;
import java.util.function.Consumer;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import static org.hibernate.criterion.Order.*;
import org.mesol.spmes.model.factory.EquipmentClass;
import org.mesol.spmes.model.gr.ObjectState;
import org.mesol.spmes.model.gr.Router;
import org.mesol.spmes.model.gr.RouterOperation;
import org.mesol.spmes.model.gr.RouterStep;
import org.mesol.spmes.model.gr.Vertex;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static org.hibernate.criterion.Restrictions.*;
import org.mesol.spmes.model.gr.IRouterElement;
import org.mesol.spmes.model.gr.RoutingUtils;
import org.mesol.spmes.model.refs.Duration;
import org.springframework.util.Assert;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Service
public class RoutingService 
{
    private static final Logger         logger = Logger.getLogger(MethodHandles.lookup().lookupClass());

    @PersistenceContext
    private EntityManager               em;

    @Transactional
    public Collection<Router> findActiveRouters () {
        return findRoutersByStatus(ObjectState.RELEASED);
    }

    @Transactional
    public Collection<Router> findRoutersByStatus (ObjectState status) {
        Session session = em.unwrap(Session.class);
        return session.createCriteria(Router.class)
            .add(eq("status", status))
            .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
            .list();
    }

    @Transactional
    public Collection<RouterOperation> finaAllOperations (Router router) {
        Session session = em.unwrap(Session.class);
        return session.createCriteria(RouterOperation.class)
            .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
            .add(eq("router", router))
//            .setFetchMode("router", FetchMode.JOIN)
//            .setFetchMode("from", FetchMode.SELECT)
//            .setFetchMode("to", FetchMode.SELECT)
            .addOrder(asc("startTime"))
            .list();
    }
    
    private void calcStartTime (final Vertex to) {
        if (to == null || to.getOutEdges().isEmpty())
            return;

        final long startTime = RoutingUtils.computeStart(to.getInEdges());
        to.getOutEdges().forEach(edge -> {
            edge.setStartTime(startTime);
            em.merge(edge);
            calcStartTime (edge.getTo());
        });
    }

    @Transactional
    public RouterOperation insert (Vertex after, Vertex element, Duration duration, Consumer<RouterOperation> cons) {
        Assert.isInstanceOf(IRouterElement.class, after, "After element should be instance of IRouterElement");
        // move next from after vertex to element 
        if (!after.getOutEdges().isEmpty()) {
            after.getOutEdges().forEach(edge -> {
                after.undockEdge(edge);
                element.addOutEdge(edge);
                em.merge(edge);
            });
        }

        IRouterElement re = (IRouterElement)after;
        RouterOperation ro = re.createEdgeTo(after, duration);
        if (cons != null) {
            cons.accept(ro);
        }

        // Save new operation
        em.merge(ro);

        // Recalculate startTime for all following operations
        calcStartTime(ro.getTo());

        return ro;
    }

    @Transactional
    public Router save (Router router) {
        return em.merge(router);
    }

    private void createRouter (String routerName, EquipmentClass eq) {
        Router r = new Router();
        r.setName(routerName);
        em.persist(r);

        Vertex last = null;
        for (int i=0;i<100;i++) {
            RouterStep rs = new RouterStep();
            rs.setName("RS-" + r.getName() + "." + i);
            rs.setRouter(r);
            em.persist(rs);
//            RouterOperation ro = ((last != null) ? last : r).createEdgeTo(rs, RouterOperation.class, new Duration(100));
            String name = last != null ? ((RouterStep)last).getName() : r.getName();
//            ro.setName("OP: " + name + " -> " + rs.getName());
//            ro.setEquipmentClass(eq);
//            ro.setRouter(r);
//            em.persist(ro);
            last = rs;
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
}
