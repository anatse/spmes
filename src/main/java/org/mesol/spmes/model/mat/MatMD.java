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

import java.io.Serializable;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.mesol.spmes.consts.BasicConstants;
import org.mesol.spmes.model.abs.AbstractEntity;
import org.mesol.spmes.model.factory.Equipment;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Entity
@Table(name = "MMD")
public class MatMD extends AbstractEntity implements Serializable
{
    @Id
    @SequenceGenerator(initialValue = 1, name = "matMdId", sequenceName = "MATMD_SEQ", allocationSize = BasicConstants.SEQ_ALLOCATION_SIZE)
    @GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "matMdId")
    private Long                    id;

    @Column(name = "NAME", length = 32, nullable = false)
    private String                  name;

    @Column(name = "EXTERNAL_ID", length = 180, nullable = true)
    private String                  extrnalId;

    @ManyToOne
    @JoinColumn(name = "SITE_ID", foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_MATMD_EQ"))
    private Equipment               site;

    @Column(name = "DESCRIPTION", length = 255)
    private String                  description;

    @ManyToOne
    @JoinColumn(name = "STATUS", foreignKey = @ForeignKey(name = "FK_MSTMD_STATUS", value = ConstraintMode.CONSTRAINT))
    private MatMdStatus             status;
    
    @ElementCollection
    @CollectionTable (
        name = "MMDA",
        joinColumns = @JoinColumn(name = "MMD_ID")
    )
    private Set<MatMDAttribute>     attributes;
}
