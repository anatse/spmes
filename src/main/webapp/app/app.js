//Ext.Loader.setConfig({enabled:true, disableCache:true});
Ext.application({
    name:'NeoMes',
    appFolder:"app",
    autoCreateViewport:false,

    models:['Menu'],
    stores:['Menu'],
    controllers:['App'],

    init: function() {
        Ext.setGlyphFontFamily('Pictos');
        Ext.tip.QuickTipManager.init();
        Ext.state.Manager.setProvider(Ext.create('Ext.state.CookieProvider'));
    },

    launch:function () {
        this.viewport = Ext.create('NeoMes.view.Main', {
            application:this
        });
    }
});