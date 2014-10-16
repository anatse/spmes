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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.apache.log4j.Logger;
import org.mesol.spmes.consts.BasicConstants;
import org.mesol.spmes.model.abs.AbstractEntity;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Entity
@Table(name = "EQC",  uniqueConstraints=@UniqueConstraint(columnNames={"NAME"}, name = "UK_EQC_NAME"))
public class EquipmentClass extends AbstractEntity implements Serializable
{
    @Id
    @SequenceGenerator(initialValue = 1, name = "eqcId", sequenceName = "EQC_SEQ", allocationSize = BasicConstants.SEQ_ALLOCATION_SIZE)
    @GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "eqcId")
    private Long id;

    @Column(name = "NAME", length = 32, nullable = false)
    private String          name;
    private String          description;
    
    @ManyToMany
    @JoinTable(
        name="EQC2EQ",
        joinColumns = @JoinColumn(name="EQC_ID", referencedColumnName="ID"),
        inverseJoinColumns = @JoinColumn(name="EQ_ID", referencedColumnName="ID"),
        foreignKey = @ForeignKey(name = "FK_EQC_EQ", value = ConstraintMode.CONSTRAINT),
        inverseForeignKey = @ForeignKey(name = "FK_EQ_EQC", value = ConstraintMode.CONSTRAINT)
    )
    private Set<Equipment>   equipments;

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

    public Set<Equipment> getEquipments() {
        return equipments;
    }

    public void setEquipments(Set<Equipment> equipments) {
        this.equipments = equipments;
    }
}
