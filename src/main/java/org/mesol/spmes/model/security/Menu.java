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
package org.mesol.spmes.model.security;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.apache.log4j.Logger;
import org.mesol.spmes.model.abs.AbstractEntity;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Entity
@Table(name = "MENU")
public class Menu extends AbstractEntity implements Serializable
{
    private static final Logger     logger = Logger.getLogger(Menu.class);

    @Id
    @SequenceGenerator(initialValue = 1, name = "menuId", sequenceName = "MENU_SEQ")
    @GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "menuId")
    private Long id;

    @Column(length = 80, nullable = false)
    private String                  name;
    @Column(length = 255)
    private String                  description;
    @Column(length = 1024, nullable = false)
    private String                  url;
        
    @ManyToOne
    @JoinColumn(name="PARENT_ID")
    private Menu                    parentMenu;

    @ManyToMany
    @JoinTable(
        name="MENU2GRP",
        joinColumns = @JoinColumn(name="MENU_ID", referencedColumnName="ID"),
        inverseJoinColumns = @JoinColumn(name="GRP_ID", referencedColumnName="ID"),
        foreignKey = @ForeignKey(name = "FK_MENU_GRP"),
        inverseForeignKey = @ForeignKey(name = "FK_GRP_MENU")
    )
    private Set<UserGroup>   groups;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Menu getParentMenu() {
        return parentMenu;
    }

    public void setParentMenu(Menu parentMenu) {
        this.parentMenu = parentMenu;
    }

    public Set<UserGroup> getGroups() {
        return groups;
    }

    public void setGroups(Set<UserGroup> groups) {
        this.groups = groups;
    }
}