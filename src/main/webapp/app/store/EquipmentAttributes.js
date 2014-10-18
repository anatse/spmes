
Ext.define('NeoMes.store.EquipmentAttributes', {
    extend: 'Ext.panel.Panel',
    model: 'NeoMes.model.EquipmentAttributes',
    autoLoad: true,
    autoSync: true,
    nodeParam: 'eqId',
    proxy: {
        type: 'json',
        headers: NeoMes.utils.CommonUtils.buildCSRFHeader (),
//        actionMethods: {
//            read: 'POST'
//        },        
        api: {
            read: 'equipment/list.json'
        },        
        reader: {
            type: 'json'
        }
    }
});

