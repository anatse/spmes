Ext.define('NeoMes.view.Main', {
    extend:'Ext.container.Viewport',
    layout:'border',

    requires:[
        'Ext.layout.container.Border',
        'Ext.panel.Panel',
        'NeoMes.view.Navigation',
        'NeoMes.view.ContentPanel',
        'NeoMes.utils.CommonUtils',
        'NeoMes.view.user.List'
    ],

    initComponent:function () {
        this.items = [{
            region: 'west',
            xtype: 'navigation',
            width: 250,
            minWidth: 100,
            height: 200,
            split: true,
            stateful: true,
            stateId: 'mainnav.west',
            collapsible: true
        }, {
            region: 'center',
            xtype: 'contentPanel'
        }];
    
        var store = Ext.create ('NeoMes.store.Oper');
        store.load ();
        store.load({
            success: function(store) {
                
            }
        });

        store.insert(0, new NeoMes.store.Oper());
        store.save();
        this.callParent();
    }
});