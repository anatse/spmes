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
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.mesol.spmes.service.Import;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Controller
@RequestMapping(value = "/import")
public class InitialImport 
{
    private static final Logger     logger = Logger.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private Import                  imp;

    @RequestMapping(value = "init", produces = "application/json")
    public Map<String, String> initialImport () {
        Map<String, String> ret = new HashMap<>();
        imp.parse(getClass().getClassLoader().getResourceAsStream("imp/router.xml"));
        ret.put("status", "OK");
        return ret;
    }
}
