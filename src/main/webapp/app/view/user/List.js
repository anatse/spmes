Ext.define('NeoMes.view.user.List', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.users',

    requires: [
        'NeoMes.store.Users',
        'NeoMes.view.user.ListController'
    ],
    controller: 'list',

    initComponent: function () {
        this.store = Ext.create('NeoMes.store.Users');
//        var model = store.getModel();
//        var columns = [];
//
//        Ext.each (model.getFields(), function (field) {
//            columns.push ();
//        });

        this.columns = [
            {header: 'Name', dataIndex: 'name', flex:1},
            {header: 'Email', dataIndex: 'email', flex:1}
        ];

        this.buttons = [{
            text:'Sync',
            action:'sync'
        }];

        this.callParent(arguments);
    }
});