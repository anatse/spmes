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
package org.mesol.spmes.controller;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.mesol.spmes.consts.BasicConstants;
import org.mesol.spmes.model.security.Menu;
import org.mesol.spmes.model.security.User;
import org.mesol.spmes.model.security.UserGroup;
import org.mesol.spmes.service.UserService;
import org.mesol.spmes.utils.EntityCopier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Controller
public class AuthController 
{
    private static final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private UserService             userService;

    @Autowired
    private MessageSource           messageSource;
    
    @RequestMapping(value = "/")
    public ModelAndView home (HttpServletRequest request, HttpServletResponse response) {       
        return new ModelAndView("home");
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(
        Locale locale, @RequestParam(value = "error", required = false) String error, @RequestParam(value = "logout", required = false) String logout) {
        
        ModelAndView model = new ModelAndView();
        if (error != null) {
            String message = messageSource.getMessage("invalidCredentials.message", new Object[0], locale);
            model.addObject("error", message);
        }

        if (logout != null) {
            String message = messageSource.getMessage("loggedOut.message", new Object[0], locale);
            model.addObject("msg", message);
        }

        model.setViewName("login");
        return model;
    }

    /**
     * Function return menu for current user
     * @param parentId
     * @return 
     */
    @Secured({
        BasicConstants.ADMIN_ROLE,
        BasicConstants.CHIEF_ROLE,
        BasicConstants.LABORER_ROLE,
        BasicConstants.TECHNOLOG_ROLE
    })
    @RequestMapping(value = "/service/menu", produces = {"application/json"})
    @Transactional
    public List<Menu> getUserMenu(
        @RequestParam (value = "parentId", required = false) Long parentId) {
        if (logger.isDebugEnabled())
            logger.debug("getUserMenu called");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userService.getUserMenu(auth.getName(), parentId == -1 ? null : parentId);
    }

    /**
     * Function implements LIST and GET operation of the REST interface for users
     * @return list of users, or information about concrete  user
     */
    @Secured({
        BasicConstants.ADMIN_ROLE, 
        BasicConstants.CHIEF_ROLE
    })
    @RequestMapping(value = "/service/user/list", method = {RequestMethod.POST}, produces = {"application/json"})
    @Transactional
    public List<User> findAllUsers () {
        if (logger.isDebugEnabled())
            logger.debug("findAllUsers called");

        return userService.findAll();
    }

    @Secured({
        BasicConstants.ADMIN_ROLE, 
        BasicConstants.CHIEF_ROLE
    })
    @RequestMapping(
        value = "/service/user/add", 
        method = {RequestMethod.POST}, 
        produces = {"application/json"},
        consumes = {"application/json"}
    )
    @Transactional
    public User createUser (
        @RequestBody User newUser
    ) {
        if (logger.isDebugEnabled())
            logger.debug("findAllUsers called");
        
        userService.save(newUser);
        return newUser;
    }

    /**
     * Function update user information
     * @param changedUser - changed user data
     * @return updated user
     */
    @Secured({
        BasicConstants.ADMIN_ROLE, 
        BasicConstants.CHIEF_ROLE
    })
    @RequestMapping(value = "/service/user/update", 
        method = {RequestMethod.POST},
        produces = {"application/json"},
        consumes = {"application/json"}
    )
    @Transactional
    public User saveUser (
        @RequestBody User changedUser
    ) {
        User user = userService.findByName(changedUser.getUsername());
        if (user != null) {
            user = EntityCopier.copy(changedUser, user);
            userService.save(user);
        }
        return user;
    }
    
    @Secured({
        BasicConstants.ADMIN_ROLE, 
        BasicConstants.CHIEF_ROLE
    })
    @RequestMapping(value = "/service/user/grp/list", 
        method = {RequestMethod.POST},
        produces = {"application/json"}
    )
    @Transactional
    public Set<UserGroup> getUserGroups (@RequestParam (value = "username") String userName) {
        User usr = userService.findByName(userName);
        return usr.getGroups();
    }
    
    @Secured({
        BasicConstants.ADMIN_ROLE, 
        BasicConstants.CHIEF_ROLE
    })
    @RequestMapping(value = "/service/user/grp/availist", 
        method = {RequestMethod.POST},
        produces = {"application/json"}
    )
    @Transactional
    public List<UserGroup> findAvailableGroups (@RequestParam (value = "username") String userName) {
        return userService.findAvailableGroups(userName);
    }
    
    @Secured({
        BasicConstants.ADMIN_ROLE, 
        BasicConstants.CHIEF_ROLE
    })
    @RequestMapping(value = "/service/user/grp/add", 
        method = {RequestMethod.POST},
        produces = {"application/json"}
    )
    @Transactional
    public List<UserGroup> addUserGroups (
            @RequestParam (value = "username") String userName,
            @RequestParam (value = "groups")List<String> groups
    ) {
        return userService.addGroups(userName, groups);
    }
}
