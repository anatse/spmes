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
import org.mesol.spmes.service.abs.AbstractServiceWithAttributes;
import org.springframework.stereotype.Service;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Service
public class MaterialService extends AbstractServiceWithAttributes
{
    private static final            Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass());

    @PersistenceContext
    private EntityManager           em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
