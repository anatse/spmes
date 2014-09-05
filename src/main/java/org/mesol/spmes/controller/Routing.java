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
package org.mesol.spmes.controller;

import java.util.List;
import org.apache.log4j.Logger;
import org.mesol.spmes.model.graph.OperEdge;
import org.mesol.spmes.repo.RoutingRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Controller
@RequestMapping(value = "/router")
public class Routing 
{
    private static final Logger     logger = Logger.getLogger(Routing.class);
    
    @Autowired
    private RoutingRepo             routingRepo;
    
    @RequestMapping(value = "oper", method = RequestMethod.GET)
    public List<OperEdge> findAllOperations (@RequestParam(value = "routerId", defaultValue = "5150") Long routerId) {
        return routingRepo.findAllOpers(routerId);
    }
    
    @RequestMapping(value = "oper", method = RequestMethod.PUT)
    public List<OperEdge> addOperation (
        @RequestParam(value = "oper")OperEdge oper
    ) {
        return null;
    }
}
