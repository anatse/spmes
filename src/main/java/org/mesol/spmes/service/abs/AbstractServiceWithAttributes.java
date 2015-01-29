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
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.EntityManager;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import static org.hibernate.criterion.Restrictions.and;
import static org.hibernate.criterion.Restrictions.eq;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.mesol.spmes.model.abs.AbstractAttribute;
import org.mesol.spmes.model.abs.AbstractEntity;
import org.mesol.spmes.model.abs.NamingRuleConstants;

/**
 * Abstract template for service
 * @version 1.0.0
 * @author ASementsov
 */
public abstract class AbstractServiceWithAttributes
{
    private static final Logger         logger = Logger.getLogger(MethodHandles.lookup().lookupClass());
    private static final String         ATTR_QUERY = "select distinct e.id from {0} e, {1} a where a.{2} = e.id and (a.name, a.attrValue) = all (select a1.name, a1.attrValue from {1} a1 where a1.{2} = e.id and {3})";

    protected abstract EntityManager getEntityManager ();

    /**
     * Function unwrap hibernate session
     * @return hibernate session
     */
    public Session getHibernateSession () {
        return getEntityManager().unwrap(Session.class);
    }

    /**
     * Function get entity table from entity class
     * @param <T> entity class template
     * @param entity entity class
     * @return table name for entity
     */
    protected <T extends AbstractEntity> String getEntityTable (Class<T> entity) {
        Session session = getHibernateSession();
        SingleTableEntityPersister md = (SingleTableEntityPersister)session.getSessionFactory().getClassMetadata(entity);
        return md.getTableName();
    }
    
    /**
     * Function find entity attributes CollectionTable annotation
     * @param <T> Entity type
     * @param entity entity class
     * @return CollectionTable object
     */
    protected <T extends AbstractEntity> CollectionTable getEntityAttributes (Class<T> entity) {
        try {
            Field field = entity.getDeclaredField(NamingRuleConstants.ATTRIBUTES);
            CollectionTable ct = field.getAnnotation(CollectionTable.class);
            return ct;
        }
        catch (NoSuchFieldException | SecurityException ex) {
            logger.error(ex, ex);
        }
        
        return null;
    }

    /**
     * Function find attribute class for entity
     * @param <A> attribute class type
     * @param entity entity class
     * @return attribute class
     */
    protected <A extends AbstractAttribute> Class<A> getAttributeClass (Class entity) {
        Class<A> type = null;

        try {
            Field field = entity.getDeclaredField(NamingRuleConstants.ATTRIBUTES);
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
    
    /**
     * Function find entity by its attribute
     * @param <T> Entity type
     * @param <A> Attribute type
     * @param attr attribute used to find entity
     * @param entity entity class
     * @return found entities
     */
    protected <T extends AbstractEntity, A extends AbstractAttribute> List<T> findByAttribute (A attr, Class<T> entity) {
        return getHibernateSession().createCriteria(entity)
            .createAlias(NamingRuleConstants.ATTRIBUTES, "attr")
            .add (
                and()
                .add(eq("attr." + NamingRuleConstants.NAME, attr.getName()))
                .add(eq("attr." + NamingRuleConstants.VALUE, attr.getAttrValue()))
            )
            .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
            .list();
    }

    /**
     * Function build and execute ANSI SQL query to find entity object by it's attributes.
     * Query uses all= (select a1 = ? or a2 = ? ...) operand to match all attributes at once
     * 
     * @param <T> Entity type
     * @param <A> Attribute type
     * @param attrs attributes used to find entity
     * @param entity entity class
     * @return found entities
     */
    protected <T extends AbstractEntity, A extends AbstractAttribute> List<T> findByAttributes (Set<A> attrs, Class<T> entity) {
        // Build attributes where clause
        StringBuilder sb = new StringBuilder("(");
        for (int attrNo=0;attrNo<attrs.size();attrNo++) {
            if (attrNo > 0)
                sb.append(" or ");

            sb.append("(a1.name = :name").append(attrNo).append(" and a1.attrValue = :value").append(attrNo).append(")");
        }

        CollectionTable attrTable = getEntityAttributes(entity);
        
        sb.append(")");
        String sqlQuery = MessageFormat.format(ATTR_QUERY, 
                            getEntityTable (entity), 
                            attrTable.name(),
                            attrTable.joinColumns()[0].name(),
                            sb.toString());
        SQLQuery query = getHibernateSession().createSQLQuery(sqlQuery);

        int attrNo = 0;
        for (A attr : attrs) {
            query.setParameter("name" + attrNo, attr.getName());
            query.setParameter("value" + attrNo, attr.getAttrValue());
            attrNo++;
        }

        // Add entity class
        final List<Object> objs = query.list();
        final List<T> ret = new LinkedList<>();
        objs.stream().map((obj) -> getEntityManager().find(entity, ((Number)obj).longValue())).forEach((ent) -> {
            ret.add(ent);
        });

        return ret;
    }
    
    public <T extends AbstractEntity> List<T> findAll (Class<T> clazz) {
        return getHibernateSession()
            .createCriteria(clazz)
            .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
            .list();
    }
}
