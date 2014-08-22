/* 
 * Copyright 2014 Mes Solutions.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
Ext.define('NeoMes.store.Menu', {
    extend: 'Ext.data.TreeStore',
    autoLoad: true,
    model: 'NeoMes.model.Menu',
    root: {
        id: -1,
        name: 'root'
    },
    proxy: {
        type: 'ajax',
        headers: NeoMes.utils.CommonUtils.buildCSRFHeader (),
        actionMethods: {
            read: 'POST'
        },
        api: {
            read: 'service/menu.json'
        },
        reader: {
            type: 'json',
            rootProperty: 'menuItemList'
        }
    }
});

