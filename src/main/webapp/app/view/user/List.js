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
        this.columns = [
            {header: 'Name', dataIndex: 'username', flex:1},
            {header: 'Email', dataIndex: 'email', flex:1}
        ];

        this.buttons = [{
            text:'Sync',
            action:'sync'
        }];

        this.callParent(arguments);
    }
});