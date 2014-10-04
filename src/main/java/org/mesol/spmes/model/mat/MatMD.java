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
import java.util.Set;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.mesol.spmes.consts.BasicConstants;
import org.mesol.spmes.model.abs.AbstractEntity;
import org.mesol.spmes.model.factory.Equipment;
import org.mesol.spmes.model.refs.Quantity;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Entity
@Table(name = "MMD")
public class MatMD extends AbstractEntity implements Serializable
{
    @Id
    @SequenceGenerator(initialValue = 1, name = "matMdId", sequenceName = "MATMD_SEQ", allocationSize = BasicConstants.SEQ_ALLOCATION_SIZE)
    @GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "matMdId")
    private Long                    id;

    @Column(name = "NAME", length = 32, nullable = false)
    private String                  name;

    @Column(name = "EXTERNAL_ID", length = 180, nullable = true)
    private String                  extrnalId;

    @ManyToOne
    @JoinColumn(name = "SITE_ID", foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_MATMD_EQ"))
    private Equipment               site;

    @Column(name = "DESCRIPTION", length = 255)
    private String                  description;

    @ManyToOne
    @JoinColumn(name = "STATUS", foreignKey = @ForeignKey(name = "FK_MSTMD_STATUS", value = ConstraintMode.CONSTRAINT))
    private MatMdStatus             status;
    
    @Column(name = "UNIT_CODE", length = 32)
    private String                  unit;

    @Embedded
    @AttributeOverrides ({
        @AttributeOverride(name="quantity", column = @Column(name = "WIDTH", precision = 38, scale = 6)),
        @AttributeOverride(name="unitCode", column = @Column(name = "WIDTH_UNIT", length = 32))
    })
    private Quantity                width;

    @AttributeOverrides ({
        @AttributeOverride(name="quantity", column = @Column(name = "DIA", precision = 38, scale = 6)),
        @AttributeOverride(name="unitCode", column = @Column(name = "DIA_UNIT", length = 32))
    })
    private Quantity                diameter;

    @AttributeOverrides ({
        @AttributeOverride(name="quantity", column = @Column(name = "LEN", precision = 38, scale = 6)),
        @AttributeOverride(name="unitCode", column = @Column(name = "LEN_UNIT", length = 32))
    })
    private Quantity                length;

    @ElementCollection
    @CollectionTable (
        name = "MMDA",
        joinColumns = @JoinColumn(name = "MMD_ID")
    )
    private Set<MatMDAttribute>     attributes;

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

    public String getExtrnalId() {
        return extrnalId;
    }

    public void setExtrnalId(String extrnalId) {
        this.extrnalId = extrnalId;
    }

    public Equipment getSite() {
        return site;
    }

    public void setSite(Equipment site) {
        this.site = site;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MatMdStatus getStatus() {
        return status;
    }

    public void setStatus(MatMdStatus status) {
        this.status = status;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Quantity getWidth() {
        return width;
    }

    public void setWidth(Quantity width) {
        this.width = width;
    }

    public Quantity getDiameter() {
        return diameter;
    }

    public void setDiameter(Quantity diameter) {
        this.diameter = diameter;
    }

    public Quantity getLength() {
        return length;
    }

    public void setLength(Quantity length) {
        this.length = length;
    }

    public Set<MatMDAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<MatMDAttribute> attributes) {
        this.attributes = attributes;
    }
}
