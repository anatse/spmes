Ext.define("NeoMes.controller.Equipment", {
    extend: 'Ext.app.ViewController',
    stores: ['Equipment'],
    models: ['Equipment'],
    views: ['factory.Equipment'],
    alias: 'controller.equipment',

    refs: [{
        ref: 'contentPanel',
        selector: 'contentPanel'
    }],

    init: function() {
        this.control ({
           'equipment button[action=eqAdd]': {
                click: this.equipmentAdd
            },
            'equipment button[action=eqUpd]': {
                click: this.equipmentUpd
            },
            'equipment button[action=eqDel]': {
                click: this.equipmentDel
            },
            'equipment button[action=eqAttrAdd]': {
                click: this.equipmentAttrAdd
            },
            'equipment button[action=eqAttrSave]': {
                click: this.equipmentAttrSave
            },
            'equipment button[action=eqAttrDel]': {
                click: this.equipmentAttrDel
            }            
        });
    },
//    equipmentAdd: function(grid, record) {
//       alert("equipmentAdd!");
//       
//       
//       
//       
//    },

    getEQSelection:function () {
        var eqNode = Ext.getCmp('eqGrid').getSelectionModel().getSelectedNode();
        return eqNode.length > 0 ? eqNode[0] : null;
    },
    
    equipmentAdd: function () {
        //var required = '<span style="color:red;font-weight:bold" data-qtip="Required">*</span>';
        var win = null;
        
        var eqNode = this.getEQSelection();
        
//        if (!soRow)
//        {
//            Ext.MessageBox.alert('Сначала нужно выбрать производственный заказ!');
//            return;            
//        }
               
//        if (maxCnt <= 0) {
//            Ext.MessageBox.alert('Выбранный заказ целиком запущен в производство!');
//            return;
//        }
        
        win = new Ext.Window ({
            width: 500,
            height: 200, 
            modal: true,
            title: 'Новое оборудование',
            layout: 'fit',           
            items: [{
                xtype: 'form',
                layout: 'form',
                id: 'equipmentAddForm',
                frame: true,
                bodyPadding: '5 5 0',
                width: 450,
                fieldDefaults: {
                    msgTarget: 'side',
                    labelWidth: 150
                },
                defaultType: 'textfield',
                items: [
                    {
                        fieldLabel: 'Наименование',
                        name: 'NAME'
                        //value:soRow.get('SHOP_ORDER').toString()+'00'+(rcCount ? rcCount.toString() : '0'),
                        //readOnly: true 
                    },{
                        fieldLabel: 'Описание',
                        name: 'DESCRIPTION'
                    },{
                        xtype: 'hiddenfield',
                        fieldLabel: 'PARENT',
                        name: 'PARENT',
                        value: eqNode ? eqNode.get('PARENT').toString() : ''
                    }                    
                ],
                buttons: [
                {
                    text: 'Сохранить',
                    scope: this,
                    listeners: {
                        scope: this,
                        click: function () {
                            var form = this.up('form');
                            if (form.isValid()){
                                var values = form.getValues(false, false, false, true);
                                //this.saveEQ(values);
                                alert("parent_val:"+values.parent)
                                //win.close ();
                            }
                            else {
                                Ext.MessageBox.alert('Errors', 'Проверьте пожалуйста введенные значения');
                            }
                        }
                    }
                }, {
                    text: 'Закрыть',
                    scope: this,
                    listeners: {
                        scope: this,
                        click: function () {
                            win.close();
                        }
                    }
                }                    
                    
              ]
           }]  
        });

        win.show(null, null, this);
    },
    
    equipmentUpd: function(button) {
       alert("equipmentUpd!"); 
    },
    equipmentDel: function(button) {
        alert("equipmentUpd!"); 
    }
});