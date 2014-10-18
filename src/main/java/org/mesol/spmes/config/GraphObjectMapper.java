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

package org.mesol.spmes.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import java.lang.invoke.MethodHandles;
import java.util.HashSet;
import org.apache.log4j.Logger;

/**
 * $Rev:$
 * $Author:$
 * $Date:$
 * 
 * @version 1.0.0
 * @author ASementsov
 */
public class GraphObjectMapper extends ObjectMapper 
{
    private static final Logger     logger = Logger.getLogger(MethodHandles.lookup().lookupClass());
    
    static final long serialVersionUID = 1L;
    

    public static String getRevisionNumber() {
        return "$Revision:$";
    }

    public GraphObjectMapper() {
        super ();
        _serializationConfig = _serializationConfig
            .withSerializationInclusion(JsonInclude.Include.NON_NULL)
            .withSerializationInclusion(JsonInclude.Include.NON_EMPTY)
            .without(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .withFilters(new JsonFilter());
        
        setVisibility(PropertyAccessor.GETTER, Visibility.PUBLIC_ONLY);
    }

    public static class JsonFilter extends SimpleFilterProvider {

        private static final HashSet<String> hiddenFields = new HashSet<>();
        static {
            hiddenFields.add("password");
            hiddenFields.add("callbacks");
            hiddenFields.add("managed");
        }
        private final PropertyFilter pf;

        public JsonFilter() {
            this.pf = new SimpleBeanPropertyFilter() {
                @Override
                protected boolean include(BeanPropertyWriter writer) {
                    return !hiddenFields.contains(writer.getName());
                }
                
                @Override
                protected boolean include(PropertyWriter writer) {
                    return !hiddenFields.contains(writer.getName());
                }
            };
        }
        
        @Override
        public PropertyFilter findPropertyFilter(Object filterId, Object valueToFilter) {
            return pf;
        }
    }
}
