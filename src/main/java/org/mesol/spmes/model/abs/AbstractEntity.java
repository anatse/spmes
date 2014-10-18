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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractEntity 
{
    @CreatedDate
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime        createdDate;

    @LastModifiedDate
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime        modifiedDate;

    @CreatedBy
    private String          createdBy;

    @LastModifiedBy
    private String          modifiedBy;

    @JsonProperty
    public Timestamp getCreatedDate() {
        // int offset = DateTimeZone.forID("anytimezone").getOffset(new DateTime());
        return new Timestamp(createdDate.getMillis());
    }

    @JsonIgnore
    public void setCreatedDate(DateTime createdDate) {
        this.createdDate = createdDate;
    }

    @JsonProperty
    public Timestamp getModifiedDate() {
        return new Timestamp(modifiedDate.getMillis());
    }

    @JsonIgnore
    public void setModifiedDate(DateTime modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
}
