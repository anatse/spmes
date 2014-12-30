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

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeGridField;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.smartgwt.client.widgets.tree.events.FolderOpenedEvent;
import com.smartgwt.client.widgets.tree.events.FolderOpenedHandler;
import java.util.ArrayList;
import java.util.List;
import org.mesol.spmes.gwt.server.MenuDS;
import org.mesol.spmes.gwt.shared.FieldNamesConstants;

/**
 *
 * @version 1.0.0
 * @author ASementsov
 */
public final class MenuTree extends TreeGrid {

    private final FieldNamesConstants fieldNames = GWT.create(FieldNamesConstants.class);

    public MenuTree() {
        setShowConnectors(true);
        setShowResizeBar(true);
        setAlwaysShowOpener(Boolean.TRUE);
        setLoadDataOnDemand(Boolean.TRUE);
        setLoadingDataMessage("Loading...");

        TreeGridField nameField = new TreeGridField("name", fieldNames.name());
        setFields(nameField);

        // data source binding
        final Tree tree = new Tree();
        tree.setDefaultIsFolder(Boolean.TRUE);
        tree.setNameProperty("name");
        tree.setModelType(TreeModelType.CHILDREN);
        tree.setChildrenProperty("children");

        final TreeNode root = new TreeNode("root");
        root.setTitle("root");
        root.setAttribute("name", "root");
        root.setAttribute("id", "-1");
        tree.setRoot(root);

        fetchData (root, tree);
        setData(tree);
        
        addFolderOpenedHandler(new FolderOpenedHandler() {
            @Override
            public void onFolderOpened(FolderOpenedEvent event) {
                fetchData (event.getNode(), tree);
            }
        });
    }

    private void fetchData (final TreeNode root, final Tree tree) {
        final DataSource ds = MenuDS.get();
        Criteria cr = new Criteria("parentId", root.getAttribute("id"));
        ds.fetchData(cr, new DSCallback() {
            @Override
            public void execute(DSResponse dsResponse, Object data, DSRequest dsRequest) {
                tree.unloadChildren(root);
                List<TreeNode> nodes = new ArrayList<>();
                for (Record rec : dsResponse.getData()) {
                    TreeNode tn = new TreeNode();
                    String name = rec.getAttribute("name");
                    tn.setName(name);
                    for (DataSourceField field : ds.getFields()) {
                        tn.setAttribute(field.getName(), rec.getAttribute(field.getName()));
                    }
                    nodes.add(tn);
                }
                tree.addList(nodes.toArray(new TreeNode[nodes.size()]), root);
            }
        });
    }
}
