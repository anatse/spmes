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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.mesol.spmes.model.factory.Site;
import org.mesol.spmes.model.gr.attr.RouterAttribute;
import org.mesol.spmes.model.refs.Duration;
import org.springframework.util.Assert;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Entity
@Table(name = "ROUTER")
public class Router extends Vertex implements IRouterElement
{
    @Column(length = 80, nullable = false, unique = true)
    private String                      name;

    @ManyToOne
    @JoinColumn(name = "SITE_ID", foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_ROUTER_SITE"))
    private Site                        site;
    
    @ElementCollection
    @CollectionTable (
        name = "RA",
        joinColumns = @JoinColumn(name = "ROUTER_ID")
    )
    private Set<RouterAttribute>        attributes;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private ObjectState                 status = ObjectState.DEVELOPMENT;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public Set<RouterAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<RouterAttribute> attributes) {
        this.attributes = attributes;
    }

    public ObjectState getStatus() {
        return status;
    }

    public void setStatus(ObjectState status) {
        this.status = status;
    }

    @Override
    public Router getRouter() {
        return this;
    }

    @Override
    public RouterOperation createEdgeTo (Vertex endPoint, Duration duration) {
        Assert.isInstanceOf(IRouterElement.class, endPoint, "End point should implement IRouterElement interface");
        Assert.isTrue(this.getRouter().equals(((IRouterElement)endPoint).getRouter()), "Entry point must belong to same router");
        RouterOperation ro = createEdgeTo(endPoint, RouterOperation.class, duration);
        ro.setRouter(this);
        return ro;
    }
}
