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
import org.hibernate.Criteria;
import static org.hibernate.criterion.Restrictions.*;
import org.mesol.spmes.model.abs.NamingRuleConstants;
import org.mesol.spmes.model.factory.Equipment;
import org.mesol.spmes.model.factory.EquipmentAttribute;
import org.mesol.spmes.model.factory.EquipmentClass;
import org.mesol.spmes.repo.EquipmentRepo;
import org.mesol.spmes.service.abs.AbstractServiceWithAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Service(value = "eqService")
public class EquipmentService extends AbstractServiceWithAttributes<Equipment, EquipmentAttribute>
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
        return equipmentRepo.findByName(name);
    }

    public List<Equipment> findRoot () {
        return equipmentRepo.findRootElements();
    }
    
    public Equipment findById (Long eqId) {
        return equipmentRepo.findOne(eqId);
    }
   
    public List<Equipment> getAll () {
        return (List<Equipment>) equipmentRepo.findAll();
    }

    public List<EquipmentAttribute> getAttributesByEquipment (Long eqId) {
        Equipment eq;
        eq = this.findById(eqId);        
        return (List<EquipmentAttribute>) eq.getAttributes();
    }
    
    @Transactional
    public List<Equipment> findByParent (Equipment parent) {
        
//        return equipmentRepo.findByParentEquipment(parent);
        return getHibernateSession().createCriteria(Equipment.class).add (
            eq(NamingRuleConstants.PARENT_EQ, parent)
        )//.setFetchMode("equipmentClasses", FetchMode.SELECT)
         //.setFetchMode(NamingRuleConstants.ATTRIBUTES, FetchMode.SELECT)
         .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    @Transactional
    public EquipmentClass saveEquipmentClass (EquipmentClass eqc) {
        return getEntityManager().merge(eqc);
    }
}
