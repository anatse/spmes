/* 
 * Copyright 2014 ASementsov.
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
Ext.define('NeoMes.view.Navigation', {
    extend: 'Ext.tree.Panel',
    xtype: 'navigation',
    title: NeoMes.utils.CommonUtils.getLocaleString('titles', 'navi'),
    rootVisible: false,
    store: 'Menu',
    lines: true,
    useArrows: true,
    idField: 'id',
    displayField: 'name',
    setRootNode: function() {
        if (this.getStore().autoLoad) {
            this.callParent(arguments);
        }
    }
});

