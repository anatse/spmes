Ext.define("NeoMes.controller.Equipment", {
    extend: 'Ext.app.ViewController',
    stores: ['Equipment'],
    models: ['Equipment'],
    views: ['factory.Equipment'],
    alias: 'controller.equipment',

    requires: [
      'NeoMes.view.factory.EquipmentUpd'  
    ],
    
    refs: [{
        ref: 'contentPanel',
        selector: 'contentPanel'
    }],

    init: function() {
        this.control ({
//            '#': {
//                itemdblclick: this.equipmentUpd
//            },
           'equipment button[action=eqAdd]': {
                click: this.equipmentAdd
            },
            'equipment button[action=eqUpd]': {
                click: this.equipmentUpd
            },
           'equipmentupd button[action=eqUpdSave]': {
                click: this.equipmentUpdSave
            },             
            'equipment button[action=eqDel]': {
                click: this.equipmentDel
            }
            ,
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
    equipmentAdd: function(button) {
       alert("equipmentAdd!"); 
    },
    
    getEQSelection:function () {
        var eqNode = Ext.getCmp('eqGrid').getSelectionModel().getSelection();
        return eqNode.length > 0 ? eqNode[0] : null;
    },
    
    equipmentUpd: function (button) {
        
        var record = Ext.getCmp('eqGrid').getSelectionModel().getSelection();
        var win = new NeoMes.view.factory.EquipmentUpd();
        if (record)
        {
            win.down('form').loadRecord(record[0]);
            win.show();
        }
        else
        {
            alert("Необходимо выбрать елемент!");
        }
    },
    equipmentUpdSave: function(button) {
        var win = button.up('window'),
                form = win.down('form'),
                record = form.getRecord(),
                values = form.getValues();
        record.set(values);
        Ext.getCmp('eqGrid').getStore().sync();
        win.close();                                  
    },
        
    equipmentDel: function(button) {
        alert("equipmentDel!"); 
    },
    equipmentAttrAdd: function(button) {
       alert("equipmentAttrAdd!"); 
    },
    equipmentAttrDel: function(button) {
        alert("equipmentAttrDel!"); 
    },
    equipmentAttrSave: function(button) {
        //alert("equipmentAttrSave!"); 
       // Ext.getCmp('eqAttrGrid').getStore().sync();
       var record = Ext.getCmp('eqGrid').getSelectionModel().getSelection();
       Ext.getCmp('eqGrid').getStore().getById(record[0].id).attrs().sync();
       //Ext.getCmp('eqGrid').getStore().getById(record[0].id).setStore(Ext.getCmp('eqAttrGrid').getStore());
       //Ext.getCmp('eqGrid').getStore().sync();
    }    
});