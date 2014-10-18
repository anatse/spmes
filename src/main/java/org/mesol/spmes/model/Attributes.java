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

package org.mesol.spmes.model;

import javax.persistence.Column;

/**
 *
 * @author SGemba
 */
public class Attributes {

    @Column(nullable = false, length = 32)
    private String      attrCode;
    @Column(length = 320)
    private String      attrName;
    @Column(length = 32)
    private String      attrType;    
    @Column(name = "UNIT_CODE")
    private String      unitCode;
    
    public String getAttrCode() {
        return attrCode;
    }

    public String getAttrName() {
        return attrName;
    }

    public String getAttrType() {
        return attrType;
    }

    public String getUnitCode() {
        return unitCode;
    }    

    public void setAttrCode(String attrCode) {
        this.attrCode = attrCode;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public void setAttrType(String attrType) {
        this.attrType = attrType;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }
    
}
