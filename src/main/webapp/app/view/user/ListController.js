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
Ext.define("NeoMes.view.user.ListController", {
    extend: 'Ext.app.ViewController',
    requires: [
      'NeoMes.view.user.Edit'  
    ],
    alias: 'controller.list',
    
    control: {
        '#': {
            itemdblclick: 'editUser'
        },
        'users button[action=sync]': {
            click: 'syncUser'
        },
        'useredit button[action=save]': {
            click: 'updateUser'
        }
    },
    editUser: function(grid, record) {
        var win = new NeoMes.view.user.Edit ();
        win.down('form').loadRecord (record);
        win.show();
    },
    updateUser: function(button) {
        var win = button.up('window'),
                form = win.down('form'),
                record = form.getRecord(),
                values = form.getValues();

        record.set(values);
        win.close();
    },
    syncUser: function(button) {
        this.getView().getStore().sync();
    }
});

