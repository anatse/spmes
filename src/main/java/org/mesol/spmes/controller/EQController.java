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
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.mesol.spmes.model.factory.Equipment;
import org.mesol.spmes.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Controller
@RequestMapping(value="/equipment")
public class EQController 
{
    @Autowired
    private EquipmentService             eqService;

    @Autowired
    private MessageSource                messageSource;
    
    private static final Logger     logger = Logger.getLogger(AuthController.class);


    @Transactional
    @RequestMapping(value = "eq/{eqId}", method = RequestMethod.GET, produces="application/json")
    public List<Equipment> equipmentList (@PathVariable Long eqId)
    {       
        if (eqId != null && eqId != -1) {            
            Equipment parentEqt = eqService.findById(eqId);
            return (List<Equipment>) eqService.findByParent(parentEqt);
        }
        else
        {
            return eqService.findRoot();
        }
    }

    @Transactional
    @RequestMapping(value = "eq/{eqId}", method = RequestMethod.PUT, produces="application/json" , consumes = {"application/json"})
    public Equipment equipmentUpd (@PathVariable Long eqId, @RequestBody Equipment eq, HttpServletResponse httpResponse, WebRequest request
    ) {
        return eq;
    }
}