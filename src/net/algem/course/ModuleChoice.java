/*
 * @(#)ModuleChoice.java	2.6.a 17/09/12
 * 
 * Copyright (c) 1999-2012 Musiques Tangentes. All Rights Reserved.
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
package net.algem.course;

import net.algem.util.model.GemList;
import net.algem.util.ui.GemChoice;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class ModuleChoice
        extends GemChoice {

  public ModuleChoice(ModuleChoiceModel model, boolean none) {
    super(model);

    if (none) {
      Module s = new Module("aucun");
      insertItemAt(s, 0);
    }
    if (model.getSize() > 0) {
      setSelectedIndex(0);
    }
  }

  public ModuleChoice(GemList modules) {
    this(new ModuleChoiceModel(modules), false);
  }

  /**
   * Retourne l'id du module correspondant au choix dans la comboBox.
   *
   * @return un entier
   */
  @Override
  public int getKey() {
    return ((Module) getSelectedItem()).getId();
  }

  /**
   * Retourne le numéro d'index du module sélectionné dans la comboBox.
   *
   * @return un entier
   */
  public int getSelectedKey() {
    return getSelectedIndex();// l'index et pas l'id du forfait
  }

  @Override
  public void setKey(int k) {
    setSelectedItem(((ModuleChoiceModel) getModel()).getModule(k));
  }
}
