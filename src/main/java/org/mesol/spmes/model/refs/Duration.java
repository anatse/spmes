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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import org.springframework.util.Assert;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
@Embeddable
public class Duration implements Serializable 
{
    @Column(nullable = false)
    private long                duration;
    @Enumerated(EnumType.STRING)
    @Column(name = "DURATION_TYPE", nullable = false)
    private DurationType        durationType = DurationType.PER_UNIT;
    @Column(name = "QTY_UNIT_CODE", length = 32)
    private String              unitCode = "UNIT";

    public Duration () {
    }

    public Duration (long duration) {
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public DurationType getDurationType() {
        return durationType;
    }

    public void setDurationType(DurationType durationType) {
        this.durationType = durationType;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public long computeDuration (Quantity qty, BiFunction<String, String, UnitConverter> conv) {
        // Check units
        switch (durationType) {
            case ABSOLUTE:
                return duration;
            
            case PER_UNIT:
                if (unitCode.equals(qty.getUnitCode())) {
                    return Math.round(duration * qty.getQuantity());
                }
                else {
                    Assert.notNull(conv, "Converter function should be set");
                    UnitConverter uc = conv.apply(qty.getUnitCode(), unitCode);
                    return Math.round(duration * uc.convert(qty.getQuantity()));
                }
        }

        return 0;
    }
}
