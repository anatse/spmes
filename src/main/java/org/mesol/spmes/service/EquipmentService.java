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

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.mesol.spmes.model.abs.NamingRuleConstants;
import org.mesol.spmes.model.factory.Equipment;
import org.mesol.spmes.model.factory.EquipmentAttribute;
import org.mesol.spmes.repo.EquipmentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Service(value = "eqService")
public class EquipmentService extends AbstractCriteriaService<Equipment, EquipmentAttribute>
{
    @PersistenceContext
    private EntityManager           entityManager;
    
    @Autowired
    private EquipmentRepo           equipmentRepo;

    public EquipmentService() {
        super(Equipment.class);
        if (logger.isDebugEnabled())
            logger.debug("create EquipmentService object");
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public Equipment findByName (String name) {
//        return findSingleObject(new OrderField[0], new FilterValue(NamingRuleConstants.NAME, name));
        return equipmentRepo.findByName(name);
    }

    public List<Equipment> findRoot () {
        return equipmentRepo.findRootElements();
    }
    
    public List<Equipment> findByParent (Equipment parent) {
        return findFiltered(new OrderField[0], new FilterValue(NamingRuleConstants.PARENT, parent));
    }
}
