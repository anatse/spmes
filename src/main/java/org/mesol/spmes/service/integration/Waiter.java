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

package org.mesol.spmes.service.integration;

import java.lang.invoke.MethodHandles;
import org.apache.log4j.Logger;
import org.mesol.spmes.model.factory.Equipment;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
public class Waiter 
{
    private static final Logger     logger = Logger.getLogger(MethodHandles.lookup().lookupClass());
    private Equipment eq;
 	public Waiter prepareDelivery(Equipment eq) {
        if (logger.isInfoEnabled())
            logger.info("Equipment: " + eq.getName());

        this.eq = eq;
		return this;
	}

    @Override
    public String toString() {
        return "Waiter{" + "eq=" + (eq != null ? eq.toString() : "null") + '}';
    }
}
