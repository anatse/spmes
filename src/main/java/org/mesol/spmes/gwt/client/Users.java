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
import com.smartgwt.client.data.Record;
import com.smartgwt.client.rpc.RPCManager;
import com.smartgwt.client.rpc.RPCRequest;
import com.smartgwt.client.types.LayoutPolicy;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.layout.VStack;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mesol.spmes.gwt.server.AvailableGroupsDS;
import org.mesol.spmes.gwt.server.UserGroupsDS;
import org.mesol.spmes.gwt.server.UsersDS;
import org.mesol.spmes.gwt.shared.BaseDataSource;
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
    private final ListGrid              usersGrid;
    private final ListGrid              groupsGrid;

    /**
     * Function create dat agrid with defined data source
     * @param ds ata source
     * @param fetchData flag indicates load data initally or not
     * @return new cerated grid
     */
    private ListGrid createGrid (BaseDataSource ds, boolean fetchData) {
        ListGrid grid = new ListGrid(ds);
        grid.setWidth100();
        grid.setHeight(400);
        grid.setShowResizeBar(true);
        grid.setShowCustomScrollbars(true);
        grid.setShowFilterEditor(fetchData);
        grid.setAllowFilterExpressions(true);
        if (fetchData) 
            grid.fetchData();

        grid.setShowDetailFields(false);
        return grid;
    }
 
    /**
     * Fucntion create
     * @param userGrid
     * @param groupsGrid
     * @return 
     */
    private Canvas createUserDetails () {
        final VLayout layout = new VLayout();
        layout.setWidth100();
        layout.setHeight100();
        final DynamicForm form = new DynamicForm();
        form.setNumCols(6);
        form.setDataSource(usersGrid.getDataSource());
        layout.addMember(form);

        usersGrid.addRecordClickHandler(new RecordClickHandler() {
            @Override
            public void onRecordClick(RecordClickEvent event) {
                form.reset();
                form.editSelectedData(usersGrid);
                groupsGrid.fetchData(
                    new Criteria("username", event.getRecord().getAttributeAsString("username"))
                );
            }
        });

        final HLayout hlayout = new HLayout();
        final Button btnUpdate = new Button(buttonNames.update());
        btnUpdate.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                form.saveData(new DSCallback() {
                    @Override
                    public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest) {
                        // Refresh datasource
                        usersGrid.invalidateCache();
                        form.reset();
                    }
                });
            }
        });
        hlayout.addMember(btnUpdate);

        Button btnAdd = new Button(buttonNames.add());
        btnAdd.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                form.editNewRecord();
                form.reset();
            }
        });
        hlayout.addMember(btnAdd);
        layout.addMember(hlayout);
        return layout;
    }

    /**
     * Function create user groups panel
     * @return 
     */
    private Canvas createUserGroupsPanel () {
        final VStack layout = new VStack();
        layout.setWidth100();
        layout.setHeight100();
        layout.setVPolicy(LayoutPolicy.FILL);
        layout.addMember(groupsGrid);
        
        final HStack hlayout = new HStack();
        hlayout.setMargin(5);
        final Button btnDelete = new Button(buttonNames.delete());
        btnDelete.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                SC.say("delete: " + event.toString());
            }
        });
        btnDelete.disable();
        hlayout.addMember(btnDelete);

        Button btnAdd = new Button(buttonNames.add());
        btnAdd.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                createGroupsWindow(groupsGrid.getCriteria().getAttributeAsString("username"));
            }
        });
        hlayout.addMember(btnAdd);
        layout.addMember(hlayout);
        
        groupsGrid.addRecordClickHandler(new RecordClickHandler() {
            @Override
            public void onRecordClick(RecordClickEvent event) {
                if (groupsGrid.getSelectedRecords().length > 0) {
                    btnDelete.enable();
                }
                else {
                    btnDelete.disable();
                }
            }
        });
        
        return layout;
    }
    
    public Users () {
        usersGrid = createGrid(UsersDS.get(), true);
        usersGrid.setWidth100();
        addMember(usersGrid);
        groupsGrid = createGrid(UserGroupsDS.get(), false);

        TabSet ts = new TabSet();
        ts.setWidth100();
        ts.setHeight100();
        ts.setTabBarPosition(Side.TOP);

        Tab details = new Tab(titles.details());
        details.setPane (createUserDetails());

        Tab roles = new Tab(titles.roles());
        roles.setPane(createUserGroupsPanel());

        ts.addTab (details);
        ts.addTab (roles);

        addMember(ts);
    }
    
    private void createGroupsWindow (final String userName) {
        final Window winModal = new Window();
        final ListGrid availGroups = createGrid(AvailableGroupsDS.get(), false);
        availGroups.fetchData(
            new Criteria("username", userName)
        );
        winModal.setWidth(800);
        winModal.setHeight(600);
        winModal.setTitle(titles.groups());
        winModal.setShowMinimizeButton(false);
        winModal.setIsModal(true);
        winModal.setShowModalMask(true);
        winModal.centerInPage();

        VStack layout = new VStack ();
        layout.setWidth100();
        layout.setHeight100();
        layout.addMember(availGroups);
        
        final HStack hlayout = new HStack();
        hlayout.setMargin(5);
        final Button btnOk = new Button(buttonNames.ok());
        btnOk.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // Get selected groups
                ListGridRecord[] recs = availGroups.getSelectedRecords();
                List<String> groups = new ArrayList<>();
                for (ListGridRecord rec : recs) {
                    String grp = rec.getAttribute("name");
                    groups.add(grp);
                }

                RPCRequest request = new RPCRequest();
                Map<String, Object> rec = new HashMap<>();
                rec.put("username", userName);
                rec.put("groups", groups);
                request.setHttpHeaders(BaseDataSource.buildHeaders());
                request.setParams(rec);
                request.setActionURL("service/user/grp/add");
                request.setHttpMethod("POST");
                request.setUseSimpleHttp(true);
                RPCManager.sendRequest(request);
                
                // refresh groups
                groupsGrid.invalidateCache();
                winModal.close();
            }
        });
        btnOk.disable();
        hlayout.addMember(btnOk);

        Button btnCancel = new Button(buttonNames.cancel());
        btnCancel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                winModal.close();
            }
        });
        hlayout.addMember(btnCancel);
        layout.addMember(hlayout);
        
        availGroups.addRecordClickHandler(new RecordClickHandler() {
            @Override
            public void onRecordClick(RecordClickEvent event) {
                if (availGroups.getSelectedRecords().length > 0) {
                    btnOk.enable();
                }
                else {
                    btnOk.disable();
                }
            }
        });
        
        winModal.addItem (layout);
        
        /*
        * Destroy handler, do not change to Java 8 lambda while GWT 2.7 used
        */
        winModal.addCloseClickHandler(new CloseClickHandler() {
            @Override
            public void onCloseClick(CloseClickEvent event) {
                winModal.destroy();
            }
        });

        winModal.show();
    }
}
