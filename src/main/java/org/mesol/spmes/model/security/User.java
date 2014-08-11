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
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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
    private boolean         expired = false;
    private boolean         locked = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name="USR2GRP",
        joinColumns = @JoinColumn(name="USR_ID", referencedColumnName="ID"),
        inverseJoinColumns = @JoinColumn(name="GRP_ID", referencedColumnName="ID"),
        foreignKey = @ForeignKey(name = "FK_USR_GRP"),
        inverseForeignKey = @ForeignKey(name = "FK_GRP_USR")
    )
    private Set<Group>      groups;

    public User () {
    }
    
    public User(UserDetails usr) {
        password = encodePwd (usr.getPassword());
        name = usr.getUsername();
        groups = new HashSet<>();

        usr.getAuthorities().forEach(grp -> {
            groups.add(new Group(grp));
        });
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (groups == null)
            return groups;

        return Collections.unmodifiableSet(groups);
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

    public void setName(String name) {
        this.name = name;
    }

    private String encodePwd (String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(password.getBytes("utf-8"));
            return Base64.getEncoder().encodeToString(digest);
        } 
        catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            logger.error(ex, ex);
            return "";
        }
    }
    
    public void setPassword(String password) {
        this.password = encodePwd(password);
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    boolean checkPassword(String presentedPassword) {
        String pw = encodePwd(presentedPassword);
        return pw.equals(password);
    }

    public void addGroup(Group users) {
        if (groups == null)
            groups = new HashSet<>();

        groups.add(users);
    }
}
