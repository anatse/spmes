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
import java.util.Collections;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.apache.log4j.Logger;
import org.mesol.spmes.model.abs.AbstractEntity;
import org.springframework.security.core.GrantedAuthority;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Entity
@Table(name = "GRP", uniqueConstraints=@UniqueConstraint(columnNames={"NAME"}, name = "UK_GRP_NAME"))
public class UserGroup extends AbstractEntity implements Serializable, GrantedAuthority
{
    private static final Logger     logger = Logger.getLogger(UserGroup.class);

    @Id
    @SequenceGenerator(initialValue = 1, name = "grpId", sequenceName = "GRP_SEQ")
    @GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "grpId")
    private Long id;

    @Column(nullable = false, length = 32)
    private String          name;
    @ManyToMany(mappedBy = "groups")
    private Set<User>       users;

    public UserGroup(GrantedAuthority grp) {
        name = grp.getAuthority();
    }

    public UserGroup() {
    }

    @Override
    public String getAuthority() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getUsers() {
        if (users == null)
            return Collections.EMPTY_SET;

        return Collections.unmodifiableSet(users);
    }
}
