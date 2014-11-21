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
package org.mesol.spmes.model.gr;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import org.mesol.spmes.consts.BasicConstants;
import org.mesol.spmes.model.refs.Duration;
import org.springframework.util.Assert;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Vertex implements Serializable 
{
    @Id
    @SequenceGenerator(initialValue = 1, name = "gvertexId", sequenceName = "GМЕЧ_SEQ", allocationSize = BasicConstants.SEQ_ALLOCATION_SIZE)
    @GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "gvertexId")
    private Long                    id;

    @OneToMany(mappedBy = "to")
    private Set<Edge>               inEdges;

    @OneToMany(mappedBy = "from")
    private Set<Edge>               outEdges;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.id);
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
        final Vertex other = (Vertex) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    public Set<Edge> getInEdges() {
        if (inEdges == null)
            return Collections.EMPTY_SET;

        return Collections.unmodifiableSet(inEdges);
    }

    public void setInEdges(Set<Edge> inEdges) {
        this.inEdges = inEdges;
    }

    public Set<Edge> getOutEdges() {
        if (outEdges == null)
            return Collections.EMPTY_SET;

        return Collections.unmodifiableSet(outEdges);
    }

    public void setOutEdges(Set<Edge> outEdges) {
        this.outEdges = outEdges;
    }

    public void addOutEdge (Edge edge) {
        if (outEdges == null) {
            outEdges = new HashSet<>();
        }

        edge.setFrom(this);
        outEdges.add(edge);
    }

    public void addInEdge (Edge edge) {
        if (inEdges == null) {
            inEdges = new HashSet<>();
        }

        edge.setTo(this);
        inEdges.add(edge);
    }

    /**
     * Function creates new edge from this to endPoint. 
     * If such edge already exists then returns it
     * @param <T>
     * @param endPoint edge to create to
     * @param edgeClass edge class 
     * @param duration duration for new created edge
     * @return edge
     */
    protected <T extends Edge> T createEdgeTo (Vertex endPoint, Class<T> edgeClass, Duration duration) {
        Assert.notNull(endPoint, "Endpoint cannot be null");
        Assert.isTrue(!this.equals(endPoint), "Cannot add edge to self object");
        Assert.isTrue(endPoint.getOutEdges().isEmpty(), "Endpoint must not have any out edges");

        // if edge already exists
        if (getOutEdges() != null && !getOutEdges().isEmpty()) {
            Optional<Edge> oedge = getOutEdges().stream().filter(e -> e.getTo().equals(endPoint)).findFirst();
            if (oedge.isPresent())
                return (T)oedge.get();
        }

        try {
            T newEdge = edgeClass.newInstance();
            addOutEdge(newEdge);
            endPoint.addInEdge(newEdge);
            newEdge.setDuration(duration);

            // Compute start time for this operation. Find max duration for several edges with no redards to connection type
            newEdge.setStartTime (RoutingUtils.computeStart(getInEdges()));
            return newEdge;
        }
        catch (InstantiationException | IllegalAccessException e) {
            throw (new RuntimeException(e));
        }
    }
    
    public <T extends Edge> T undockEdge (T edge) {
        Assert.isTrue(edge.getFrom().equals(this) || edge.getTo().equals(this), "Edge is not belong to vertex");
        if (edge.getFrom().equals(this)) {
            outEdges.remove(edge);
            edge.setFrom(null);
            edge.setStartTime(0L);
        }
        else {
            inEdges.remove(edge);
            edge.setTo(null);
        }

        return edge;
    }
}
