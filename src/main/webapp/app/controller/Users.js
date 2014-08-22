Ext.define("NeoMes.controller.Users", {
    extend: 'Ext.app.ViewController',
    stores: ['Users'],
    models: ['User'],
    views: ['user.List', 'user.Edit'],
    alias: 'users',

    refs: [{
        ref: 'contentPanel',
        selector: 'contentPanel'
    }],

    init: function() {
        this.control ({
           'users': {
                itemdblclick: this.editUser
            },
            'users button[action=sync]': {
                click: this.syncUser
            },
            'useredit button[action=save]': {
                click: this.updateUser
            }
        });
    },
    editUser: function(grid, record) {
        console.log('{NeoMes.controller.Users} Double clicked on ' + record.get('name'));

        var view = Ext.widget('useredit');
        view.down('form').loadRecord(record);

    },
    updateUser: function(button) {
        console.log('{NeoMes.controller.Users} clicked the SAVE button');

        var win = button.up('window'),
                form = win.down('form'),
                record = form.getRecord(),
                values = form.getValues();

        record.set(values);
        win.close();

    },
    syncUser: function(button) {
        console.log('{NeoMes.controller.Users} clicked the SYNC button');
        this.getUsersStore().sync();
    }
});