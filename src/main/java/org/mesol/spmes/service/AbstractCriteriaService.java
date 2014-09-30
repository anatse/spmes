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

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import javax.persistence.NamedNativeQuery;
import javax.persistence.criteria.CriteriaQuery;
import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.mesol.spmes.model.abs.AbstractAttribute;
import org.mesol.spmes.model.abs.AbstractEntity;
import org.mesol.spmes.model.abs.NamingRuleConstants;
import static org.hibernate.criterion.Restrictions.*;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 * @param <T>
 * @param <A>
 */
public abstract class AbstractCriteriaService<T extends AbstractEntity, A extends AbstractAttribute>
{
    protected static final Logger       logger = Logger.getLogger(AbstractCriteriaService.class);
    protected static final Pattern      OPERANDS_PATTERN = Pattern.compile("<|>|=");
    protected static final String       LIKE_PATTERN = ".*[\\%|\\_|\\?]+.*";

    private final Class<T>              entityClass;
//    private final Class<A>              attributeClass;

    protected abstract EntityManager getEntityManager();
    
    protected AbstractCriteriaService (Class<T> entityClass) {
        this.entityClass = entityClass;
//        attributeClass = getAttributeClass();
    }

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

    protected Class<A> getAttributeClass () {
        Class<A> type = null;

        try {
            Field field = entityClass.getDeclaredField(NamingRuleConstants.ATTRIBUTES);
            if (!field.isAccessible())
                field.setAccessible(true);

            if (Collection.class.isAssignableFrom(field.getType())) {
                Type genericFieldType = field.getGenericType();
                if(genericFieldType instanceof ParameterizedType){
                    ParameterizedType aType = (ParameterizedType) genericFieldType;
                    Type[] fieldArgTypes = aType.getActualTypeArguments();
                    for (Type fieldArgType : fieldArgTypes){
                        type = (Class) fieldArgType;
                        break;
                    }
                }
            }
        }
        catch (NoSuchFieldException | SecurityException ex) {
            logger.error(ex, ex);
        }
        
        return type;
    }
    
    public List<T> findByAttribute (AbstractAttribute attr) {
        return getHibernateSession().createCriteria(entityClass)
            .createAlias(NamingRuleConstants.ATTRIBUTES, "attr")
            .add (
                and()
                .add(eq("attr." + NamingRuleConstants.NAME, attr.getName()))
                .add(eq("attr." + NamingRuleConstants.VALUE, attr.getAttrValue()))
            ).list();
    }

    public List<T> findByAttributes (List<A> attrs) {
        final String namedQueryName = "findByAttributes";
        NamedNativeQuery nquery = entityClass.getAnnotation(NamedNativeQuery.class);
        if (nquery == null || !nquery.name().contains(namedQueryName)) 
            return Collections.EMPTY_LIST;

        String sqlQuery = nquery.query();
        // Build attributes where clause
        StringBuilder sb = new StringBuilder("(");
        for (int attrNo=0;attrNo<attrs.size();attrNo++) {
            if (attrNo > 0)
                sb.append(" or ");

            sb.append("(name = :name").append(attrNo).append(" and attrvalue = :value").append(attrNo).append(")");
        }

        sb.append(")");
        sqlQuery = String.format(sqlQuery, sb.toString());
        SQLQuery query = getHibernateSession().createSQLQuery(sqlQuery);

        int attrNo = 0;
        for (A attr : attrs) {
            query.setParameter("name" + attrNo, attr.getName());
            query.setParameter("value" + attrNo, attr.getAttrValue());
            attrNo++;
        }

        final List<Object> objs = query.list();
        final List<T> ret = new LinkedList<>();
        objs.stream().map((obj) -> getEntityManager().find(entityClass, ((Number)obj).longValue())).forEach((ent) -> {
            ret.add(ent);
        });

        return ret;
    }
}
