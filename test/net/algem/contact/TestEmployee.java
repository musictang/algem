/*
 * @(#)TestEmployee.java 2.8.m 03/09/13
 * 
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
 *
 * This file is part of Algem.
 * Algem is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Algem is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Algem. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package net.algem.contact;

import net.algem.TestProperties;
import net.algem.util.DataConnection;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.m
 * @since 2.8.m 03/09/13
 */
public class TestEmployee
{

  private EmployeeService service;
  private DataConnection dc;

  public TestEmployee() {
  }

  @BeforeClass
  public static void setUpClass() throws Exception {
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
  }

  @Before
  public void setUp() {
    dc = TestProperties.getDataConnection();
    service = new BasicEmployeeService(dc);
  }

  @After
  public void tearDown() {
  }

  @Test
  public void testInsee() {
    // numero corse
    StringBuilder nir = new StringBuilder("170062A004024");
    nir.replace(5, 7, "19");

    int key = ((BasicEmployeeService) service).getKey(nir.toString());
    assertTrue(key == 33);
//    System.out.println(key);
    
    nir = new StringBuilder("170062B096024");
    key = ((BasicEmployeeService) service).getKey(nir.toString());
    assertTrue(key == 16);
//    System.out.println(key);

    String insee = "160109935201623";
    assertTrue(service.checkNir(insee));
    insee = "160109935201613";
    assertFalse(service.checkNir(insee));
    insee = "170109935201623";
    assertFalse(service.checkNir(insee));
    insee = "160129935201623";
    assertFalse(service.checkNir(insee));
    insee = "160109835201623";
    assertFalse(service.checkNir(insee));
    insee = "160109935001623";
    assertFalse(service.checkNir(insee));
    insee = "160109935202623";
    assertFalse(service.checkNir(insee));
  }
}