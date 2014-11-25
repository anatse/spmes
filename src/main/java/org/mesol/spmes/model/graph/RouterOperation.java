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

import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.mesol.spmes.model.factory.EquipmentClass;
import org.mesol.spmes.model.graph.attr.OperAttribute;

/**
 * Class describes operation edge
 * 
 * @SqlResultSetMapping(
 *    name = "OperEdgeResult",
 *    classes = {
 *        @ConstructorResult(
 *            targetClass = OperEdge.class,
 *            columns = {
 *                // level, id, name, from_id, to_id, weight
 *                @ColumnResult(name = "level"),
 *                @ColumnResult(name = "id"),
 *                @ColumnResult(name = "name")
 *            }
 *        )
 *    }
 * )
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Entity
@Table(name = "RO")
public class RouterOperation extends Edge
{
    @Column(length = 32, nullable = false, unique = true)
    private String                  name;
    
    @ManyToOne
    @JoinColumn(name = "ROUTER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_OPER_ROUTER", value = ConstraintMode.CONSTRAINT))
    private Router                  router;

    @ElementCollection
    @CollectionTable (
        name = "OPEDGA",
        joinColumns = @JoinColumn(name = "OPER_ID")
    )
    private Set<OperAttribute>      attributes;

    @ManyToOne
    @JoinColumn(name = "EQC_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_OPEREDGE_EQC", value = ConstraintMode.CONSTRAINT))
    private EquipmentClass          equipmentClass;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<OperAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<OperAttribute> attributes) {
        this.attributes = attributes;
    }

    public EquipmentClass getEquipmentClass() {
        return equipmentClass;
    }

    public void setEquipmentClass(EquipmentClass equipmentClass) {
        this.equipmentClass = equipmentClass;
    }

    public Router getRouter() {
        return router;
    }

    public void setRouter(Router router) {
        this.router = router;
    }
}
