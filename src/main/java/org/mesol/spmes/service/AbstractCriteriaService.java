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

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.log4j.Logger;
import org.mesol.spmes.model.abs.AbstractAttribute;
import org.mesol.spmes.model.abs.AbstractEntity;

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

    protected abstract EntityManager getEntityManager();
    
    protected AbstractCriteriaService (Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected Number toNumber (String value) {
        Number numVar = null;

        try {
            String vs = ((String)value).replaceAll(OPERANDS_PATTERN.pattern(), "");
            if (!vs.isEmpty())
                numVar = NumberFormat.getNumberInstance().parse(vs);
        } 
        catch (ParseException ex) {
            logger.error(ex, ex);
        }

        return numVar;
    }
    
    protected Date toDate (String value) {
        Date dateVar = null;

        try {
            String vs = ((String)value).replaceAll(OPERANDS_PATTERN.pattern(), "");
            if (!vs.isEmpty())
                dateVar = DateFormat.getDateTimeInstance().parse(vs);
        } 
        catch (ParseException ex) {
            logger.error(ex, ex);
        }

        return dateVar;
    }

    protected Predicate buildNumberPredicate (
        final CriteriaBuilder builder,
        final Path fieldPath,
        Object value
    ) {
        Predicate pd = null;
        if (value instanceof String) {
            Number numVar = toNumber ((String)value);
            if (numVar == null)
                return null;

            Matcher matcher = OPERANDS_PATTERN.matcher((String)value);
            if (matcher.find()) {
                switch (matcher.group().charAt(0)) {
                    case '<':
                        pd = builder.le(fieldPath, numVar);
                        break;

                    case '>':
                        pd = builder.ge(fieldPath, numVar);
                        break;

                    case '=':
                        pd = builder.equal(fieldPath, numVar);
                        break;
                }
            }
            else {
                pd = builder.equal(fieldPath, numVar);
            }
        }
        else if (value instanceof Number) {
            pd = builder.equal(fieldPath, (Number)value);
        }

        return pd;
    }

    protected Predicate buildDatePredicate(
        final CriteriaBuilder builder, 
        final Path fieldPath, 
        final Object value
    ) {
        Predicate pd = null;
        if (value instanceof String) {
            Date dateVar = toDate((String)value);
            Matcher matcher = OPERANDS_PATTERN.matcher((String)value);
            if (matcher.find()) {
                switch (matcher.group().charAt(0)) {
                    case '<':
                        pd = builder.lessThan(fieldPath, dateVar);
                        break;

                    case '>':
                        pd = builder.greaterThan(fieldPath, dateVar);
                        break;

                    case '=':
                        pd = builder.equal(fieldPath, dateVar);
                        break;
                }
            }
            else {
                pd = builder.equal(fieldPath, dateVar);
            }
        }
        else if (value instanceof Date) {
            pd = builder.equal(fieldPath, (Date)value);
        }

        return pd;
    }

    protected Predicate buildColumnPredicate (
        final CriteriaBuilder builder,
        final Root<T> root,
        final String field,
        final Object value) {
        Predicate pd = null;
        final Path fieldPath = root.get(field);
        final Class<?> fieldClass = fieldPath.getJavaType();

        if (value == null) {
            pd = builder.isNull (root.get(field));
        }
        else if (String.class.isAssignableFrom(fieldClass)) {
            if (((String)value).matches(LIKE_PATTERN)) {
                pd = builder.like(builder.lower(root.get(field)), value.toString().toLowerCase());
            }
            else {
                pd = builder.equal(builder.lower(root.get(field)), value.toString().toLowerCase());
            }
        }
        else if (Number.class.isAssignableFrom(fieldClass)) {
            pd = buildNumberPredicate(builder, fieldPath, value);
        }
        else if (value instanceof Date) {
            pd = buildDatePredicate(builder, fieldPath, value);
        }

        return pd;
    }
    
    protected List<Predicate> buildFilters (
        CriteriaBuilder builder,
        Root<T> root,
        FilterValue ... filters
    ) {
        List<Predicate> predicates = new ArrayList<>();
        for (FilterValue fv : filters) {
            final Predicate pd;
            if (fv.value == null) {
                pd = builder.isNull (root.get(fv.field));
            }
            else {
                pd = buildColumnPredicate(builder, root, fv.field, fv.value);
            }

            if (pd != null)
                predicates.add(pd);
        }
        return predicates;
    }
    
    protected CriteriaQuery<T> buildQuery (
        OrderField[] orderFields,
        FilterValue ... filters
    ) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> cq = builder.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);
        cq.select(root);

        if (filters.length != 0) {
            List<Predicate> predicates = buildFilters(builder, root, filters);
            cq.where(predicates.toArray(new Predicate[predicates.size()]));
        }

        if (orderFields.length > 0) {
            List<Order> orders = new ArrayList<>();
            for (OrderField orderField : orderFields) {
                orders.add(orderField.desc ? builder.desc(root.get(orderField.field)) : builder.asc(root.get(orderField.field)));
            }

            cq.orderBy (orders);
        }

        return cq;
    }
    
    public List<T> findFilteredRange (
        int first,
        int pageSize,
        OrderField[] orderFields,
        FilterValue ... filters
    ) {
        CriteriaQuery<T> cq = buildQuery(orderFields, filters);
        TypedQuery<T> q = getEntityManager().createQuery(cq);

        System.out.println (q.unwrap(org.hibernate.Query.class).getQueryString());

        q.setMaxResults(pageSize);
        q.setFirstResult(first);
        return q.getResultList();
    }
    
    public List<T> findFiltered (
        OrderField[] orderFields,
        FilterValue ... filters
    ) {
        CriteriaQuery<T> cq = buildQuery(orderFields, filters);
        TypedQuery<T> q = getEntityManager().createQuery(cq);
        return q.getResultList();
    }

    public T findSingleObject (
        OrderField[] orderFields,
        FilterValue ... filters
    ) {
        CriteriaQuery<T> cq = buildQuery(orderFields, filters);
        TypedQuery<T> q = getEntityManager().createQuery(cq);
        return q.getSingleResult();
    }

    public T find(Object id)
    {
        return getEntityManager().find(entityClass, id);
    }

    public List<T> findAll()
    {
        CriteriaQuery<T> cq = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    public static final class OrderField  {
        public final String         field;
        public final boolean        desc;

        public OrderField(String field, boolean desc) {
            this.field = field;
            this.desc = desc;
        }
    }

    public static final class FilterValue {
        public final String         field;
        public final Object         value;

        public FilterValue(String field, Object value) {
            this.field = field;
            this.value = value;
        }
    }
}
