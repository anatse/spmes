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
package org.mesol.spmes.model.abs;

import javax.persistence.ConstraintMode;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.mesol.spmes.model.factory.Site;

/**
 * Base entity for site objects 
 * @version 1.0.0
 * @author ASementsov
 */
public abstract class BaseSiteObject extends AbstractEntity
{
    /**
     * Site
     */
    @ManyToOne
    @JoinColumn(name = "SITE_ID", foreignKey = @ForeignKey(name = "FK_BSO_SITE", value = ConstraintMode.CONSTRAINT), nullable = false)
    private Site            site;

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }
}
