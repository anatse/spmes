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

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import org.mesol.spmes.gwt.shared.BaseDataSource;
import org.mesol.spmes.gwt.shared.FieldNamesConstants;

/**
 *
 * @author gosha
 */
public class AvailableGroupsDS extends BaseDataSource
{
    private final FieldNamesConstants       fieldNames = GWT.create(FieldNamesConstants.class);
    private static AvailableGroupsDS        instance;
    
    public AvailableGroupsDS() {
        super();

        setCacheAllData(false);
        setAutoCacheAllData(false);
        setClientOnly(false);

        DataSourceTextField gname = new DataSourceTextField("name", fieldNames.name());
        gname.setPrimaryKey(true);

        DataSourceField idField = new DataSourceTextField("id", fieldNames.id());
        idField.setHidden(true);
        setFields(gname, idField);
        setTitleField("name");

        setDataURL("service/user/grp/availist");
        setRecordXPath("userGroupList");
    }
 
    public static AvailableGroupsDS get () {
        if (instance == null) {
            instance = new AvailableGroupsDS();
        }
        
        return instance;
    }
}
