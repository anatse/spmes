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
package org.mesol.spmes.service.dsl;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import java.io.File;
import java.lang.invoke.MethodHandles;
import java.net.URL;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.EntityType;
import org.apache.log4j.Logger;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Class used to execute DSL script
 * @version 1.0.0
 * @author ASementsov
 */
@Component
public class DslExecutor implements ApplicationContextAware
{
    private static final Logger             logger = Logger.getLogger(MethodHandles.lookup().lookupClass());

    private ApplicationContext              context;

    @PersistenceContext
    private EntityManager                   entityManager;

    private static Set<EntityType<?>>       types;
    private static Set<EmbeddableType<?>>   embeddables;
    
    public static final class ScriptParameter {
        private final String      name;
        private final Object      value;

        public ScriptParameter(String name, Object value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public Object getValue() {
            return value;
        }
    }

    public Set<EntityType<?>> getTypes() {
        if (types == null)
            types = entityManager.getMetamodel().getEntities();

        return types;
    }

    public Set<EmbeddableType<?>> getEmbeddables() {
        if (embeddables == null)
            embeddables = entityManager.getMetamodel().getEmbeddables();

        return embeddables;
    }
    
    /**
     * Function executes DSL script
     * @param groovyScript DSL script resource name
     * @param parameters Additional script parameters
     * @return script result
     * @throws Exception 
     */
    public Object execute (String groovyScript, ScriptParameter ... parameters) throws Exception {
        GroovyClassLoader ldr = new GroovyClassLoader(getClass().getClassLoader());
        Class baseCl = ldr.parseClass(new File (getClass().getResource("/org/mesol/spmes/service/dsl/DslBase.groovy").toURI()));
        CompilerConfiguration cc = new CompilerConfiguration();
        cc.setScriptBaseClass(baseCl.getName());
        cc.setDebug(true);
        cc.setVerbose(true);

        Binding binding = new Binding ();
        binding.setVariable ("context", context);
        binding.setVariable ("entities", getTypes());
        binding.setVariable ("embeddables", getEmbeddables());
        for (ScriptParameter param : parameters) {
            binding.setVariable(param.getName(), param.getValue());
        }

        GroovyShell gs = new GroovyShell(ldr, binding, cc);
        Script script = gs.parse(new File(new URL(groovyScript).toURI()));
        return script.run();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
