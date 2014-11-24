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
package org.mesol.spmes.utils;

import java.lang.invoke.MethodHandles;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.apache.log4j.Logger;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
public class GroovyExecutor
{
    private static final Logger     logger = Logger.getLogger(MethodHandles.lookup().lookupClass());

    private final ScriptEngine      engine;
    private final Bindings          bindings;
 
    public GroovyExecutor() {
        engine = new ScriptEngineManager().getEngineByName("groovy");
        bindings = engine.createBindings();
    }
    
    public GroovyExecutor bind (String name, Object value) {
        bindings.put(name, value);
        return this;
    }
    
    public GroovyExecutor clearBindings () {
        bindings.clear();
        return this;
    }
    
    public Bindings getBindings () {
        return bindings;
    }

    public <T> T execute (String script) {
        T res = null;

        try {
            if (engine instanceof Compilable) {
                Compilable compilingEngine = (Compilable)engine;
                CompiledScript compiledScript = compilingEngine.compile(script);
                res = (T)compiledScript.eval(bindings);
            }

//            Invocable inv = (Invocable) engine;
//            Object[] params = { new Integer(5) };
//            Object result = inv.invokeFunction("factorial", params);
//            System.out.println(result);
        } 
        catch (ScriptException ex) {
            logger.error(ex, ex);
        }

        return res;
    }
}
