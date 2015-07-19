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
package org.mesol.spmes.gwt.server;

import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import org.mesol.spmes.gwt.shared.BaseDataSource;

/**
 *
 * @author gosha
 */
public class EquipmentDS extends BaseDataSource 
{
    private static EquipmentDS instance;

    public static EquipmentDS get() {
        if (instance == null) {
            instance = new EquipmentDS();
        }

        return instance;
    }

    public EquipmentDS() {
        super();

        /*
        * Add fields
        */
        DataSourceIntegerField idField = new DataSourceIntegerField("id");
        idField.setPrimaryKey(true);
        DataSourceTextField nameField = new DataSourceTextField("name");
        DataSourceTextField descField = new DataSourceTextField("description");
        setFields(idField, nameField, descField);
        /*
         * Data URL
         */
        setDataURL("service/equipment/list");
        
        /*
         * Root element 
         */
        setRecordXPath("equipmentList");
    }
}
