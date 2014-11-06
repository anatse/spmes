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
def WC1 = eqService {findByName ('WC-1')}

eqService {
    def neq = save (
        Equipment ([
            name: 'WC-5', 
            description: 'Workcenter 5',
            attributes: [
                EquipmentAttribute ([
                    name: 'test',
                    attrValue: '10'
                ]),
                EquipmentAttribute ([
                    name: 'capability',
                    attrValue: '100',
                    attrType: 'number'
                ])
            ]
        ])
    )

    assert neq.id != null

    getAll().each {eq -> 
        System.out.println eq.name + ": " + eq.description
    }

    routeService {
        findAllRouters ().each {router -> 
            System.out.println 'router: ' + router.name + ": " + router.id
        }
    }
}
