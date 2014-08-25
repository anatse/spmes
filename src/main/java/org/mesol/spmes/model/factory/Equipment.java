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

import java.io.Serializable;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.apache.log4j.Logger;
import org.mesol.spmes.model.abs.AbstractEntity;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Entity
@Table(name = "EQ", uniqueConstraints=@UniqueConstraint(columnNames={"NAME"}))
public class Equipment extends AbstractEntity implements Serializable
{
    private static final Logger     logger = Logger.getLogger(Equipment.class);

    @Id
    @SequenceGenerator(initialValue = 1, name = "eqId", sequenceName = "EQ_SEQ")
    @GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "eqId")
    private Long                    id;
    @Column(name = "NAME", length = 32, nullable = false)
    private String                  name;
    @Column(name = "DESCRIPTION", length = 255)
    private String                  description;
    @ManyToMany(mappedBy = "equipments")
    private Set<EquipmentClass>     equipmentClasses;
    @ManyToOne
    @JoinColumn(name="PARENT_ID", foreignKey = @ForeignKey(name = "FK_EQ_PARENT", value = ConstraintMode.CONSTRAINT))
    private Equipment               parentEquipment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<EquipmentClass> getEquipmentClasses() {
        return equipmentClasses;
    }

    public void setEquipmentClasses(Set<EquipmentClass> equipmentClasses) {
        this.equipmentClasses = equipmentClasses;
    }

    public Equipment getParentEquipment() {
        return parentEquipment;
    }

    public void setParentEquipment(Equipment parentEquipment) {
        this.parentEquipment = parentEquipment;
    }
}
