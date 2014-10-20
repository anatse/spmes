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

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.invoke.MethodHandles;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.apache.log4j.Logger;
import org.mesol.spmes.consts.BasicConstants;
import org.mesol.spmes.model.abs.AbstractEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Entity
@Table(name = "USR", uniqueConstraints=@UniqueConstraint(columnNames={"NAME"}, name = "UK_USERNAME"))
public class User extends AbstractEntity implements Serializable, UserDetails
{
    private static final Logger     logger = Logger.getLogger(MethodHandles.lookup().lookupClass());

    @Id
    @SequenceGenerator(initialValue = 1, name = "usrId", sequenceName = "USR_SEQ", allocationSize = BasicConstants.SEQ_ALLOCATION_SIZE)
    @GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "usrId")
    private Long id;
    
    @Column(name="NAME", nullable = false, length = 32)
    private String          username;
    @Column(nullable = false, length = 180)
    private String          password;
    @Column(length = 180)
    private String          firstName;
    @Column(length = 180)
    private String          lastName;
    private Boolean         enabled = true;
    private Boolean         expired = false;
    private Boolean         locked = false;
    private String          email;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name="USR2GRP",
        joinColumns = @JoinColumn(name="USR_ID", referencedColumnName="ID"),
        inverseJoinColumns = @JoinColumn(name="GRP_ID", referencedColumnName="ID"),
        foreignKey = @ForeignKey(name = "FK_USR_GRP", value = ConstraintMode.CONSTRAINT),
        inverseForeignKey = @ForeignKey(name = "FK_GRP_USR", value = ConstraintMode.CONSTRAINT)
    )
    private Set<UserGroup>      groups;

    public User () {
    }
    
    public User(UserDetails usr) {
        password = encodePwd (usr.getPassword());
        username = usr.getUsername();
        groups = new HashSet<>();

        usr.getAuthorities().forEach(grp -> {
            groups.add(new UserGroup(grp));
        });
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (groups == null)
            return Collections.EMPTY_SET;

        return Collections.unmodifiableSet(groups);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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

    public void setUsername(String name) {
        this.username = name;
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

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setExpired(Boolean expired) {
        this.expired = expired;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public void setGroups(Set<UserGroup> groups) {
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

    public void addGroup(UserGroup users) {
        if (groups == null)
            groups = new HashSet<>();

        groups.add(users);
    }

    public Long getId() {
        return id;
    }

    @JsonIgnore
    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
