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
package org.mesol.spmes.planning;

import org.junit.Test;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.impl.solver.DefaultSolver;

/**
 *
 * @author gosha
 */
public class TPlanningUnit {
     @Test
     public void hello() {
//         SolverFactory factory 
         Solver solver = new DefaultSolver();
                 
     }
}

// TestNG class
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = { AppConfig.class, UnitTestDatabaseConfig.class })
//@ActiveProfiles("unit-test")
//public class PersonDaoTest {
// 
//  @Autowired
//  private PersonDao personDao;
// 
//  @Test
//  public void testGetPerson() {
//    Person p = personDao.getPerson("Joe");
//  }
//}