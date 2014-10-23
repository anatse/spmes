/* 
 * Copyright 2014 ASementsov.
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
//Ext.define('NeoMes.view.factory.Equipment', {
//    extend: 'Ext.tree.Panel',
////    requires: [
////        'Ext.draw.Component'
////    ],
//    xtype: 'treepanel',
//    layout: 'border',
Ext.define('NeoMes.view.factory.Equipment', {
    extend: 'Ext.panel.Panel',
    xtype: 'equipmentPanel',
    alias: 'widget.equipment',    
    requires: [
        'Ext.layout.container.Border',
        'NeoMes.controller.Equipment',
        'NeoMes.model.EquipmentAttributes'
    ],
    controller: 'equipment',
    layout: 'border',
//    width: 500,
    height: 680,
    border: false,
    defaults: {
        collapsible: true,
        split: true
//        stateful: true
        //bodyPadding: 10
    },

    items: [
        {
            title: 'Equipment Attributes',
            region: 'south',
            height: '50%',
            tbar : [{
                text : "Добавить",
                xtype : 'button',
                height : '20px',
                action : 'eqAttrAdd'
//                listeners : {
//                        scope : this,
//                        click : function(node, rec) {alert('OK!')}
//                }
            },{
                text : "Удалить",
                xtype : 'button',
                height : '20px',
                action : 'eqAttrDel'
//                listeners : {
//                        scope : this,
//                        click : function(node, rec) {alert('OK!')}
//                }
            },{
                text : "Сохранить",
                xtype : 'button',
                height : '20px',
                action : 'eqAttrSave'
//                listeners : {
//                        scope : this,
//                        click : function(node, rec) {alert('OK!')}
//                }
            }],            
            items:[{
                xtype: 'propertygrid',
                id: 'eqAttrGrid',
                nameColumnWidth: 165,
                valueField: 'attrValue',
                nameField: 'name',
                source: {}
            }]
            
//            minHeight: 75,
//            maxHeight: 150,
            //html: '<p>Eq Attr content</p>'
        },
        {
            title: 'Equipment:',
            collapsible: false,
            height: '50%',
            tbar : [{
                text : "Добавить",
                xtype : 'button',
                height : '20px',
                action : 'eqAdd'
            },{
                text : "Изменить",
                xtype : 'button',
                height : '20px',
                action : 'eqUpd'
            },{
                text : "Удалить",
                xtype : 'button',
                height : '20px',
                action : 'eqDel'
            }],
            region: 'center',
            autoScroll: true,
            items:[{
                xtype: 'treepanel',
                id: 'eqGrid',
                idField: 'id',
                displayField: 'name',
                autoScroll: false,
                rootVisible: false,
                store: Ext.create('NeoMes.store.Equipment'),
                lines: false,
                listeners: {
                    itemclick: function(node, rec) {                                    
                        var propGrid = Ext.getCmp('eqAttrGrid');
                        if (propGrid)
                        {
                            
//                            var attributes = [];
//                            attributes = Ext.Array.toArray(rec.get('attributes'));
//                            
//                            var source = {};
//                            
//                            Ext.Array.forEach(attributes, function(record) {
//                              source[record.name] = record.attrValue;
//                            });
//                            
//                            propGrid.setSource(source);        
                              propGrid.setStore(this.store.getById(rec.get('id')).attrs());//this.store.getAt(1).attrs());
//                              propGrid.store.reload();
//                              propGrid.store.sync();
                              //propGrid.reconfigure(this.store.getById(rec.get('id')).attrs(), [{'dataIndex': 'eqId', 'text' : 'equipment id'}]
//                                      );
//                              propGrid.store.save();
//                              propGrid.store.load();
                         //     alert("ok!");
                        }
                    }
                }
            }]
        }
    ],
    initComponent: function() {

        this.callParent();
    }      
});

