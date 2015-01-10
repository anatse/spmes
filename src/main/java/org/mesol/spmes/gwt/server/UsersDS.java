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

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.OperationBinding;
import com.smartgwt.client.data.fields.DataSourcePasswordField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSOperationType;
import com.smartgwt.client.types.DSProtocol;
import com.smartgwt.client.types.FieldType;
import com.smartgwt.client.util.JSON;
import com.smartgwt.client.widgets.form.validator.RegExpValidator;
import org.mesol.spmes.gwt.shared.BaseDataSource;
import org.mesol.spmes.gwt.shared.FieldNamesConstants;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
public class UsersDS extends BaseDataSource
{
    private final FieldNamesConstants   fieldNames = GWT.create(FieldNamesConstants.class);
    private static UsersDS              instance;
    
    protected UsersDS () {
        super();

        setCacheAllData(false);
        setAutoCacheAllData(false);
        setClientOnly(false);

        /*
        * Add fields
        */
        DataSourceTextField unameField = new DataSourceTextField("username", fieldNames.name());
        unameField.setPrimaryKey(true);
        DataSourcePasswordField passwordField = new DataSourcePasswordField("password", fieldNames.password());
        DataSourceTextField firstNameField = new DataSourceTextField("firstName", fieldNames.firstName());
        DataSourceTextField lastNameField = new DataSourceTextField("lastName", fieldNames.lastName());
        
        /**
         * Email field with validating
         */
        RegExpValidator regExpValidator = new RegExpValidator();
        regExpValidator.setExpression("^([a-zA-Z0-9_.\\-+])+@(([a-zA-Z0-9\\-])+\\.)+[a-zA-Z0-9]{2,4}$");
        DataSourceTextField emailField = new DataSourceTextField("email", fieldNames.email());
        emailField.setValidators(regExpValidator);

//        DataSourceField groups = new DataSourceTextField("groups");
//        groups.setTypeAsDataSource (GroupsDS.get());
//        groups.setMultiple(true);
//        groups.setDetail(true);

        setFields(unameField, passwordField, firstNameField, lastNameField, emailField);
        /*
         * Data URL
         */
        setDataURL("service/user/list");
        
        /*
         * Root element 
         */
        setRecordXPath("userList");
        
        /*
         * Operation bindings
         */
        setOperationBindings(
            updateOperation(),
            addOperation(),
            new OperationBinding(DSOperationType.REMOVE, "service/user/delete")
        );
    }
    
    private OperationBinding addOperation () {
        OperationBinding oper = new OperationBinding(DSOperationType.ADD, "service/user/add");
        oper.setUseFlatFields(false);
        oper.setDataFormat(DSDataFormat.JSON);
        oper.setDataProtocol(DSProtocol.POSTMESSAGE);
        return oper;
    }
    
    private OperationBinding updateOperation () {
        OperationBinding oper = new OperationBinding(DSOperationType.UPDATE, "service/user/update");
        oper.setUseFlatFields(false);
        oper.setDataFormat(DSDataFormat.JSON);
        oper.setDataProtocol(DSProtocol.POSTMESSAGE);
        return oper;
    }

    @Override
    protected Object transformRequest(DSRequest dsRequest) {
        switch (dsRequest.getOperationType()) {
            case ADD:
            case UPDATE:
                dsRequest.setContentType("application/json");
                return JSON.encode(dsRequest.getData());
        }

        return super.transformRequest(dsRequest);
    }
    
    public static UsersDS get () {
        if (instance == null)
            instance = new UsersDS();
        
        return instance;
    }
}
