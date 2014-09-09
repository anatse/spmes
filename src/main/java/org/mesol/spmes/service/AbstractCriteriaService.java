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

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.EntityType;
import org.apache.log4j.Logger;
import org.mesol.spmes.model.abs.AbstractAttribute;
import org.mesol.spmes.model.abs.AbstractEntity;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 * @param <T>
 */
public abstract class AbstractCriteriaService<T extends AbstractEntity, A extends AbstractAttribute>
{
    protected static final Logger       logger = Logger.getLogger(AbstractCriteriaService.class);
    protected static final Pattern      OPERANDS_PATTERN = Pattern.compile("<|>|=");

    private Class<T>                    entityClass;

    protected abstract EntityManager getEntityManager();
    
    protected AbstractCriteriaService (Class<T> entityClass) {
        this.entityClass = entityClass;
    }
    
    /**
     * Function used to add order by to criteria query
     * @param cq - criteria query object
     * @param builder - criteria query builder
     * @param from - root object
     * @param sortField - field used to sort
     * @param descend - flag indicates sort direction
     */
    protected void setOrderBy (CriteriaQuery<T> cq, CriteriaBuilder builder, Root<T> from, String sortField, boolean descend)
    {
        if (sortField != null)
            cq.orderBy(descend ? builder.desc(from.get(sortField)) : builder.asc(from.get(sortField)));
    }

    private void addNumberFilter (CriteriaBuilder builder, List<Predicate> preList, Path ptAttr, Object var) {
        Number numVar;
        if (var instanceof String) {
            try {
                String vs = ((String)var).replaceAll(OPERANDS_PATTERN.pattern(), "");
                if (!vs.isEmpty())
                    numVar = NumberFormat.getNumberInstance().parse(vs);
                else
                    return;

                Matcher matcher = OPERANDS_PATTERN.matcher((String)var);
                if (matcher.find()) {
                    switch (matcher.group().charAt(0)) {
                        case '<':
                            preList.add(builder.le(ptAttr, numVar));
                            break;

                        case '>':
                            preList.add(builder.ge(ptAttr, numVar));
                            break;

                        case '=':
                            preList.add(builder.equal(ptAttr, numVar));
                            break;
                    }
                }
                else {
                    preList.add(builder.equal(ptAttr, numVar));
                }
            }
            catch (ParseException ex) {
                logger.error(ex, ex);
            }
        }
        else if (var instanceof Number) {
            numVar = (Number)var;
            preList.add(builder.equal(ptAttr, numVar));
        }
    }
    
    private void addDateFilter (CriteriaBuilder builder, List<Predicate> preList, Path ptAttr, Object var) {
        Date dateVar;
        if (var instanceof String) {
            try {
                String vs = ((String)var).replaceAll(OPERANDS_PATTERN.pattern(), "");
                if (!vs.isEmpty())
                    dateVar = DateFormat.getDateTimeInstance().parse(vs);
                else
                    return;

                Matcher matcher = OPERANDS_PATTERN.matcher((String)var);
                if (matcher.find()) {
                    switch (matcher.group().charAt(0)) {
                        case '<':
                            preList.add(builder.lessThan(ptAttr, dateVar));
                            break;

                        case '>':
                            preList.add(builder.greaterThan(ptAttr, dateVar));
                            break;

                        case '=':
                            preList.add(builder.equal(ptAttr, dateVar));
                            break;
                    }
                }
                else {
                    preList.add(builder.equal(ptAttr, dateVar));
                }
            }
            catch (ParseException ex) {
                logger.error(ex, ex);
            }
        }
        else if (var instanceof Date) {
            dateVar = (Date)var;
            preList.add(builder.equal(ptAttr, dateVar));
        }
    }
    
    /**
     * Add filter to predicate list for column
     * @param builder - CriteriaBuilder object
     * @param preList - predicate list
     * @param ptAttr - attribute
     * @param var - value for filter
     */
    protected void addColumnFilter (CriteriaBuilder builder, 
                                  List<Predicate> preList,
                                  Path ptAttr,
                                  Object var)
    {
        Class<?> cl = ptAttr.getJavaType();
        if (cl.equals(String.class)) {
            if (var instanceof String) {
                if (((String)var).contains("%"))
                    preList.add(builder.like(builder.lower(ptAttr), var.toString().toLowerCase()));
                else
                    preList.add(builder.equal(builder.lower(ptAttr), var.toString().toLowerCase()));
            }
        }
        else if (Number.class.isAssignableFrom(cl)) {
            /*
             * This is numeric value
             */
            addNumberFilter(builder, preList, ptAttr, var);
        }
        else if (Date.class.isAssignableFrom(cl)) {
            /*
             * This is date value
             */
            addDateFilter(builder, preList, ptAttr, var);
        }
        else {
            preList.add(builder.equal(ptAttr, var));
        }
    }
    
    /**
     * Function used to add predicates to criteria query. This function allows follow filters:
     * <ul>
     * <li>For numeric values allowed '<', '>' and '=' operands
     * <li>For date values allowed '<', '>' and '=' operands
     * <li>For string values allowed '%' symbol. If this symbol was found then <b>like</b> operand will be used 
     * otherwise will be used <b>equals</b>
     * </ul>
     * For the
     * @param cq - criteria query object
     * @param builder - criteria query builder
     * @param from - root object
     * @param model - entity model
     * @param filters - filters map
     * @param preList - predicate list (in, out)
     */
	protected void addSimpleFilter (CriteriaQuery cq, CriteriaBuilder builder, Path<T> from, EntityType<T> model, Map<String, ?> filters, List<Predicate> preList)
    {
        if (filters != null && filters.size() > 0) {
            for (String column : filters.keySet()) {
                Object var = filters.get(column);

                /*
                 * If this is an attribute
                 */
                if (column.indexOf('[') != -1) {
                    try {
                        String attrName = column.substring(column.indexOf('[')+2, column.indexOf(']')-1);
                        Class attributesClass = from.get (NamingRuleConstants.ATTRIBUTES).getJavaType();
                        if (AbstractAttribute.class.isAssignableFrom(attributesClass)) {
                            AbstractAttribute attr = (AbstractAttribute)attributesClass.newInstance();
                            attr.setName(attrName);
                            attr.setAttrValue((String)var);
                            addAttributeFilter(cq, builder, from, Arrays.asList(attr), preList);
                        }
                    }
                    catch (InstantiationException | IllegalAccessException ex) {
                        logger.error(var, ex);
                    }

                    continue;
                }

                if (column.indexOf('.') != -1) {
                    /*
                     * This is not simple query because this field could become a reason for table join
                     */
                    String[] fields = column.split("\\.");
                    /*
                     * This is simple join. This filter allowed only simple joins
                     */
                    if (fields.length == 2) {
                        /*
                         * Get main attribute
                         */
                        Path attr = from.get(fields[0]);
                        /*
                         * If filterable column is id then just get desired object by it's identifier
                         */
                        if (fields[1].equals(NamingRuleConstants.ID)) {
                            try {
                                BigInteger id = BigInteger.valueOf(NumberFormat.getInstance().parse(var.toString()).longValue());
                                Object value = getEntityManager().find(attr.getJavaType(), id);
                                preList.add(builder.equal(from.get(fields[0]), value));
                            }
                            catch (ParseException ex) {
                                logger.error(ex, ex);
                            }
                        }
                        else {
                            /*
                             * Create subquery to find by attribute
                             */
                            Subquery subquery = cq.subquery(attr.getJavaType());
                            Root fromAttr = subquery.from(attr.getJavaType());
                            subquery.select(fromAttr.get(NamingRuleConstants.ID));
                            List<Predicate> subQueryWhere = new ArrayList<>();
                            addColumnFilter(builder, subQueryWhere, fromAttr.get(fields[1]), var);
                            subquery.where(subQueryWhere.toArray(new Predicate[subQueryWhere.size()]));

                            /*
                             * Add where to main query
                             */
                            preList.add(builder.in(from.get(fields[0]).get(NamingRuleConstants.ID)).value(subquery));
                        }
                    }
                }
                else {
                    Path ptAttr = from.get(column);
                    addColumnFilter(builder, preList, ptAttr, var);
                }
            }
        }
    }
    
    /**
     * Function used to add predicates to criteria query
     * @param cq - criteria query
     * @param builder - criteria query builder
     * @param from - root object
     * @param filters - filter map
     * @param preList - predicate list (in, out)
     */
	protected void addSimpleFilter (CriteriaQuery cq, CriteriaBuilder builder, Root<T> from, Map<String, ?> filters, List<Predicate> preList)
    {
        addSimpleFilter(cq, builder, from, from.getModel(), filters, preList);
    }

    /**
     * Function used to add subquery filter by entity attributes 
     * @param cq - criteria query
     * @param builder - criteria query builder
     * @param from - root object
     * @param attributesFilter - attributes for filter
     * @param preList  - predicates list
     */
    protected void addAttributeFilter (CriteriaQuery cq, 
                                       CriteriaBuilder builder, 
                                       Path<T> from, 
                                       List attributesFilter,
                                       List<Predicate> preList)
    {
        /*
         * Get attributes class
         */
        Class attributesClass = from.get (NamingRuleConstants.ATTRIBUTES).getJavaType();
        Subquery<A> subquery = cq.subquery(attributesClass);
        Root fromAttr = subquery.from(attributesClass);

        subquery.select(fromAttr.get(NamingRuleConstants.OWNER).get(NamingRuleConstants.ID));
        subquery.groupBy(fromAttr.get(NamingRuleConstants.OWNER).get(NamingRuleConstants.ID));
        subquery.having(builder.equal(builder.count(fromAttr.get(NamingRuleConstants.OWNER).get(NamingRuleConstants.ID)), attributesFilter.size()));

        Predicate wherePredicates = null;

        for (Object a : attributesFilter) {
            if (!(a instanceof AbstractAttribute))
                continue;

            AbstractAttribute attr = (AbstractAttribute)a;
            Predicate pp = builder.and (
                builder.equal(fromAttr.get(NamingRuleConstants.NAME), attr.getName()),
                builder.equal(
                    fromAttr.get(NamingRuleConstants.VALUE), 
                    attr.getAttrValue())
            );

            if (wherePredicates == null)
                wherePredicates = pp;
            else
                wherePredicates = builder.or(wherePredicates, pp);
        }
        
        subquery.where(wherePredicates);
        preList.add(builder.in(from.get(NamingRuleConstants.ID)).value(subquery));
    }
}
