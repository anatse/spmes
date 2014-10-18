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
Ext.define('NeoMes.view.user.Edit', {
    extend: 'Ext.window.Window',
    alias: 'widget.useredit',
    controller: 'list',

    title: 'Edit User',
    layout: 'fit',
    autoShow: true,

    initComponent:function () {
        this.items = [{
            xtype:'form',
            items:[{
                xtype:'textfield',
                name:'username',
                fieldLabel:'Name'
            }, {
                xtype:'textfield',
                name:'email',
                fieldLabel:'Email'
            }, {
                xtype:'hidden',
                name:'id',
                fieldLabel:'id'
            }]
        }];

        this.buttons = [{
            text:'Save',
            action:'save'
        }, {
            text:'Cancel',
            scope:this,
            handler:this.close
        }];

        this.callParent(arguments);
    }
});

