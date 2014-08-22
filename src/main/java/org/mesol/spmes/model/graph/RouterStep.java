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

import java.util.Collection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.apache.log4j.Logger;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Entity
@Table(name = "RS")
public class RouterStep extends Vertex
{
    private static final Logger     logger = Logger.getLogger(RouterStep.class);
    
    @Id
    @SequenceGenerator(initialValue = 1, name = "rsId", sequenceName = "RS_SEQ")
    @GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "rsId")
    private Long                    id;
    
    @OneToMany(mappedBy = "from")
    private Collection<OperEdge>    out;
    
    @OneToMany(mappedBy = "to")
    private Collection<OperEdge>    in;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Collection<OperEdge> getOut() {
        return out;
    }

    public void setOut(Collection<OperEdge> out) {
        this.out = out;
    }

    public Collection<OperEdge> getIn() {
        return in;
    }

    public void setIn(Collection<OperEdge> in) {
        this.in = in;
    }
}
