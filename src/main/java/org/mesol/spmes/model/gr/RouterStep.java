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
import javax.script.CompiledScript;
import org.mesol.spmes.model.gr.attr.RsAttribute;
import org.mesol.spmes.model.mat.BOM;
import org.mesol.spmes.model.refs.Duration;
import org.springframework.util.Assert;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Entity
@Table(name = "RS")
public class RouterStep extends Vertex implements IRouterElement
{
    @Column(length = 80, nullable = false, unique = true)
    private String                      name;
    
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CompiledScript getRuleScript() {
        return ruleScript;
    }

    public void setRuleScript(CompiledScript ruleScript) {
        this.ruleScript = ruleScript;
    }

    @Override
    public Router getRouter() {
        return router;
    }

    public void setRouter(Router router) {
        this.router = router;
    }

    public BOM getBom() {
        return bom;
    }

    public void setBom(BOM bom) {
        this.bom = bom;
    }

    public Set<RsAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<RsAttribute> attributes) {
        this.attributes = attributes;
    }

    @Override
    public RouterOperation addEdgeTo (Vertex endPoint, RouterOperation op) throws Exception {
        Assert.isInstanceOf(IRouterElement.class, endPoint, "End point should implement IRouterElement interface");
        Assert.isTrue(this.getRouter().equals(((IRouterElement)endPoint).getRouter()), "Entry point must belong to same router");
        RouterOperation ro = addEdge(endPoint, op);
        ro.setRouter(router);
        return ro;
    }
}
