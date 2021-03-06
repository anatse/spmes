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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Controller
@RequestMapping(value = "/router")
public class Routing 
{
//    private static final Logger     logger = Logger.getLogger(MethodHandles.lookup().lookupClass());
//    
//    @Autowired
//    private RouteService            routeService;
//    
//    @RequestMapping(value = "oper", method = RequestMethod.GET)
//    public List<OperEdge> findAllOperations (@RequestParam(value = "routerId", defaultValue = "5150") Long routerId) {
////        return routeService.findAllOperation(routerId);
//        return Collections.EMPTY_LIST;
//    }
//
//    @Secured({BasicConstants.TECHNOLOG_ROLE})
//    @RequestMapping(value = "oper", method = RequestMethod.POST)
//    @ResponseBody
//    public OperEdge addOperation (
//        @RequestBody OperEdge oper
//    ) {
//        try {
//            return routeService.addOperation(oper, null, null);
//        } 
//        catch (ManySequentalOperationException | NonParallelOperationException | NoRuleException ex) {
//            logger.error(ex, ex);
//        }
//
//        return null;
//    }
//    
//    @RequestMapping(value = "lastStep", method = RequestMethod.GET)
//    @Transactional
//    public RouterStep getlastStep (@RequestParam(value = "routerId", defaultValue = "5150") Long routerId) {
//        try {
//            //        Router rt = routeService.findRouter(routerId);
//            RouterStep rs = routeService.getLastStep(routerId);
//            RouterStep rsNew = new RouterStep();
//            rsNew.setName("seventhStep");
//
////            rsNew = routeService.saveRouterStep(rsNew);
//
//            OperEdge opNew = new OperEdge();
//            opNew.setName("newOper");
//            opNew.setPerformingType(PerformType.SEQUENTIAL);
//            opNew.setWeight(100.0);
//            opNew = routeService.addOperation(opNew, rs, rsNew);
//            return routeService.getLastStep(routerId);
//        } 
//        catch (Exception ex) {
//            logger.error(ex, ex);
//        }
//
//        return null;
//    }
//        
//    @InitBinder
//    public void initBinder(WebDataBinder binder) {
//        binder.registerCustomEditor(OperEdge.class, new PropertyEditorSupport() {
//            @Override
//            public void setAsText(String text) throws IllegalArgumentException {
//                ObjectMapper mapper = new ObjectMapper();
//                OperEdge value = null;
//
//                try {
//                    value = mapper.readValue(text, OperEdge.class);
//                } 
//                catch (IOException ex) {
//                    if (logger.isDebugEnabled())
//                        logger.debug(ex, ex);
//                }
//
//                setValue(value);
//            }
//        });
//    }
}
