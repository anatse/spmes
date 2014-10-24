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
package org.mesol.spmes.model.graph;

/**
 * This enum defines type of operation. It defines count and
 * behavior of outgoing edges (operations) of router step.
 * 
 * @version 1.0.0
 * @author ASementsov
 */
public enum PerformType 
{
    /**
     * Sequential router step. Only one outgoing edge allowed
     */
    SEQUENTIAL,
    /**
     * Parallel operation, all operations marked as parallel can be started as the same time
     * And all of its should have the same endpoint(non parallel). 
     */
    PARALLEL,
    /**
    * This is sequential router, but it is possible to choose one of many outgoing edges (operations).
    * This decision is rule based. Rule should be written using Groovy language
    */
    RULE_BASED,
    /**
     * This is alternative router step. It means only one of many outgoing edges can be chosen during
     * creation of production order based on this router
     */
    ALTERNATIVE
}
