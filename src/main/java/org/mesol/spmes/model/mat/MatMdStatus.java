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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.apache.log4j.Logger;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Entity
@Table(name = "MMDS")
public class MatMdStatus implements Serializable 
{
    @Id
    @Column(length =  10)
    private String                  name;

    @Column(length = 255)
    private String                  description;

    @Column(length = 255)
    private String                  allowedNext;

    private transient Set<String>   nextStatuses;

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

    public String getAllowedNext() {
        if (nextStatuses != null) {
            allowedNext = "";
            nextStatuses.forEach(s->{
                allowedNext += s + ",";
            });
        }

        return allowedNext;
    }

    public void setAllowedNext(String allowedNext) {
        this.allowedNext = allowedNext;
        String[] ns = allowedNext.split(",");
        nextStatuses = new HashSet<>(Arrays.asList(ns));
    }

    public boolean isStatusAllowed (String status) {
        return nextStatuses.contains(status);
    }
}
