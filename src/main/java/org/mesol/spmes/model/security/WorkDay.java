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
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Embeddable;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Embeddable
public class WorkDay implements Serializable
{
    @Column(name = "DT")
    private Date        date;
    @ManyToOne
    @JoinColumn(name = "USH_ID", foreignKey = @ForeignKey(name = "FK_WD_USH", value = ConstraintMode.CONSTRAINT))
    private UserShift   shift;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public UserShift getShift() {
        return shift;
    }

    public void setShift(UserShift shift) {
        this.shift = shift;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.date);
        hash = 79 * hash + Objects.hashCode(this.shift);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WorkDay other = (WorkDay) obj;
        if (!Objects.equals(this.date, other.date)) {
            return false;
        }

        return Objects.equals(this.shift, other.shift);
    }
}
