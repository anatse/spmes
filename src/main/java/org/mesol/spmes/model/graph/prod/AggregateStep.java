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
package org.mesol.spmes.model.graph.prod;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Entity
@Table(name = "PAGGR")
public class AggregateStep extends ProductionObject
{
    @OneToMany
    @JoinColumn(name="PAGGR_ID")
    private List<ProductionObject>          aggregatedSteps;

    public List<ProductionObject> getAggregatedSteps() {
        return aggregatedSteps;
    }

    public void setAggregatedSteps(List<ProductionObject> aggregatedSteps) {
        this.aggregatedSteps = aggregatedSteps;
    }
}
