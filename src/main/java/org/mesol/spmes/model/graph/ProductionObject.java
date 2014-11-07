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

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import org.mesol.spmes.consts.BasicConstants;
import org.mesol.spmes.model.graph.attr.PSAttribute;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Entity
//@DiscriminatorColumn(name = "OBJ_TYPE", discriminatorType = DiscriminatorType.STRING, length = 10)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class ProductionObject extends Vertex 
{
    @Id
    @SequenceGenerator(initialValue = 1, name = "pobjId", sequenceName = "POBJ_SEQ", allocationSize = BasicConstants.SEQ_ALLOCATION_SIZE)
    @GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "pobjId")
    private Long                        id;

    /**
     * List of outgoing edges (operations)
     * Outgoing edges available only if this production object 
     * already finished and ready to move further
     */
    @OneToMany(mappedBy = "from")
    private Set<ProdOper>               out;
    /**
     * List of incoming edges (operations)
     */
    @OneToMany(mappedBy = "to")
    private Set<ProdOper>               in;
    /**
     * Name of this objects
     */
    @Column(nullable = false, length = 32)
    private String                      name;
    /**
     * reference to production order
     */
    @ManyToOne
    @JoinColumn(name = "ROUTER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_POBJ_PO", value = ConstraintMode.CONSTRAINT))
    private ProductionOrder             productionOrder;
    /**
     * Reference to corresponding router step
     */
    @ManyToOne
    @JoinColumn(name = "BOM_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_POBJ_RS", value = ConstraintMode.CONSTRAINT))
    private RouterStep                  routerStep;
    /**
     * Additional attributes for this production object
     */
    @ElementCollection
    @CollectionTable (
        name = "POBJA",
        joinColumns = @JoinColumn(name = "POBJ_ID")
    )
    private Set<PSAttribute>            attributes;

    @JsonIgnore
    public Set<ProdOper> getOut() {
        return out;
    }

    public void setOut(Set<ProdOper> out) {
        this.out = out;
    }

    public Set<ProdOper> getIn() {
        return in;
    }

    public void setIn(Set<ProdOper> in) {
        this.in = in;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProductionOrder getProductionOrder() {
        return productionOrder;
    }

    public void setProductionOrder(ProductionOrder productionOrder) {
        this.productionOrder = productionOrder;
    }

    public RouterStep getRouterStep() {
        return routerStep;
    }

    public void setRouterStep(RouterStep routerStep) {
        this.routerStep = routerStep;
    }

    public Set<PSAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<PSAttribute> attributes) {
        this.attributes = attributes;
    }
}
