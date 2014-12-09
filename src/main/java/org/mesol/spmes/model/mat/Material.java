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
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.ElementCollection;
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
import org.mesol.spmes.model.abs.BaseSiteObject;
import org.mesol.spmes.model.refs.Quantity;

/**
 * Class represents material
 * @version 1.0.0
 * @author ASementsov
 */
@Entity
@Table(name = "MAT")
public class Material extends BaseSiteObject implements Serializable
{
    @Id
    @SequenceGenerator(initialValue = 1, name = "materialId", sequenceName = "MAT_SEQ", allocationSize = BasicConstants.SEQ_ALLOCATION_SIZE)
    @GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "materialId")
    private Long                        id;

    @ManyToOne
    @JoinColumn(name = "MATMD_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_MAT_MATMD", value = ConstraintMode.CONSTRAINT))
    private MatMD                       matMd;

    @Column(name = "MAT_NR", length = 255, nullable = false)
    private String                      materialNumber;

    @Column(name = "EXT_MAT_NR", length = 255)
    private String                      extMatNumber;

    @Column(name = "STATUS", length = 10, nullable = false)
    private String                      status;

    @Column(name = "LOC", length = 255)
    private String                      storageLocation;

    @Column(name = "STG_SYS", length = 255)
    private String                      storageSystem;

    private Quantity                    qty;

    @ElementCollection
    @CollectionTable (
        name = "MATA",
        joinColumns = @JoinColumn(name = "MAT_ID")
    )
    private Set<MaterialAttribute>     attributes;
    
//    @ManyToOne
//    @JoinColumn(name = "PO_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_MAT_PO", value = ConstraintMode.CONSTRAINT))
//    private ProductionOrder             ownedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MatMD getMatMd() {
        return matMd;
    }

    public void setMatMd(MatMD matMd) {
        this.matMd = matMd;
    }

    public String getMaterialNumber() {
        return materialNumber;
    }

    public void setMaterialNumber(String materialNumber) {
        this.materialNumber = materialNumber;
    }

    public String getExtMatNumber() {
        return extMatNumber;
    }

    public void setExtMatNumber(String extMatNumber) {
        this.extMatNumber = extMatNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public String getStorageSystem() {
        return storageSystem;
    }

    public void setStorageSystem(String storageSystem) {
        this.storageSystem = storageSystem;
    }

    public Quantity getQty() {
        return qty;
    }

    public void setQty(Quantity qty) {
        this.qty = qty;
    }

//    public ProductionOrder getOwnedBy() {
//        return ownedBy;
//    }
//
//    public void setOwnedBy(ProductionOrder ownedBy) {
//        this.ownedBy = ownedBy;
//    }

    public Set<MaterialAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<MaterialAttribute> attributes) {
        this.attributes = attributes;
    }
}
