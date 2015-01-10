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

import com.google.gwt.core.shared.GWT;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import org.mesol.spmes.gwt.server.GroupsDS;
import org.mesol.spmes.gwt.server.UsersDS;
import org.mesol.spmes.gwt.shared.ButtonNameConstants;
import org.mesol.spmes.gwt.shared.TitlesConstants;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
public final class Users extends VLayout
{
    private final ButtonNameConstants   buttonNames = GWT.create(ButtonNameConstants.class);
    private final TitlesConstants       titles = GWT.create(TitlesConstants.class);

    private ListGrid createGrid () {
        UsersDS ds = UsersDS.get();
        ListGrid grid = new ListGrid(ds);
        grid.setWidth100();
        grid.setHeight(400);
        grid.setShowResizeBar(true);
        grid.setShowCustomScrollbars(true);
        grid.setShowFilterEditor(true);
        grid.setAllowFilterExpressions(true);
        grid.fetchData();
        grid.setShowDetailFields(false);
        
        return grid;
    }
    
    private Canvas createDetails (final ListGrid grid) {
        VLayout vl = new VLayout();
        vl.setWidth100();
        vl.setHeight100();
        final DynamicForm form = new DynamicForm();
        form.setNumCols(6);
        form.setDataSource(grid.getDataSource());
        vl.addMember(form);

        grid.addRecordClickHandler(new RecordClickHandler() {
            @Override
            public void onRecordClick(RecordClickEvent event) {
                form.reset();
                form.editSelectedData(grid);
                GroupsDS.get().fetchData(
                    new Criteria("username", event.getRecord().getAttributeAsString("username"))
                );
            }
        });
        
        HLayout hl = new HLayout();
        Button btnUpdate = new Button(buttonNames.update());
        btnUpdate.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                form.saveData(new DSCallback() {
                    @Override
                    public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest) {
                        // Refresh datasource
                        grid.invalidateCache();
                        form.reset();
                    }
                });
            }
        });
        hl.addMember(btnUpdate);

        Button btnAdd = new Button(buttonNames.add());
        btnAdd.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                form.editNewRecord();
                form.reset();
            }
        });
        hl.addMember(btnAdd);
        vl.addMember(hl);
        return vl;
    }
    
    public Users () {
        ListGrid grid = createGrid();
        grid.setWidth100();
        addMember(grid);

        TabSet ts = new TabSet();
        ts.setWidth100();
        ts.setHeight100();
        ts.setTabBarPosition(Side.TOP);

        Tab details = new Tab(titles.details());
        details.setPane (createDetails(grid));
        
        Tab roles = new Tab(titles.roles());
        
        ts.addTab (details);
        ts.addTab (roles);

        addMember(ts);
    }
}
