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
package org.mesol.spmes.repo;

import java.util.List;
import org.mesol.spmes.model.graph.OperEdge;
import org.mesol.spmes.model.graph.Router;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * 
 * @version 1.0.0
 * @author ASementsov
 */
public interface RoutingRepo extends CrudRepository<Router, Long>
{
    @Query (nativeQuery = true, value = "select level, id, name, from_id, to_id, weight " +
                                        "from operedge op " +
                                        "start with id = (select oper_id from router where id = ?1) " +
                                        "connect by prior to_id = from_id " +
                                        "order by level")
    List<OperEdge> findAllOpers (Long routerId);
}
