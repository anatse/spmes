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

package org.mesol.spmes.gwt.server;

import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import org.mesol.spmes.gwt.shared.BaseDataSource;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
public final class MenuDS extends BaseDataSource
{
    private static MenuDS instance;
    
    protected MenuDS () {
        super();

        /*
        * Add fields
        */
        DataSourceIntegerField idField = new DataSourceIntegerField("id");
        idField.setPrimaryKey(true);
        DataSourceTextField nameField = new DataSourceTextField("name");
        DataSourceTextField urlField = new DataSourceTextField("url");
        DataSourceTextField descField = new DataSourceTextField("description");
        setFields(idField, nameField, urlField, descField);
        /*
         * Data URL
         */
        setDataURL("service/menu");
        
        /*
         * Root element 
         */
        setRecordXPath("menuList");
    }
    
    public static MenuDS get () {
        if (instance == null)
            instance = new MenuDS();
        
        return instance;
    }
}
