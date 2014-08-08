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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.mesol.spmes.model.security.Group;
import org.mesol.spmes.model.security.User;
import org.mesol.spmes.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Controller
public class Welcome 
{
    @Autowired
    private UserRepo        userRepo;
    
    private static final Logger     logger = Logger.getLogger(Welcome.class);
    public static String getRevisionNumber () {
        return "$Revision:$";
    }

    @RequestMapping(value = "/")
    @Transactional
    public User test (HttpServletRequest request, HttpServletResponse response) {
        User admin = new User();
        admin.setName("admin");
        admin.setPassword("admin");
        return admin;
//        admin = userRepo.save(admin);
//        return admin;
    }
    
    @RequestMapping(value = "/test")
    @Transactional
    public User test2 (HttpServletRequest request, HttpServletResponse response) {
        User demo = new User();
        demo.setName("demo");
        demo.setPassword("demo");
        
        Group users = new Group();
        users.setName("users");
        demo.addGroup (users);
        
        demo = userRepo.save(demo);
        return demo;
    }
}
