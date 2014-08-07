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
import java.util.Collection;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.apache.log4j.Logger;
import org.mesol.spmes.model.abs.AbstractEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Entity
@Table(name = "USR")
public class User extends AbstractEntity implements Serializable, UserDetails
{
    private static final Logger     logger = Logger.getLogger(User.class);
    public static String getRevisionNumber () {
        return "$Revision:$";
    }
    
    private String          name;
    private String          password;
    private String          firstName;
    private String          lastName;
    private boolean         enabled = true;
    private boolean         expired;
    private boolean         locked;

    @ManyToMany
    @JoinTable(
        name="USR2GRP",
        joinColumns = @JoinColumn(name="USR_ID", referencedColumnName="ID"),
        inverseJoinColumns = @JoinColumn(name="GRP_ID", referencedColumnName="ID"),
        foreignKey = @ForeignKey(name = "FK_USR_GRP"),
        inverseForeignKey = @ForeignKey(name = "FK_GRP_USR")
    )
    private Set<Group>      groups;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return groups;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !expired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isAccountNonLocked();
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
