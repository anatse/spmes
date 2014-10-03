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
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Entity
@Table(name = "BOMC")
public class BOMComponent extends AbstractEntity implements Serializable
{
    @Id
    @SequenceGenerator(initialValue = 1, name = "bomcId", sequenceName = "BOMC_SEQ", allocationSize = BasicConstants.SEQ_ALLOCATION_SIZE)
    @GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "bomcId")
    private Long                    id;
    @ManyToOne
    @JoinColumn(name = "BOM_ID", foreignKey = @ForeignKey(name = "FK_BOMC_BOM", value = ConstraintMode.CONSTRAINT))
    private BOM                     bom;
    @Enumerated(EnumType.STRING)
    private ComponentDirection      direction;
    @ManyToOne
    @JoinColumn(name = "MAT_MD_ID", foreignKey = @ForeignKey(name = "FK_BOMC_MMD", value = ConstraintMode.CONSTRAINT))
    private MatMD                   matMd;
    @Column(name = "QTY")
    private Double                  quantity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BOM getBom() {
        return bom;
    }

    public void setBom(BOM bom) {
        this.bom = bom;
    }

    public ComponentDirection getDirection() {
        return direction;
    }

    public void setDirection(ComponentDirection direction) {
        this.direction = direction;
    }

    public MatMD getMatMd() {
        return matMd;
    }

    public void setMatMd(MatMD matMd) {
        this.matMd = matMd;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }
}
