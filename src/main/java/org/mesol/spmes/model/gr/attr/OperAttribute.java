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

package org.mesol.spmes.model.gr.attr;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.AssociationOverride;
import javax.persistence.ConstraintMode;
import javax.persistence.Embeddable;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import org.hibernate.annotations.Parent;
import org.mesol.spmes.model.abs.AbstractAttribute;
import org.mesol.spmes.model.gr.RouterOperation;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Embeddable
@AssociationOverride(
    name="unit", 
    joinColumns=@JoinColumn(name="UNIT_CODE", foreignKey = @ForeignKey(name = "FK_OPEGA_UNIT", value = ConstraintMode.CONSTRAINT))
)
public class OperAttribute extends AbstractAttribute
{
    @Parent
    private RouterOperation     owner;

    @JsonIgnore
    public RouterOperation getOwner() {
        return owner;
    }

    @JsonIgnore
    public void setOwner(RouterOperation owner) {
        this.owner = owner;
    }
}
