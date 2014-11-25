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
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import org.mesol.spmes.consts.BasicConstants;
import org.mesol.spmes.model.refs.Duration;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Edge implements Serializable
{
    @Id
    @SequenceGenerator(initialValue = 1, name = "gedgeId", sequenceName = "GEDGE_SEQ", allocationSize = BasicConstants.SEQ_ALLOCATION_SIZE)
    @GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "gedgeId")
    private Long                    id;
    
    @ManyToOne
    private Vertex                  from;
    
    @ManyToOne
    private Vertex                  to;

    @Embedded
    private Duration                duration;

    @Column(name = "START_TIME", nullable = false)
    private Long                    startTime;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "OPER_TYPE")
    private PerformType             performType;
    
    @Column(length = 255, name = "RULE_VAL")
    private String                  ruleValue;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Edge other = (Edge) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    public Vertex getFrom() {
        return from;
    }

    public void setFrom(Vertex from) {
        this.from = from;
    }

    public Vertex getTo() {
        return to;
    }

    public void setTo(Vertex to) {
        this.to = to;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public PerformType getPerformType() {
        return performType;
    }

    public void setPerformType(PerformType performType) {
        this.performType = performType;
    }

    public String getRuleValue() {
        return ruleValue;
    }

    public void setRuleValue(String ruleValue) {
        this.ruleValue = ruleValue;
    }
}
