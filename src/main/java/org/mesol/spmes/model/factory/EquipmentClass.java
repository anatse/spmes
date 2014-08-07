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
package org.mesol.spmes.model.factory;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import org.apache.log4j.Logger;
import org.mesol.spmes.model.abs.AbstractEntity;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Entity
@Table(name = "EQC")
public class EquipmentClass extends AbstractEntity implements Serializable
{
    private static final Logger     logger = Logger.getLogger(EquipmentClass.class);
    public static String getRevisionNumber () {
        return "$Revision:$";
    }
    
    private String          name;
    private String          description;
    
    @ManyToMany
    @JoinTable(
        name="EQC2EQ",
        joinColumns = @JoinColumn(name="EQC_ID", referencedColumnName="ID"),
        inverseJoinColumns = @JoinColumn(name="EQ_ID", referencedColumnName="ID"),
        foreignKey = @ForeignKey(name = "FK_EQC_EQ"),
        inverseForeignKey = @ForeignKey(name = "FK_EQ_EQC")
    )
    private Set<Equipment>   equipments;
}
