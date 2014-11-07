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
import static org.hibernate.criterion.Restrictions.eq;
import org.mesol.spmes.model.abs.NamingRuleConstants;
import org.mesol.spmes.model.graph.ProductionOrder;
import org.mesol.spmes.model.graph.attr.POAttribute;
import org.mesol.spmes.model.refs.Quantity;
import org.mesol.spmes.service.abs.AbstractServiceWithAttributes;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Service
public class ProductionService extends AbstractServiceWithAttributes<ProductionOrder, POAttribute>
{
    private static final Logger     logger = Logger.getLogger(MethodHandles.lookup().lookupClass());

    @PersistenceContext
    private EntityManager           em;

    public ProductionService() {
        super(ProductionOrder.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Transactional
    public ProductionOrder findByName (String poName) {
        return (ProductionOrder) getHibernateSession().createCriteria(ProductionOrder.class)
            .add(eq("externalOrderId", poName))
            .uniqueResult();
    }

    @Transactional
    public void releaseProductionOrder (ProductionOrder po, Quantity qtyToRelease) {
        if (po.getId() == null)
            po = findByName (po.getExternalOrderId());
    }
}
