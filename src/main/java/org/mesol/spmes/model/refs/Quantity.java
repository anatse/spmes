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
package org.mesol.spmes.model.refs;

import java.io.Serializable;
import java.util.function.BiFunction;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import org.springframework.util.Assert;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Embeddable
public class Quantity implements Serializable 
{
    @Column(name = "QTY_UNIT_CODE", length = 32)
    private String      unitCode;
    @Column(name = "QTY")
    private Double  quantity;

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }
    
    public Quantity minus (Quantity qty, BiFunction<String, String, UnitConverter> conv) {
        final Quantity q = new Quantity();
        q.setUnitCode(unitCode);

        // Check units
        if (unitCode.equals(qty.unitCode)) {
            Assert.isTrue(quantity >= qty.quantity, "Cannot substruct more quantity than exist");
            q.setQuantity(quantity - qty.quantity);
        }
        else {
            // Try to convert unit codes
            Assert.notNull(conv, "Converter function should be set");
            UnitConverter uc = conv.apply(qty.unitCode, unitCode);
            Assert.notNull(uc, String.format ("Converter between %s and %s not found", qty.unitCode, unitCode));
            double tQty = uc.convert(qty.quantity);
            q.setQuantity(quantity - tQty);
        }

        return q;
    }
}
