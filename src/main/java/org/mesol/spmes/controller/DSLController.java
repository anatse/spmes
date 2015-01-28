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
import org.apache.log4j.Logger;
import org.mesol.spmes.service.dsl.DslExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @Transactional
    @RequestMapping(value="{path}")
    public Object executeDslScript (@PathVariable(value = "path")String path) throws Exception {
        String script = getClass().getResource(path).toString();
        return dslExecutor.execute(script);
    }
}
