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
package org.mesol.spmes.model.gr;

import java.util.Collection;
import java.util.OptionalLong;
import org.mesol.spmes.model.gr.exceptions.ManySequentalOperationException;
import org.mesol.spmes.model.gr.exceptions.NoRuleException;
import org.mesol.spmes.model.gr.exceptions.NonParallelOperationException;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
public class RoutingUtils 
{
    public static void checkPerformingType (Vertex rs, Edge oper) throws ManySequentalOperationException, 
                                                                            NonParallelOperationException, 
                                                                            NoRuleException 
    {
        Edge firstOper = rs.getOutEdges().iterator().next();

        // It is necessary to check type of the first operation.
        switch (firstOper.getPerformType()) {
            case SEQUENTIAL:
                throw (new ManySequentalOperationException("Trying to add more than one sequental operation. Please use other operation type such as Parallel or Rule based"));

            case PARALLEL:
                // Check if all operatio types are parallel
                if (rs.getOutEdges().stream()
                    .filter(op -> ((RouterOperation)op).getPerformType() != PerformType.PARALLEL)
                    .findAny()
                    .isPresent()) {
                    throw (new NonParallelOperationException("One of operation parallel other is not."));
                }

                if (oper.getPerformType() != PerformType.PARALLEL)
                    throw (new NonParallelOperationException("Trying to add non parallel operation with parallel one."));

                break;

            case RULE_BASED:
                // Check if router step has rule
                if (rs.getRule() == null || rs.getRule().isEmpty())
                    throw (new NoRuleException("Trying to add rule based oepration to rouser step with no rule defined."));

                break;

            case ALTERNATIVE:
                oper.setPerformType(PerformType.ALTERNATIVE);
                break;
        }
    }

    public static long computeStart (Collection<Edge> edges) {
        OptionalLong startTime = edges.stream().mapToLong(e -> e.getStartTime() + e.getDuration().getDuration()).max();
        return startTime.isPresent() ? startTime.getAsLong() : 0;
    }
}
