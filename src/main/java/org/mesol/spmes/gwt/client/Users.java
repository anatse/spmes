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

package org.mesol.spmes.gwt.client;

import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;
import org.mesol.spmes.gwt.server.UsersDS;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
public final class Users extends VLayout
{
    public Users () {
        final ListGrid grid = new ListGrid(UsersDS.get());
        grid.setAutoFetchData(true);
        grid.setWidth100();
        grid.setHeight(400);
        addMember(grid);
        
        final DynamicForm form = new DynamicForm();
        form.setNumCols(4);
        form.setDataSource(UsersDS.get());
        addMember(form);

        grid.addRecordClickHandler(new RecordClickHandler() {
            public void onRecordClick(RecordClickEvent event) {
                form.reset();
                form.editSelectedData(grid);
            }
        });
    }
}
