Ext.define('NeoMes.view.Main', {
    extend:'Ext.container.Viewport',
    layout:'border',

    requires:[
        'Ext.layout.container.Border',
        'Ext.panel.Panel',
        'NeoMes.view.Navigation',
        'NeoMes.view.ContentPanel',
        'NeoMes.utils.CommonUtils',
        'NeoMes.view.user.List',
        'NeoMes.view.factory.Equipment'
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
    
        this.callParent();
    }
});