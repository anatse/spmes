/*
 * Copyright 2015 Mes Solutions.
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
import java.net.URL;
import org.apache.log4j.Logger;
import org.mesol.spmes.service.dsl.DslExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Controller
@RequestMapping(value = "/dsl")
public class DSLController 
{
    private static final Logger         logger = Logger.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private DslExecutor                 dslExecutor;

    // http://localhost:8080/SpringMes/dsl/run?path=/org/mesol/spmes/service/dsl/CreateUsers.groovy
    @Transactional
    @RequestMapping(value="run", produces = {"appliation/json"})
    public Object executeDslScript (@RequestParam(value = "path")String path) throws Exception {
        System.out.println ("Path: " + path);
        URL scriptUrl = getClass().getResource(path);
        if (scriptUrl == null) {
            return new Exception("Resource not found: " + path);
        }

        String script = scriptUrl.toString();
        return dslExecutor.execute(script);
    }
}
