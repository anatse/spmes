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
package org.mesol.spmes.model.graph.states;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.mesol.spmes.model.abs.AbstractEntity;

/**
 * State holder for production order entity
 * @version 1.0.0
 * @author ASementsov
 */
@Entity
@Table(name = "POR_STATE")
public class ProdOrderState extends AbstractEntity implements Serializable
{
    @Id
    @Column(name = "PO_STATE", length = 10, nullable = false, unique = true)
    private String              operState;

    @Column(name = "DESCR", length = 255)
    private String              description;
    
    @OneToMany(mappedBy = "prevState")
    private Set<ProdOrderState>  nextStates;

    @ManyToOne
    @JoinColumn(name="PREV_STATE", foreignKey = @ForeignKey(name = "FK_PORS_PREV", value = ConstraintMode.CONSTRAINT))
    private ProdOrderState       prevState;

    public String getOperState() {
        return operState;
    }

    public void setOperState(String operState) {
        this.operState = operState;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonIgnore
    public Set<ProdOrderState> getNextStates() {
        return nextStates;
    }

    public void setNextStates(Set<ProdOrderState> nextStates) {
        this.nextStates = nextStates;
    }

    @JsonIgnore
    public ProdOrderState getPrevState() {
        return prevState;
    }

    public void setPrevState(ProdOrderState prevState) {
        this.prevState = prevState;
    }
}
