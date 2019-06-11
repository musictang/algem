/*
 * @(#)TestRoomFileEditor.java 2.6.a 08/10/12
 * 
 * Copyright (c) 1999-2011 Musiques Tangentes. All Rights Reserved.
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

import java.util.Collections;
import java.util.List;
import java.util.Vector;
import junit.framework.TestCase;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class TestRoomFileEditor
        extends TestCase
{

  public TestRoomFileEditor(String testName) {
    super(testName);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  /**
   * Test d'égalité entre 2 vecteurs. Deux vecteurs sont égaux s'ils contiennent
   * les mêmes éléments dans le même ordre.
   */
  public void testEquality() {
    Equipment e1 = new Equipment("Ampli Fender", 1, 1);
    Equipment e2 = new Equipment("Ampli Marshall", 1, 1);
    Equipment e3 = new Equipment("Piano Rhodes", 1, 1);

    Vector<Equipment> v1 = new Vector<Equipment>();
    Vector<Equipment> v2 = new Vector<Equipment>();
    Vector<Equipment> v3 = new Vector<Equipment>();
    assertEquals(v1, v2);

    v1.add(e1);
    v1.add(e2);
    v1.add(e3);

    Equipment e4 = new Equipment();
    e4.setLabel(e1.getLabel());
    e4.setQuantity(e1.getQuantity());
    e4.setRoom(e1.getRoom());

    Equipment e5 = new Equipment("Ampli Marshall 800", 1, 1);
    Equipment e6 = new Equipment("Piano Rhodes", 2, 1);

    v2.add(e4);
    v2.add(e5);
    v2.add(e6);
    v3.add(e1);
    v3.add(e2);
    v3.add(e6);

    assertFalse("vecteur v1 == v2 ?", v1.equals(v2));
    assertFalse("vecteur v1 == v3 ?", v1.equals(v3));

    e6.setQuantity(1);
    assertEquals("vecteur v1 != v3 ?", v1, v3);
    e6.setRoom(2);
    assertFalse("vecteur v1 == v3 ?", v1.equals(v3));
  }

  public void testUnmodifiableCollection() {
    Equipment e1 = new Equipment("Ampli Fender", 1, 1);
    Equipment e2 = new Equipment("Ampli Marshall", 1, 1);
    Equipment e3 = new Equipment("Piano Rhodes", 1, 1);

    Vector<Equipment> v1 = new Vector<Equipment>();
    v1.add(e1);
    v1.add(e2);
    v1.add(e3);
    List<Equipment> v2 = Collections.unmodifiableList(v1);
    e1.setQuantity(2);
    try {
      v2.add(new Equipment());
      fail("ajout impossible");
    } catch (java.lang.UnsupportedOperationException e) {
    }

  }
}
