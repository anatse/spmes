Ext.define ('NeoMes.utils.CommonUtils', {
    requires: [
        'Ext.*',
        'Ext.LoadMask'
    ],
    
    singleton: true,
    
    getLocaleString: function (group, value) {
        if (GLocale && GLocale[group]) {
            if (value)
                return GLocale[group][value];
            else
                return GLocale[group];
        }
        else
            return group + value;
    },
    
    /**
     * Function refreshes any grid and after restores current selection based
     * on value of the key field 
     * @param grid - grid to be refreshed
     * @param params - additional parameters which use to reload store
     * @param idFieldName - key field name
     * @param selectedItemObj
     * @param callbackFunc
     * @param scope
     * @param disableSelect
     */
    refreshAnyGrid: function (grid, params, idFieldName, selectedItemObj, callbackFunc, scope, disableSelect) {
        // get current selection
        var selectedItem = selectedItemObj;
        if (!selectedItem)
            selectedItem = grid.getSelectionModel().getLastSelected();

        grid.getSelectionModel().deselectAll();
        grid.getStore().sorters.clear();

        grid.getStore().load({ 
            params: params,
            async: false,
            callback: function (records, operation, success) {
                if (disableSelect !== true) {
                    if (selectedItem && idFieldName) {
                        // restore previously selected item
                        var record = grid.getStore().findRecord (idFieldName, selectedItem.get(idFieldName));
                        if (record)
                            grid.getSelectionModel().select (record);
                        else {
                            if (!isNaN(selectedItem.index) && records && records.length >= selectedItem.index) 
                                grid.getSelectionModel().select (records[selectedItem.index]);
                            else if (records && records.length > 0)
                                    grid.getSelectionModel().select (records[0]);
                        }
                    }
                    else {
                        // select first record
                        if (records && records.length > 0)
                            grid.getSelectionModel().select (records[0]);
                    }
                }

                if (callbackFunc)
                    callbackFunc.call (scope ? scope : this, records, operation, success);
            },
            exception: function (proxy, response, operation) {
                if (operation) {
                    Ext.Msg.alert('Error', operation.error);
                }
                else {
                    Ext.Msg.alert ('Error', 'Proxy error');
                }
            }
        });
    },

    selectFirstElement: function( grid ){
        try{
            grid.getSelectionModel().select (0);
        }
        catch (e){};
    },

    statusRenderer: function (value) {
        return value;
    },

    buildCSRFHeader: function () {
        var metaValue = Ext.DomQuery.select("meta[@name='_csrf']");
        if (metaValue.length > 0) {
            metaValue = metaValue[0].content;
        }
        
        var metaName = Ext.DomQuery.select("meta[@name='_csrf_header']");
        if (metaName.length > 0) {
            metaName = metaName[0].content;
        }
        
        var ret = {};
        if (metaName && metaValue) {
            ret[metaName] = metaValue;
        }
        return ret;
    },
            
    postJsonRequest: function (opts) {
        var waitMsg = opts.waitMsg ? opts.waitMsg : 'Waiting';
        var scope = opts.scope ? opts.scope : this;
        var wnd = Ext.WindowMgr.getActive();
        if (wnd) {
            var myMask = new Ext.LoadMask(wnd, {msg: waitMsg});
            myMask.show(); 
        }
        
        var header = NeoMes.utils.CommonUtils.buildCSRFHeader ();
        // Only for Security CSRF
        Ext.Ajax.request ({
            url: opts.url,
            jsonData: opts.jsonData,
            method: 'POST',
            scope: scope,
            // for Spring CSRF security
            headers: header,
            success: function(response) {
                try {
                    var result = Ext.decode(response.responseText);
                    opts.success.call (this, result);
                }
                finally {
                    if (myMask)
                        myMask.hide();
                }
            },
            failure: function (o) {
                Ext.MessageBox.alert('Result', o);
                if (myMask)
                    myMask.hide();
            }
        });
    },

    showEditDialog: function (dialog, record, isEdit, callback, scope) {
        var dlg = new dialog ();
        var win = dlg.showDialog (record, isEdit);
        win.on ('destroy', callback, scope);
    },

    createColumnFromModel: function (res, model, defaultWidth, maxCols, wordWrap) {
        if (!defaultWidth)
            defaultWidth = 150;

        var columns = [];
        if (model.model)
            model = model.model;

        Ext.each (model.getFields(), function(field) {
            if (maxCols && columns.length === maxCols)
                return true;

            var column = {
                text: res.gridFields[field.name],
                width: defaultWidth,
                dataIndex: field.name,
                stateId: field.name
            };

            if (field.type.type === 'date') {
                column.renderer = Ext.util.Format.dateRenderer('d.m.Y');
            }
            else if (wordWrap) {
                column.renderer = function (val) {
                    return '<div style="white-space:normal !important;">'+ val +'</div>';
                };
            }

            columns.push (column);
        });

        return columns;
    }
});