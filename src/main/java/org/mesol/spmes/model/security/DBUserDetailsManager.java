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

import java.lang.invoke.MethodHandles;
import org.apache.log4j.Logger;
import org.mesol.spmes.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsOperations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
public class DBUserDetailsManager implements UserDetailsManager
{
    private static final Logger     logger = Logger.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private UserService             userService;
    
    @Autowired
    private JmsOperations           jmsOpers;
    
    @Override
    public void createUser(UserDetails user) {
        User usr = new User(user);
        userService.save(usr);
    }

    @Override
    public void updateUser(UserDetails user) {
        User usr = new User(user);
        userService.save(usr);
    }

    @Override
    public void deleteUser(String username) {
        userService.deleteByName(username);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean userExists(String username) {
        User usr = userService.findByName(username);
        return usr != null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails ud = userService.findByName(username);
        if (ud == null)
            throw (new UsernameNotFoundException("user not found"));

//        try {
//            ActiveMQTextMessage msg = new ActiveMQTextMessage();
//            msg.setText("Hello world");
//            jmsOpers.convertAndSend(msg);
//        } 
//        catch (MessageNotWriteableException ex) {
//            logger.error(username, ex);
//        }

        return ud;
    }
}
