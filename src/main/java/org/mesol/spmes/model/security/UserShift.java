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
import java.util.Calendar;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.mesol.spmes.consts.BasicConstants;
import org.mesol.spmes.model.abs.BaseSiteObject;

/**
 * Class contains user shift definition. All time data defined in minutes
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Entity
@Table (name = "USH")
@NamedQueries(
    @NamedQuery (name = "UserShift.currentShift", query = "select u from UserShift u where :curTime between u.startTime and u.endTime")
)
public class UserShift extends BaseSiteObject implements Serializable
{

    public static int convertTime(int hour, int minutes, int seconds) {
        return hour * 3600 + minutes * 60 + seconds;
    }

    public static int convertTime(Date time) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        return convertTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
    }
    @Id
    @SequenceGenerator(initialValue = 1, name = "ushId", sequenceName = "USH_SEQ", allocationSize = BasicConstants.SEQ_ALLOCATION_SIZE)
    @GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "ushId")
    private Long            id;

    @Column(nullable = false, length = 32, unique = true)
    private String          name;

    @Column(name = "START_TIME")
    private Integer         startTime;

    @Column(name = "END_TIME")
    private Integer         endTime;

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

    public Integer getStartTime() {
        return startTime;
    }

    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

    public Integer getEndTime() {
        return endTime;
    }

    public void setEndTime(Integer endTime) {
        this.endTime = endTime;
    }

}
