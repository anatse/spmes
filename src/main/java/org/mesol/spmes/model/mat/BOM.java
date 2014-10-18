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
package org.mesol.spmes.model.mat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.mesol.spmes.consts.BasicConstants;
import org.mesol.spmes.model.abs.AbstractEntity;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Entity
@Table(name = "BOM")
public class BOM extends AbstractEntity implements Serializable
{
    @Id
    @SequenceGenerator(initialValue = 1, name = "bomId", sequenceName = "BOM_SEQ", allocationSize = BasicConstants.SEQ_ALLOCATION_SIZE)
    @GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "bomId")
    private Long                id;
    @Column(name = "NAME", length = 32, nullable = false)
    private String              name;
    @Column(name = "DESCRIPTION", length = 255)
    private String              description;
    @Column(name = "EXTERNAL_BOM", length = 180)
    private String              externalBom;
    @OneToMany(mappedBy = "bom")
    private Set<BOMInputCmp>    inputComponents;
    @OneToMany(mappedBy = "bom")
    private Set<BOMOutputCmp>   outputComponents;

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

    public String getExternalBom() {
        return externalBom;
    }

    public void setExternalBom(String externalBom) {
        this.externalBom = externalBom;
    }

    @JsonIgnore
    public Set<BOMInputCmp> getInputComponents() {
        return inputComponents;
    }

    public void setInputComponents(Set<BOMInputCmp> inputComponents) {
        this.inputComponents = inputComponents;
    }

    @JsonIgnore
    public Set<BOMOutputCmp> getOutputComponents() {
        return outputComponents;
    }

    public void setOutputComponents(Set<BOMOutputCmp> outputComponents) {
        this.outputComponents = outputComponents;
    }
}
