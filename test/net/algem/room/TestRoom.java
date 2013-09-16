/*
 * @(#)TestRoom.java	2.8.m 11/09/13
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
package net.algem.room;

import java.util.Vector;
import net.algem.contact.Person;
import static org.junit.Assert.*;
import org.junit.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.m
 */
public class TestRoom
{
  
  public TestRoom() {
  }

  @BeforeClass
  public static void setUpClass() throws Exception {
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
  }
  
  @Before
  public void setUp() {
  }
  
  @After
  public void tearDown() {
  }
  
  @Test
  public void testRoomAttributes() {
    Room r = new Room("RATTRAPAGE");
    assertTrue(r.isCatchingUp());
    r.setName("Rattrapage MK");
    assertTrue(r.isCatchingUp());
    r.setName("RATTRAPAGE MK");
    assertTrue(r.isCatchingUp());
    r.setName(" RATTRAPAGE ");
    assertTrue(r.isCatchingUp());
    r.setName("catching");
    assertTrue(r.isCatchingUp());
    r.setName("catch");
    assertFalse(r.isCatchingUp());
    r.setName("TRAP");
    assertFalse(r.isCatchingUp());
    r.setName("RATROUGE");
    assertFalse(r.isCatchingUp());
  }
  
  @Test
  public void testEquipment() {
    Equipment e1 = new Equipment(1, "Ampli 1");
    Equipment e2 = new Equipment(1, "Piano 1");
    Equipment e3 = new Equipment(1, "Ampli 1");
    Equipment e4 = new Equipment(1, "Piano 1");
    Vector<Equipment> v1 = new Vector<Equipment>();
    Vector<Equipment> v2 = new Vector<Equipment>();
    
    v1.add(e1);
    v1.add(e2);
    v2.add(e3);
    v2.add(e4);
    assertEquals(v1, v2);
    v2.clear();
    assertFalse(v1.equals(v2));
    e3.setLabel("Ampli 2");
    v2.add(e3);
    v2.add(e4);
    assertFalse(v1.equals(v2));
    e3.setLabel("Ampli 1");
    assertEquals(v1, v2);
    assertTrue(v1.equals(v2));
    v2.add(new Equipment(2,"toms"));
    assertFalse(v1.equals(v2));
    
    Room r1 = new Room();
    r1.setEquipment(v1);
    r1.setPayer(new Person(1234));
    Room r2 = new Room();
    r2.setEquipment(v2);
    r2 = r1;
    assertEquals(r1, r2);
    assertTrue(r1.getPayer().getId() == r2.getPayer().getId() && r2.getPayer().getId() == 1234);
    assertTrue(r1.getEquipment().equals(r2.getEquipment()));
    
    r1.setEquipment(v1);
    System.out.println(r1.getEquipment());
    System.out.println(r2.getEquipment());
    assertTrue(r1.getEquipment().equals(r2.getEquipment()));
    
    Room r3 = new Room();
    r3.setEquipment(v1);
    Vector<Equipment> v3 = new Vector<Equipment>(v1);
    v3.addElement(new Equipment(1,"Trompette"));
    r3.setEquipment(v3);
    assertFalse(r3.getEquipment().equals(r1.getEquipment()));
    System.out.println(r3.getEquipment());
    
  }
}
