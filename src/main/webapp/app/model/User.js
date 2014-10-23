Ext.define('NeoMes.model.User', {
    extend: 'Ext.data.Model',
    fields: ['username', 'email', 'locked', 'password',
    {
        name: 'id',
        type: 'number'
    }]
});