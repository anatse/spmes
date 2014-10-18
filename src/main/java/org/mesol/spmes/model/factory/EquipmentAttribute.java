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
package org.mesol.spmes.model.factory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import org.hibernate.annotations.Parent;
import org.mesol.spmes.model.abs.AbstractAttribute;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Embeddable
public class EquipmentAttribute extends AbstractAttribute
{   
    @Parent
    @Column(name = "EQ_ID")
    private Equipment           owner;

    @JsonIgnore
    public Equipment getOwner() {
        return owner;
    }

    public void setOwner(Equipment owner) {
        this.owner = owner;
    }
}
