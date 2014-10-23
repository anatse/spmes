Ext.define('NeoMes.view.user.List', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.users',

    requires: [
        'NeoMes.store.Users',
        'NeoMes.view.user.ListController'
    ],
    controller: 'list',
    tbar: [{
        text: NeoMes.utils.CommonUtils.getLocaleString('button', 'add'),
        action: 'add'
    }, {
        text: NeoMes.utils.CommonUtils.getLocaleString('button', 'update'),
        action: 'update'
    }, {
        text: NeoMes.utils.CommonUtils.getLocaleString('button', 'lock'),
        action: 'lock'
    }],
    initComponent: function () {
        this.store = Ext.create('NeoMes.store.Users');
        this.columns = [{
            header: NeoMes.utils.CommonUtils.getLocaleString('fields', 'userName'), 
            dataIndex: 'username', 
            flex:1
        }, {
            header: NeoMes.utils.CommonUtils.getLocaleString('fields', 'email'), 
            dataIndex: 'email', 
            flex:1
        }];

        this.buttons = [{
            text: NeoMes.utils.CommonUtils.getLocaleString('button', 'save'),
            action: 'sync'
        }];

        this.callParent(arguments);
    }
});