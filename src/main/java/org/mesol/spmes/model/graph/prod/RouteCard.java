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

import java.io.Serializable;
import javax.persistence.ConstraintMode;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.mesol.spmes.model.graph.Vertex;

/**
 * Route card object
 * @version 1.0.0
 * @author ASementsov
 */
public class RouteCard extends Vertex implements Serializable
{
    @ManyToOne
    @JoinColumn(name = "PARENT_ID", foreignKey = @ForeignKey(name = "FK_RC_PARENT", value = ConstraintMode.CONSTRAINT))
    private RouteCard           parent;
    
    @ManyToOne
    @JoinColumn(name = "PO_ID", foreignKey = @ForeignKey(name = "FK_RC_PO", value = ConstraintMode.CONSTRAINT))
    private ProductionOrder     order;
}
