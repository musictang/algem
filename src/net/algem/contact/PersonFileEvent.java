/*
 * @(#)PersonFileEvent.java     2.6.a 18/09/12
 *
 * Copyright (c) 2009 Musiques Tangentes All Rights Reserved.
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

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class PersonFileEvent
        extends java.util.EventObject
{

  /** Identifies one or more changes in file. */
  public static final int CONTENTS_CHANGED = 0;
  
  public static final int CONTACT_CHANGED = 1;
  public static final int MEMBER_ADDED = 2;
  public static final int MEMBER_CHANGED = 3;
  public static final int MEMBER_REMOVED = 4;
  public static final int TEACHER_ADDED = 5;
  public static final int TEACHER_CHANGED = 6;
  public static final int TEACHER_REMOVED = 7;
  public static final int BANK_ADDED = 8;
  public static final int BANK_CHANGED = 9;
  public static final int BANK_REMOVED = 10;
  public static final int SUBSCRIPTION_CARD_CHANGED = 20;
  private int type;

  /**
   * Constructs a PersonFileEvent object.
   *
   * @param source the source Object (typically
   * <code>this</code>)
   * @param type an int specifying {@link #CONTACT_CHANGED},
   *                {@link #MEMBER_ADDED}, and more ..}
   */
  public PersonFileEvent(Object source, int type) {
    super(source);
    this.type = type;
  }

  /**
   * Returns the event type. The possible values are: <ul> <li> {@link #CONTENTS_CHANGED}
   * <li> {@link #CONTACT_CHANGED} <li> {@link #MEMBER_ADDED} <li> {@link #MEMBER_CHANGED}
   * <li> {@link #MEMBER_REMOVED} <li> {@link #TEACHER_ADDED} <li> {@link #TEACHER_CHANGED}
   * <li> {@link #TEACHER_REMOVED} <li> {@link #BANK_ADDED} <li> {@link #BANK_CHANGED}
   * <li> {@link #BANK_REMOVED} </ul>
   *
   * @return an int representing the type value
   */
  public int getType() {
    return type;
  }

  @Override
  public String toString() {
    return "PersonFileEvent type:" + type + " src:" + source;
  }
}
