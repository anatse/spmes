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
import java.util.Collection;
import java.util.HashSet;
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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.script.CompiledScript;
import org.mesol.spmes.consts.BasicConstants;
import org.mesol.spmes.model.graph.attr.RsAttribute;
import org.mesol.spmes.model.mat.BOM;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Entity
@Table(name = "RS")
public class RouterStep extends Vertex
{
    @Id
    @SequenceGenerator(initialValue = 1, name = "rsId", sequenceName = "RS_SEQ", allocationSize = BasicConstants.SEQ_ALLOCATION_SIZE)
    @GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "rsId")
    private Long                        id;

    @OneToMany(mappedBy = "from")
    private Collection<OperEdge>        out;

    @OneToMany(mappedBy = "to")
    private Collection<OperEdge>        in;

    @Column(nullable = false, length = 32)
    private String                      name;

    /**
     * Groovy script to determine next operation (only for rule based operations)
     */
    @Column(length = 255)
    private String                      rule;
    /**
     * Compiled script (rule)
     */
    private transient CompiledScript    ruleScript;

    @ManyToOne
    @JoinColumn(name = "ROUTER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RS_ROUTER", value = ConstraintMode.CONSTRAINT))
    private Router                      router;

    @ManyToOne
    @JoinColumn(name = "BOM_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_RS_BOM", value = ConstraintMode.CONSTRAINT))
    private BOM                         bom;
    
    @ElementCollection
    @CollectionTable (
        name = "RSA",
        joinColumns = @JoinColumn(name = "RS_ID")
    )
    private Set<RsAttribute>            attributes;

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonIgnore
    public Collection<OperEdge> getOut() {
        return out;
    }

    public void setOut(Collection<OperEdge> out) {
        this.out = out;
    }

    @JsonIgnore
    public Collection<OperEdge> getIn() {
        return in;
    }

    public void setIn(Collection<OperEdge> in) {
        this.in = in;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
        ruleScript = null;
    }
    
    public void addIn (OperEdge edge) {
        if (in == null)
            in = new HashSet<>();

        in.add(edge);
    }
    
    public void addOut (OperEdge edge) {
        if (out == null)
            out = new HashSet<>();

        out.add(edge);
    }

    public Router getRouter() {
        return router;
    }

    public void setRouter(Router router) {
        this.router = router;
    }

    public Set<RsAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<RsAttribute> attributes) {
        this.attributes = attributes;
    }

    public CompiledScript getRuleScript() {
        return ruleScript;
    }

    public void setRuleScript(CompiledScript ruleScript) {
        this.ruleScript = ruleScript;
    }

    public BOM getBom() {
        return bom;
    }

    public void setBom(BOM bom) {
        this.bom = bom;
    }
}
