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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;
import org.mesol.spmes.consts.BasicConstants;
import org.mesol.spmes.model.abs.AbstractEntity;
import org.mesol.spmes.model.factory.Equipment;
import org.mesol.spmes.model.graph.attr.RouterAttribute;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Entity
@Table(name = "ROUTER")
public class Router extends AbstractEntity implements Serializable
{
    @Id
    @SequenceGenerator(initialValue = 1, name = "routerId", sequenceName = "ROUTER_SEQ", allocationSize = BasicConstants.SEQ_ALLOCATION_SIZE)
    @GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "routerId")
    private Long                    id;

    @ManyToOne
    @JoinColumn(name = "EQ_ID", foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_ROUTER_EQO"))
    private Equipment               owner;

    @OneToOne
    @JoinColumn (name = "OPER_ID", foreignKey = @ForeignKey(name = "FK_ROUTER_FOPER", value = ConstraintMode.CONSTRAINT))
    private OperEdge                firstOper;
    
    @Version
    private Long                    version;
    
    @Column(length = 32, nullable = false)
    private String                  name;
    
    @ElementCollection
    @CollectionTable (
        name = "ROUTERA",
        joinColumns = @JoinColumn(name = "ROUTER_ID")
    )
    private Set<RouterAttribute>      attributes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Equipment getOwner() {
        return owner;
    }

    public void setOwner(Equipment owner) {
        this.owner = owner;
    }

    public OperEdge getFirstOper() {
        return firstOper;
    }

    public void setFirstOper(OperEdge firstOper) {
        this.firstOper = firstOper;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<RouterAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<RouterAttribute> attributes) {
        this.attributes = attributes;
    }
}
