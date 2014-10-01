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
Ext.define("NeoMes.controller.App", {
    extend: 'Ext.app.Controller',

    refs: [{
        ref: 'contentPanel',
        selector: 'contentPanel'
    }],

    init: function() {
        this.control ({
            'navigation': {
                selectionchange: this.onNavSelectionChange
            }
        });
    },
    onNavSelectionChange: function(selModel, records) {
        var record = records[0],
            xtype = record.get('url'),
            alias = 'widget.' + xtype;
    
        if (!this.getContentPanel)
            return;
    
        var contentPanel = this.getContentPanel();
        contentPanel.setTitle (null);
        
        if (xtype) {
            contentPanel.removeAll(true);

            var className = Ext.ClassManager.getNameByAlias(alias);
            if (!className) {
                if (console)
                    console.error ('Not found module: ' + xtype);

                return;
            }

            // load controller class
//            var controller = Ext.create(className + 'Controller');
            var viewClass = Ext.ClassManager.get(className);
            var cmp = new viewClass();
            contentPanel.add(cmp);
            contentPanel.setTitle (NeoMes.utils.CommonUtils.getLocaleString('titles', xtype));

            if (cmp.floating) {
                cmp.show();
            } 
            else {
                if (this.centerContent)
                    this.centerContent();
            }
        }
    }
});

