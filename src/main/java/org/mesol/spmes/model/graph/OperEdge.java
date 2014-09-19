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
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQuery;
import org.apache.log4j.Logger;

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
@DiscriminatorValue("Oper")
@NamedNativeQuery (name = "OperEdge.operList", 
    resultClass = OperEdge.class, 
    query = "select * " + 
            "from operedge op " +
            "start with id = (select oper_id from router where id = ?1) " +
            "connect by prior to_id = from_id " +
            "order by level")
public class OperEdge extends Edge<RouterStep> implements Serializable
{
    private static final Logger     logger = Logger.getLogger(OperEdge.class);
    
    @ManyToOne
    @JoinColumn(name = "FROM_ID", foreignKey = @ForeignKey(name = "FK_OPER_FROM", value = ConstraintMode.CONSTRAINT))
    private RouterStep              from;
    @ManyToOne
    @JoinColumn(name = "TO_ID", foreignKey = @ForeignKey(name = "FK_OPER_TO", value = ConstraintMode.CONSTRAINT))
    private RouterStep              to;
    @Column(length = 255)
    private String                  rule;

    @Override
    public RouterStep getFrom() {
        return from;
    }

    @Override
    public RouterStep getTo() {
        return to;
    }

    public void setFrom(RouterStep from) {
        this.from = from;
    }

    public void setTo(RouterStep to) {
        this.to = to;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }
}
