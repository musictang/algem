/*
 * @(#)GemModuleSID.java	2.9.1 27/11/14
 * 
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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
package net.algem.util.module;

/**
 * Serialization object for GemModule.
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.1
 */
public class GemModuleSID
        implements java.io.Serializable
{

  private String moduleClass;
  private String moduleSID;
  private String moduleString;
  private Object[] state;

  public GemModuleSID(String _class, String _id, String _string) {
    moduleClass = _class;
    moduleSID = _id;
    moduleString = _string;
  }

  public String getModuleClass() {
    return moduleClass;
  }

  public String getSID() {
    return moduleSID;
  }

  public String getModuleString() {
    return moduleString;
  }

  public Object[] getState() {
    return state;
  }

  public void setState(Object... state) {
    this.state = state;
  }

  @Override
  public String toString() {
    return moduleClass + ":" + moduleSID + ":" + moduleString;
  }
}
