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

import java.beans.PropertyDescriptor;
import org.apache.log4j.Logger;
import java.lang.invoke.MethodHandles;
import org.joda.time.DateTime;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
public class EntityCopier 
{
    private static final Logger     logger = Logger.getLogger(MethodHandles.lookup().lookupClass());

    public static BeanWrapper getWrapper (Object obj) {
        final BeanWrapper bw = new BeanWrapperImpl(obj);
        bw.registerCustomEditor(DateTime.class, new JodaDateTimeEditor());
        return bw;
    }
    
    public static <T> T copy (T from, T to) {
        final BeanWrapper fromB = getWrapper(from);
        final BeanWrapper toB = getWrapper(to);
        for (PropertyDescriptor pd : fromB.getPropertyDescriptors()) {
            String name = pd.getName();
            if (fromB.isReadableProperty(name) && toB.isWritableProperty(name)) {
                Object value = fromB.getPropertyValue(name);
                Object oldValue = toB.getPropertyValue(name);
                if (value != null) {
                    if (!value.equals(oldValue))
                        toB.setPropertyValue(name, value);
                }
//                else if (oldValue != null) {
//                    
//                    
//                    toB.setPropertyValue(name, value);
//                }
            }
        }

        return to;
    }
}
