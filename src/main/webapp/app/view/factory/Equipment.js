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
Ext.define('NeoMes.view.factory.Equipment', {
    extend: 'Ext.panel.Panel',
    requires: [
        'Ext.draw.*'
    ],
    layout: 'fit',
    alias: 'widget.eqipment-chart',
    title: 'Organization chart',
    initComponent: function() {
        var drawComponent = Ext.create('Ext.draw.Component', {
            viewBox: false,
            items: [{
                    type: 'text', // ������ ������ �����
                    text: '��� �������',
                    fill: 'dark-gray', // ���� ������
                    font: '14px serif', // ��������� ������
                    x: 80,
                    y: 10
                }, {
                    type: 'rect', //������ �������������
                    width: 50,
                    height: 50,
                    fill: 'blue',
                    x: 10,
                    y: 20
                }, {
                    type: 'circle', // ������ ����
                    radius: 25,
                    fill: '#eee',
                    x: 100,
                    y: 45,
                    stroke: 'red', // ������� �������-�������
                    'stroke-width': 1 //������� �������
                }, {
                    type: 'ellipse', // ������ ������
                    radiusX: 30,
                    radiusY: 20,
                    fill: '#eee',
                    opacity: 0.5,
                    x: 140,
                    y: 45,
                    stroke: 'red',
                    'stroke-width': 1
                }, {
                    type: 'image', // ������-�����������
                    src: 'Penguins.jpg', //���� � ����������� - ����� � ������ ���-��������
                    width: 150,
                    height: 100,
                    x: 200,
                    y: 15
                }, {
                    type: 'path', // ������ ����
                    path: 'M 400 20 L 350 150 L 450 150', // ���������� ����� ����
                    fill: '#ccc'
                }]
        });

        this.width = 800;
        this.height = 600;
        this.items = [drawComponent];
        this.callParent();
    }
});

