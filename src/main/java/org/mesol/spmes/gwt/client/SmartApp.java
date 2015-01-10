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

import com.google.gwt.core.client.EntryPoint;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.HLayout;
import java.util.HashMap;
import java.util.Map;

/**
 * Main GUI class
 * How-to use JSNI (native JavaScript interface) see https://lustforge.com/2012/11/11/gwt-jsni-variables-an-exhaustive-list/
 * 
 * @version 1.0.0
 * @author ASementsov
 */
public class SmartApp implements EntryPoint
{
    public SmartApp() {
    }
    
    @Override
    public void onModuleLoad() {
        HLayout layout = new HLayout();
        layout.setWidth100();
        layout.setHeight100();

        MenuTree tr = new MenuTree();
        tr.setWidth(200);
        tr.setHeight100();
        layout.addMember(tr);

        final ContentView view = new ContentView();
        view.setHeight100();
        view.setWidth100();
        tr.addSelectionChangedHandler(new SelectionChangedHandler() {
            @Override
            public void onSelectionChanged(SelectionEvent event) {
                Canvas[] children = view.getChildren();
                if (children != null) {
                    for (Canvas child : children)
                        view.removeChild(child);
                }

                String url = event.getSelectedRecord().getAttribute("url");
                if (url != null) {
                   Canvas module = ContentFactory.getCanvas(url);
                   if (module == null) {
                       SC.say("Not found module: " + url);
                   }
                   else {
                       module.setWidth100();
                       module.setHeight100();
                       view.addChild(module);
                   }
                }
            }
        });

        layout.addMember(view);
        layout.draw();
    }
}
