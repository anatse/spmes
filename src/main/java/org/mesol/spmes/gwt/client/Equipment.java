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

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.PropertySheet;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import org.mesol.spmes.gwt.client.abs.BaseTree;
import org.mesol.spmes.gwt.server.EquipmentDS;
import org.mesol.spmes.gwt.shared.ButtonNameConstants;
import org.mesol.spmes.gwt.shared.FieldNamesConstants;
import org.mesol.spmes.gwt.shared.TitlesConstants;

/**
 *
 * @author gosha
 */
public class Equipment extends VLayout 
{
    private final ButtonNameConstants   buttonNames = GWT.create(ButtonNameConstants.class);
    private final TitlesConstants       titles = GWT.create(TitlesConstants.class);
    private final FieldNamesConstants   fieldNames = GWT.create(FieldNamesConstants.class);

    public Equipment() {
        SC.say("title: " + titles.equipments());
        setTitle("Title: " + titles.equipments());
        addMember(createTreePanel());
    }
    
    private Canvas createTreePanel () {
        HLayout treePane = new HLayout();
        treePane.setTitle(titles.equipments());
        treePane.setWidth100();
        treePane.setHeight100();

        BaseTree<EquipmentDS> equipmentTree = new BaseTree<>(EquipmentDS.get());
        equipmentTree.setWidth(300);
        equipmentTree.setHeight100();
        treePane.addMember(equipmentTree);
        
        SC.say("title: " + titles.equipments());

//        VLayout right = new VLayout();
//        right.setHeight100();
//        DynamicForm form = new DynamicForm();
//        form.setNumCols(6);
//        form.setDataSource(equipmentTree.getDataSource());
//        treePane.addMember(form);
//        PropertySheet psheet = new PropertySheet();
//        psheet.setDataSource(equipmentTree.getDataSource());
//        treePane.addMember(psheet);

        final Button btnUpdate = new Button(buttonNames.update());
        btnUpdate.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        treePane.addMember(btnUpdate);
//
//        right.addMember(btnUpdate);
//        treePane.addMember(right);
        return treePane;
    }
}
