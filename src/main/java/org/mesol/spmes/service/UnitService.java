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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.log4j.Logger;
import static org.hibernate.criterion.Restrictions.and;
import static org.hibernate.criterion.Restrictions.eq;
import org.mesol.spmes.model.refs.Unit;
import org.mesol.spmes.model.refs.UnitConverter;
import org.mesol.spmes.service.abs.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Service
public class UnitService extends AbstractService<Unit>
{
    private static final Logger     logger = Logger.getLogger(MethodHandles.lookup().lookupClass());

    @PersistenceContext
    private EntityManager           entityManager;

    public UnitService() {
        super(Unit.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    @Transactional
    public Unit findByCode (String unitCode) {
        return (Unit)getHibernateSession().load(Unit.class, unitCode);
    }

    @Transactional
    public UnitConverter findFor (String unitFrom, String unitTo) {
        if (logger.isDebugEnabled()) 
            logger.debug("findFor");
        
        return (UnitConverter)getHibernateSession().createCriteria(UnitConverter.class)
            .add(
                and (
                    eq("fromUnit.code", unitFrom),
                    eq("toUnit.code", unitTo)
                )
            ).uniqueResult();
    }

    @Transactional
    public UnitConverter saveUnitConverter (UnitConverter uc) {
        getHibernateSession().save(uc);
        return uc;
    }

    public Unit save(Unit unit) {
        getHibernateSession().save(unit);
        return unit;
    }
}
