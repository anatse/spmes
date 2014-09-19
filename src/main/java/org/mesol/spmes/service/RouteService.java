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
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.log4j.Logger;
import org.mesol.spmes.model.graph.OperEdge;
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
@Transactional
public class RouteService 
{
    private static final Logger     logger = Logger.getLogger(RouteService.class);

    @PersistenceContext
    private EntityManager           em;
    
    @Autowired
    private RoutingRepo             repo;

    public Iterable<Router> findAllRouters () {
        return repo.findAll();
    }

    public Router findRouterByName (String routerName) {
        return repo.findRouterByName(routerName);
    }

    public List<OperEdge> findAllOperation (Long routerId) {
        return repo.findAllRouterOpers(routerId);
    }

    private OperEdge getNextOperation (RouterStep rs) {
//        GroovyScriptEngine gs = new GroovyScriptEngine((ResourceConnector)null);
//        gs.loadScriptByName(null);
        return null;
    }

    public RouterStep getLastStep (Long routerId) {
        RouterStep rs = null;
        Router router = repo.findOne(routerId);
        OperEdge oper = router.getFirstOper();
        while (oper != null) {
            
            
            Collection<OperEdge> opers = oper.getTo().getOut();
            
        }
        
        return rs;
    }
    
    public OperEdge addOperation (OperEdge oper, RouterStep routerStep) {
        
        
        
        return null;
    }
}
