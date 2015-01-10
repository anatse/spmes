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
package org.mesol.spmes.gwt.client;

import com.smartgwt.client.widgets.Canvas;
import java.util.HashMap;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
public final class ContentFactory 
{
    private static final HashMap<String, Canvas>       loadedObjects = new HashMap<>();
    private ContentFactory() {}
    public static Canvas getCanvas (String name) {
        Canvas ret = loadedObjects.get(name);
        if (ret == null) {
            switch (name) {
                case "Users":
                    ret = new Users();
                    loadedObjects.put(name, ret);
                    break;

                default:
                    break;
            }
        }

        return ret;
    }
}
