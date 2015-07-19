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
package org.mesol.spmes.gwt.client.abs;

import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 *
 * @author gosha
 */
public class FormBuilder {
    private VLayout         mainPanel;
    private Canvas          center;
    private Canvas          top;
    private Canvas          bottom;

    protected FormBuilder () {
    }

    public static FormBuilder start() {
        return new FormBuilder();
    }

    public FormBuilder setCenter(Canvas center) {
        this.center = center;
        return this;
    }

    public FormBuilder setTop(Canvas top) {
        this.top = top;
        return this;
    }

    public FormBuilder setBottom(Canvas bottom) {
        this.bottom = bottom;
        return this;
    }

    public Canvas build () {
        mainPanel = new VLayout();
        if (top != null) {
            mainPanel.addMember(top);
        }
        
        if (center != null) {
            mainPanel.addMember(center);
        }
        
        if (bottom != null) {
            mainPanel.addMember(bottom);
        }

        return mainPanel;
    }

    public class HandleButton extends Button {
        public HandleButton(String title, ClickHandler handler) {
            super(title);
            addClickHandler(handler);
        }
    }
}
