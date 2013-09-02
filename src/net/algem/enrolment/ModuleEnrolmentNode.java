/*
 * @(#)ModuleEnrolmentNode.java 2.8.l 30/08/13
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
package net.algem.enrolment;

import net.algem.util.BundleUtil;

/**
 * Tree node for module info.
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.l
 */
public class ModuleEnrolmentNode
        extends EnrolmentNode
{

  private ModuleOrder mo;

  public ModuleEnrolmentNode(Object o) {
    super(o);

    if (o instanceof ModuleOrder) {
      mo = (ModuleOrder) o;
    }
  }

  public ModuleOrder getModule() {
    return mo;
  }

  @Override
  public String toString() {
    return BundleUtil.getLabel("Module.label") + " : " + mo.getTitle();
  }

  @Override
  public boolean isLeaf() {
    return false;
  }
}
