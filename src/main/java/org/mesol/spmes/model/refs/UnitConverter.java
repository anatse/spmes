/*
 * Copyright 2014 Mes Solutions.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed toUnit in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mesol.spmes.model.refs;

import java.io.Serializable;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.mesol.spmes.model.abs.AbstractEntity;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * Class used toUnit convert fromUnit one measure unit toUnit another
 * @version 1.0.0
 * @author ASementsov
 */
@Entity
@Table (name = "UCONV")
public class UnitConverter extends AbstractEntity implements Serializable
{
    @Id
    @ManyToOne
    @JoinColumn(name = "UFROM", foreignKey = @ForeignKey(name = "FK_UCF_UNIT", value = ConstraintMode.CONSTRAINT))
    private Unit        fromUnit;
    @Id
    @ManyToOne
    @JoinColumn(name = "UTO", foreignKey = @ForeignKey(name = "FK_UCT_UNIT", value = ConstraintMode.CONSTRAINT))
    private Unit        toUnit;
    /**
     * Spring EL formula toUnit convert BigDecimal value
     */
    private String      formula;

    public Double convert (Double value) {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("qty", value);
        Expression expr = parser.parseExpression(formula);
        return expr.getValue(context, Double.class);
    }

    public Unit getFromUnit() {
        return fromUnit;
    }

    public void setFromUnit(Unit fromUnit) {
        this.fromUnit = fromUnit;
    }

    public Unit getToUnit() {
        return toUnit;
    }

    public void setToUnit(Unit toUnit) {
        this.toUnit = toUnit;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }
}
