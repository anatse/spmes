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

package org.mesol.spmes.service.abs;

import java.lang.invoke.MethodHandles;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.mesol.spmes.model.abs.AbstractEntity;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 * @param <T>
 */
public abstract class AbstractService<T extends AbstractEntity>
{
    protected static final Logger   logger = Logger.getLogger(MethodHandles.lookup().lookupClass());
    private final Class<T>          entityClass;

    protected AbstractService(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager ();

    public List<T> findAll()
    {
        CriteriaQuery<T> cq = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    /**
     * Function find objects using all given object as template. I.e. all filled fields in this object
     * used to build where clause. 
     * 
     * @param template
     * @return 
     */
    public List<T> findByTemplate (T template) {
        Example example = Example.create(template).ignoreCase().enableLike(MatchMode.ANYWHERE);
        return getHibernateSession().createCriteria(entityClass).add (example).list();
    }

    public Session getHibernateSession () {
        return getEntityManager().unwrap(Session.class);
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }
}
