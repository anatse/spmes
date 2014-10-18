Ext.define('NeoMes.store.Users', {
    extend: 'Ext.data.Store',
    model: 'NeoMes.model.User',
    autoLoad: true,

    proxy:{
        type: 'rest',
        url: 'service/user/list',
        headers: NeoMes.utils.CommonUtils.buildCSRFHeader(),
        reader:{
            rootProperty: 'userList'
        }
    }
});