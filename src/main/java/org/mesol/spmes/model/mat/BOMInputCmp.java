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
package org.mesol.spmes.model.mat;

import java.io.Serializable;
import javax.persistence.ConstraintMode;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Entity
@DiscriminatorValue("IN")
public class BOMInputCmp extends BOMComponent implements Serializable
{
    @ManyToOne
    @JoinColumn(name = "BOM_ID", foreignKey = @ForeignKey(name = "FK_BOMC_BOM", value = ConstraintMode.CONSTRAINT))
    private BOM                     bom;

    @Override
    public BOM getBom() {
        return bom;
    }

    @Override
    public void setBom(BOM bom) {
        this.bom = bom;
    }
}
