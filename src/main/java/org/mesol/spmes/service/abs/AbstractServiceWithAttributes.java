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

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import javax.persistence.CollectionTable;
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
 * 
 * @version 1.0.0
 * @author ASementsov
 * @param <T>
 * @param <A>
 */
public abstract class AbstractServiceWithAttributes<T extends AbstractEntity, A extends AbstractAttribute> extends AbstractService<T>
{
    protected static final Pattern      OPERANDS_PATTERN = Pattern.compile("<|>|=");
    protected static final String       LIKE_PATTERN = ".*[\\%|\\_|\\?]+.*";
    private static final String         ATTR_QUERY = "select distinct e.id from {0} e, {1} a where a.{2} = e.id and (a.name, a.attrValue) = all (select a1.name, a1.attrValue from {1} a1 where a1.{2} = e.id and {3})";

    protected AbstractServiceWithAttributes (Class<T> entityClass) {
        super(entityClass);
    }
    
    protected String getTable (Class entity) {
        Session session = getHibernateSession();
        SingleTableEntityPersister md = (SingleTableEntityPersister) session.getSessionFactory().getClassMetadata(getEntityClass());
        return md.getTableName();
    }
    
    protected String getJoinColumn () {
        String table = null;

        try {
            Field field = getEntityClass().getDeclaredField(NamingRuleConstants.ATTRIBUTES);
            CollectionTable ct = field.getAnnotation(CollectionTable.class);
            if (ct != null) {
                table = ct.joinColumns()[0].name();
            }
        }
        catch (NoSuchFieldException | SecurityException ex) {
            logger.error(ex, ex);
        }
        
        return table;
    }
    
    protected String getAttributeTable () {
        String table = null;

        try {
            Field field = getEntityClass().getDeclaredField(NamingRuleConstants.ATTRIBUTES);
            CollectionTable ct = field.getAnnotation(CollectionTable.class);
            if (ct != null)
                table = ct.name();
        }
        catch (NoSuchFieldException | SecurityException ex) {
            logger.error(ex, ex);
        }
        
        return table;
    }

    protected Class<A> getAttributeClass () {
        Class<A> type = null;

        try {
            Field field = getEntityClass().getDeclaredField(NamingRuleConstants.ATTRIBUTES);
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
    
    public List<T> findByAttribute (A attr) {
        return getHibernateSession().createCriteria(getEntityClass())
            .createAlias(NamingRuleConstants.ATTRIBUTES, "attr")
            .add (
                and()
                .add(eq("attr." + NamingRuleConstants.NAME, attr.getName()))
                .add(eq("attr." + NamingRuleConstants.VALUE, attr.getAttrValue()))
            ).list();
    }

    public List<T> findByAttributes (Set<A> attrs) {
        // Build attributes where clause
        StringBuilder sb = new StringBuilder("(");
        for (int attrNo=0;attrNo<attrs.size();attrNo++) {
            if (attrNo > 0)
                sb.append(" or ");

            sb.append("(a1.name = :name").append(attrNo).append(" and a1.attrValue = :value").append(attrNo).append(")");
        }

        sb.append(")");
        String sqlQuery = MessageFormat.format(ATTR_QUERY, 
                            getTable (getEntityClass()), 
                            getAttributeTable(),
                            getJoinColumn (),
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
        objs.stream().map((obj) -> getEntityManager().find(getEntityClass(), ((Number)obj).longValue())).forEach((ent) -> {
            ret.add(ent);
        });

        return ret;
    }
}
