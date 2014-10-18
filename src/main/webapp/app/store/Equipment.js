
Ext.define('NeoMes.store.Equipment', {
    extend: 'Ext.data.TreeStore',
    model: 'NeoMes.model.Equipment',
    //autoLoad: true,
    autoSync: true,
    root: {
        id: -1,
        name: 'root'
    },
    nodeParam: 'eqId',
    proxy: {
        type: 'rest',
        headers: NeoMes.utils.CommonUtils.buildCSRFHeader (),
//        actionMethods: {
//            read: 'POST'
//        },        
        api: {
            read: 'equipment/list.json'
        },        
        reader: {
            type: 'json',
            rootProperty: 'equipmentList'
        }
    }
});

