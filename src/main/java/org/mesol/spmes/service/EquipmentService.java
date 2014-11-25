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
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import static org.hibernate.criterion.Restrictions.*;
import org.mesol.spmes.model.abs.NamingRuleConstants;
import org.mesol.spmes.model.factory.Equipment;
import org.mesol.spmes.model.factory.EquipmentAttribute;
import org.mesol.spmes.model.factory.EquipmentClass;
import org.mesol.spmes.service.abs.AbstractServiceWithAttributes;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Service(value = "eqService")
public class EquipmentService extends AbstractServiceWithAttributes
{
    private static final Logger         logger = Logger.getLogger(MethodHandles.lookup().lookupClass());

    @PersistenceContext
    private EntityManager               em;

    public EquipmentService() {
        if (logger.isDebugEnabled())
            logger.debug("create EquipmentService object");
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Equipment findByName (String name) {
        Session session = getHibernateSession();
        return (Equipment)session.createCriteria(Equipment.class)
            .add(eq(NamingRuleConstants.NAME, name))
            .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
            .uniqueResult();
    }

    public List<Equipment> findRoot () {
        Session session = getHibernateSession();
        return session.createCriteria(Equipment.class)
            .add(isNull(NamingRuleConstants.PARENT_EQ))
            .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
            .list();
    }

    public Equipment findById (Long eqId) {
        return em.find(Equipment.class, eqId);
    }

    public List<Equipment> getAll () {
        Session session = getHibernateSession();
        return session.createCriteria(Equipment.class)
            .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
            .list();
    }

    public Set<EquipmentAttribute> getAttributesByEquipment (Long eqId) {
        Equipment eq = findById(eqId);        
        return eq.getAttributes();
    }

    @Transactional
    public List<Equipment> findByParent (Equipment parent) {
        return getHibernateSession().createCriteria(Equipment.class).add (
            eq(NamingRuleConstants.PARENT_EQ, parent)
        ).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    @Transactional
    public EquipmentClass saveEquipmentClass (EquipmentClass eqc) {
        return getEntityManager().merge(eqc);
    }

    @Transactional
    public Equipment save (Equipment eq) {
        return getEntityManager().merge(eq);
    }
    
    @Transactional
    public List<Equipment> findEquipmentByAttributes (Set<EquipmentAttribute> attrs) {
        if (attrs.size() == 1)
            return findByAttribute(attrs.iterator().next(), Equipment.class);
        else
            return findByAttributes(attrs, Equipment.class);
    }
}
