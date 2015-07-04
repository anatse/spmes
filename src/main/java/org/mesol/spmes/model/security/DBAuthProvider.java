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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsOperations;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 *
 * @version 1.0.0
 * @author ASementsov
 */
public class DBAuthProvider extends AbstractUserDetailsAuthenticationProvider 
{
    private static final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass());

    private UserDetailsService userDetailsService;

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    protected UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Authentication failed: no credentials provided");
            }

            throw new BadCredentialsException(messages.getMessage("badCredentials", "Bad credentials"));
        }

        String presentedPassword = authentication.getCredentials().toString();
        if (!(userDetails instanceof User) || !((User) userDetails).checkPassword(presentedPassword)) {
            throw new BadCredentialsException(messages.getMessage("badCredentials", "Bad credentials"));
        }
        
        if (logger.isInfoEnabled())
            logger.info("User loged sucessfully");
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        UserDetails loadedUser;

        try {
            loadedUser = this.getUserDetailsService().loadUserByUsername(username);
        } 
        catch (UsernameNotFoundException notFound) {
            throw notFound;
        }

        if (loadedUser == null) {
            throw new AuthenticationServiceException("UserDetailsService returned null, which is an interface contract violation");
        }

        return loadedUser;
    }

}
