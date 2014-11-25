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

package org.mesol.spmes.model.graph.prod;

import org.mesol.spmes.model.graph.Edge;
import org.mesol.spmes.model.graph.states.ProdOperState;
import java.io.Serializable;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.ConstraintMode;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.mesol.spmes.model.factory.EquipmentClass;
import org.mesol.spmes.model.graph.attr.POperAttribute;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Entity
@DiscriminatorValue("poop")
public class ProdOper extends Edge implements Serializable
{
    @ElementCollection
    @CollectionTable (
        name = "OPEDGA",
        joinColumns = @JoinColumn(name = "OPER_ID")
    )
    private Set<POperAttribute>     attributes;
    @ManyToOne
    @JoinColumn(name = "EQC_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_OPO_EQC", value = ConstraintMode.CONSTRAINT))
    private EquipmentClass          equipmentClass;
    @ManyToOne
    @JoinColumn(name = "STATE_CODE", nullable = false, foreignKey = @ForeignKey(name = "FK_OPO_STATE", value = ConstraintMode.CONSTRAINT))
    private ProdOperState           status;

    public Set<POperAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<POperAttribute> attributes) {
        this.attributes = attributes;
    }

    public EquipmentClass getEquipmentClass() {
        return equipmentClass;
    }

    public void setEquipmentClass(EquipmentClass equipmentClass) {
        this.equipmentClass = equipmentClass;
    }

    public ProdOperState getStatus() {
        return status;
    }

    public void setStatus(ProdOperState status) {
        this.status = status;
    }
}
