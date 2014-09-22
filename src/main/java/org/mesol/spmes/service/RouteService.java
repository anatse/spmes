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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.log4j.Logger;
import org.mesol.spmes.model.graph.exceptions.ManySequentalOperationException;
import org.mesol.spmes.model.graph.exceptions.MultipleOperationsException;
import org.mesol.spmes.model.graph.exceptions.NoRuleException;
import org.mesol.spmes.model.graph.exceptions.NonParallelOperationException;
import org.mesol.spmes.model.graph.OperEdge;
import org.mesol.spmes.model.graph.PerformanceType;
import org.mesol.spmes.model.graph.Router;
import org.mesol.spmes.model.graph.RouterStep;
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
    private static final Logger     logger = Logger.getLogger(RouteService.class);

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
                throw (new ManySequentalOperationException("Tring to add more than one sequental operation. Please use other operation type such as Parallel or Rule based"));

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
    public RouterStep createRouterStep (RouterStep rs) {
        return em.merge(rs);
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

        // Create rsTo router step if it not exists javax.persistence.PersistenceUnitUtil jpaUtil = entityManager.getEntityManagerFactory().getPersistenceUnitUtil();
        if (rsTo.getId() == null) {
            rsTo = em.merge(rsTo);
        }

        oper.setFrom(rsFrom);
        oper.setTo(rsTo);

        // Save operation
        em.merge(oper);

        rsFrom.getOut().add(oper);
        if (!em.contains(rsFrom)){
            em.refresh(em.merge(rsFrom));
        }

        if (rsTo.getIn() == null)
            rsTo.setIn(new LinkedList<>());

        rsTo.getIn().add(oper);
        if (!em.contains(rsTo)){
            em.refresh(em.merge(rsTo));
        }

        return oper;
    }

    @Transactional
    public Router findRouter(Long routerId) {
        return em.find(Router.class, routerId);
    }
}
