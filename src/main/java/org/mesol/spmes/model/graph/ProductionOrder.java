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
package org.mesol.spmes.model.graph;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
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
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;
import org.mesol.spmes.consts.BasicConstants;
import org.mesol.spmes.model.abs.AbstractEntity;
import org.mesol.spmes.model.graph.attr.POAttribute;
import org.mesol.spmes.model.graph.states.ProdOrderState;
import org.mesol.spmes.model.mat.MatMD;
import org.mesol.spmes.model.refs.Quantity;

/**
 * Class represents production order data
 * @version 1.0.0
 * @author ASementsov
 */
@Entity
@Table(name = "PO")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class ProductionOrder extends AbstractEntity implements Serializable
{
    /**
     * Primary key
     */
    @Id
    @SequenceGenerator(initialValue = 1, name = "poId", sequenceName = "PO_SEQ", allocationSize = BasicConstants.SEQ_ALLOCATION_SIZE)
    @GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "poId")
    private Long                    id;
    /**
     * Final product material master data
     */
    @ManyToOne
    @JoinColumn (name = "MAT_MD_ID", foreignKey = @ForeignKey(name = "FK_PO_MATMD", value = ConstraintMode.CONSTRAINT))
    private MatMD                   product;
    /**
     * Production order quantity. This field cannot be changed
     */
    @Embedded
    @AttributeOverrides ({
        @AttributeOverride(name="quantity", column = @Column(name = "QTY", precision = 38, scale = 6)),
        @AttributeOverride(name="unitCode", column = @Column(name = "QTY_UNIT", length = 32))
    })
    private Quantity                poQty;
    /**
     * Planned product quantity
     */
    @Embedded
    @AttributeOverrides ({
        @AttributeOverride(name="quantity", column = @Column(name = "PLAN_QTY", precision = 38, scale = 6)),
        @AttributeOverride(name="unitCode", column = @Column(name = "PLAN_QTY_UNIT", length = 32))
    })
    private Quantity                plannedQty;
    /**
     * Default patch quantity
     */
    @Embedded
    @AttributeOverrides ({
        @AttributeOverride(name="quantity", column = @Column(name = "DEF_BATCH_QTY", precision = 38, scale = 6)),
        @AttributeOverride(name="unitCode", column = @Column(name = "DEF_BATCH_QTY_UNIT", length = 32))
    })
    private Quantity                defaultBatchQty;
    /**
     * Completed product quantity
     */
    @Embedded
    @AttributeOverrides ({
        @AttributeOverride(name="quantity", column = @Column(name = "DONE_QTY", precision = 38, scale = 6)),
        @AttributeOverride(name="unitCode", column = @Column(name = "DONE_QTY_UNIT", length = 32))
    })
    private Quantity                doneQty;
    /**
     * Scrapped product quantity
     */
    @Embedded
    @AttributeOverrides ({
        @AttributeOverride(name="quantity", column = @Column(name = "SCRAP_QTY", precision = 38, scale = 6)),
        @AttributeOverride(name="unitCode", column = @Column(name = "SCRAP_QTY_UNIT", length = 32))
    })
    private Quantity                scrapQty;
    /**
     * Unmanaged product quantity. It means quantity of product which not managed of production order object.
     * E.g. production managed by production batches - InWork production
     */
    @Embedded
    @AttributeOverrides ({
        @AttributeOverride(name="quantity", column = @Column(name = "UMAN_QTY", precision = 38, scale = 6)),
        @AttributeOverride(name="unitCode", column = @Column(name = "UMAN_QTY_UNIT", length = 32))
    })
    private Quantity                unmanagedQty;
    /**
     * Reference to default router
     */
    @ManyToOne
    @JoinColumn (name = "ROUTER_ID", foreignKey = @ForeignKey(name = "FK_PO_ROUTER", value = ConstraintMode.CONSTRAINT))
    private Router                  router;
    /**
     * Reference to external production order (e.g. ERP shop order)
     */
    @Column(name = "EXT_ORDER_ID")
    private String                  externalOrderId;
    /**
     * Current production order status
     */
    @ManyToOne
    @JoinColumn (name = "STATE_CODE", foreignKey = @ForeignKey(name = "FK_PO_STATE", value = ConstraintMode.CONSTRAINT))
    private ProdOrderState          status;
    /**
     * Planned completion date
     */
    @Column(name = "PL_FIN_DATE")
    private Timestamp               planFinishDate;
    /**
     * Planned starting date
     */
    @Column(name = "PL_START_DATE")
    private Timestamp               planStartDate;
    /**
     * Actual completion date
     */
    @Column(name = "FACT_FIN_DATE")
    private Timestamp               factFinishDate;
    /**
     * Actual starting date
     */
    @Column(name = "FACT_START_DATE")
    private Timestamp               factStartDate;
    /**
     * Additional production order attributes
     */
    @ElementCollection
    @CollectionTable (
        name = "POA",
        joinColumns = @JoinColumn(name = "PO_ID")
    )
    private Set<POAttribute>        attributes;

    /**
     * field used to implement optimistic locking
     */
    @Version
    @Column(name = "OPT_LOCK")
    private Long                    optLock;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MatMD getProduct() {
        return product;
    }

    public void setProduct(MatMD product) {
        this.product = product;
    }

    public Quantity getPoQty() {
        return poQty;
    }

    public void setPoQty(Quantity poQty) {
        this.poQty = poQty;
    }

    public Quantity getPlannedQty() {
        return plannedQty;
    }

    public void setPlannedQty(Quantity plannedQty) {
        this.plannedQty = plannedQty;
    }

    public Quantity getDefaultBatchQty() {
        return defaultBatchQty;
    }

    public void setDefaultBatchQty(Quantity defaultBatchQty) {
        this.defaultBatchQty = defaultBatchQty;
    }

    public Quantity getDoneQty() {
        return doneQty;
    }

    public void setDoneQty(Quantity doneQty) {
        this.doneQty = doneQty;
    }

    public Quantity getScrapQty() {
        return scrapQty;
    }

    public void setScrapQty(Quantity scrapQty) {
        this.scrapQty = scrapQty;
    }

    public Quantity getUnmanagedQty() {
        return unmanagedQty;
    }

    public void setUnmanagedQty(Quantity unmanagedQty) {
        this.unmanagedQty = unmanagedQty;
    }

    public Router getRouter() {
        return router;
    }

    public void setRouter(Router router) {
        this.router = router;
    }

    public String getExternalOrderId() {
        return externalOrderId;
    }

    public void setExternalOrderId(String externalOrderId) {
        this.externalOrderId = externalOrderId;
    }

    public ProdOrderState getStatus() {
        return status;
    }

    public void setStatus(ProdOrderState status) {
        this.status = status;
    }

    public Timestamp getPlanFinishDate() {
        return planFinishDate;
    }

    public void setPlanFinishDate(Timestamp planFinishDate) {
        this.planFinishDate = planFinishDate;
    }

    public Timestamp getPlanStartDate() {
        return planStartDate;
    }

    public void setPlanStartDate(Timestamp planStartDate) {
        this.planStartDate = planStartDate;
    }

    public Timestamp getFactFinishDate() {
        return factFinishDate;
    }

    public void setFactFinishDate(Timestamp factFinishDate) {
        this.factFinishDate = factFinishDate;
    }

    public Timestamp getFactStartDate() {
        return factStartDate;
    }

    public void setFactStartDate(Timestamp factStartDate) {
        this.factStartDate = factStartDate;
    }

    public Set<POAttribute> getAttributes() {
        if (attributes == null)
            attributes = new HashSet<>();
        
        return attributes;
    }

    public void setAttributes(Set<POAttribute> attributes) {
        this.attributes = attributes;
    }

    public Long getOptLock() {
        return optLock;
    }

    public void setOptLock(Long optLock) {
        this.optLock = optLock;
    }
}
