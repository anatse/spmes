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
package org.mesol.spmes.model.abs;

import java.sql.Timestamp;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@MappedSuperclass
public abstract class AbstractEntity 
{
//    @Id 
//    @SequenceGenerator(initialValue = 1, name = "idgen", sequenceName = "SEQ_TAB_ID")
//    @GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "idgen")
//    private Long id;

    @CreatedDate
    private Timestamp       createdDate;

    @LastModifiedDate
    private Timestamp       modifiedDate;

    @CreatedBy
    private String          createdBy;

    @LastModifiedBy
    private String          modifiedBy;
}
