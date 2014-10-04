Ext.define('NeoMes.store.Users', {
    extend: 'Ext.data.Store',
    model: 'NeoMes.model.User',
    autoLoad: true,

    proxy:{
        type: 'ajax',
        api:{
            read: 'service/user/list.json',
            update: 'service/user/update.json'
        },
        reader:{
            type: 'json',
            rootProperty: 'userList'
        }
    }
});