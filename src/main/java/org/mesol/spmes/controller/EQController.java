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

import java.lang.invoke.MethodHandles;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.mesol.spmes.model.factory.Equipment;
import org.mesol.spmes.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Controller
@RequestMapping(value="/service/equipment")
public class EQController 
{
    private static final Logger     logger = Logger.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private EquipmentService             eqService;

    @Transactional
    @RequestMapping(value = "/list", method = RequestMethod.POST, produces="application/json")
    public List<Equipment> equipmentList (@RequestParam(value = "parentId", required = false) Long parentId) {
        if (parentId != null && parentId != -1) {            
            Equipment parentEqt = eqService.findById(parentId);
            return (List<Equipment>) eqService.findByParent(parentEqt);
        }
        else {
            return eqService.findRoot();
        }
    }
    
    @Transactional
    @RequestMapping(value = "add", method = RequestMethod.POST, produces="application/json" , consumes = {"application/json"})
    public Equipment addEquipment (
            @RequestParam(value = "parentId") Long parentId, 
            @RequestBody Equipment equipment, 
            HttpServletResponse httpResponse, 
            WebRequest request
    ) {
        Equipment parent = eqService.findById(parentId);
        equipment.setParentEquipment(parent);
        return eqService.save(equipment);
    }

    @Transactional
    @RequestMapping(value = "eq/{eqId}", method = RequestMethod.PUT, produces="application/json" , consumes = {"application/json"})
    public Equipment equipmentUpd (@PathVariable Long eqId, @RequestBody Equipment eq, HttpServletResponse httpResponse, WebRequest request
    ) {
        return eq;
    }
}