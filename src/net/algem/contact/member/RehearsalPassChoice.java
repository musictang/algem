/*
 * @(#)RehearsalPassChoice.java 2.9.2 12/01/15
 * 
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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
package net.algem.contact.member;

import java.util.Vector;
import net.algem.util.ui.GemChoice;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.2
 */
public class RehearsalPassChoice
        extends GemChoice
{

  public RehearsalPassChoice(Vector<RehearsalPass> cartes) {
    super(cartes);
  }

  @Override
  public int getKey() {
    return ((RehearsalPass) getSelectedItem()).getId();
  }

  @Override
  public void setKey(int k) {
    setSelectedItem(new RehearsalPass(k));
  }
}
