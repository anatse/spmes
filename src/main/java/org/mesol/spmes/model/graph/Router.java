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
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.apache.log4j.Logger;
import org.mesol.spmes.model.abs.AbstractEntity;
import org.mesol.spmes.model.factory.Equipment;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Entity
@Table(name = "ROUTER")
public class Router extends AbstractEntity implements Serializable
{
    private static final Logger     logger = Logger.getLogger(Router.class);

    @Id
    @SequenceGenerator(initialValue = 1, name = "routerId", sequenceName = "ROUTER_SEQ")
    @GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "routerId")
    private Long                    id;

    @ManyToOne
    @JoinColumn(name = "EQ_ID", 
        foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT, name = "FK_ROUTER_EQO"))
    private Equipment               owner;
    
    
}
