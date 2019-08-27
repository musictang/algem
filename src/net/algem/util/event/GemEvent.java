/*
 * @(#)GemEvent.java	2.8.v 17/06/14
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

package net.algem.util.event;

/**
 * Gem event object.
 * Basic event handler utility.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.v
 */
public class GemEvent extends java.util.EventObject
{
  // operation
  public static final int MODIFICATION = 1;
  public static final int CREATION = 2;
  public static final int SUPPRESSION = 3;
  public static final int SELECTION = 4;
  public static final int CANCEL = 5;
  // type
  public static final int DATE = 1;
  public static final int PLANNING = 2;
  public static final int ENROLMENT = 3;
  public static final int LOGIN = 4;
  public static final int MESSAGE = 5;
  public static final int COURSE = 5;
  public static final int ROOM = 6;
  public static final int ESTABLISHMENT = 7;
  public static final int TEACHER = 8;
  public static final int GROUP = 9;
  public static final int POSTIT = 10;
  public static final int MUSIC_STYLE = 11;
  public static final int INSTRUMENT = 12;
  public static final int MODULE = 13;
  public static final int CONTACT = 14;
  public static final int ORDER_ITEM = 15;
  public static final int INVOICE_ITEM = 16;
  public static final int INVOICE = 17;
  public static final int MEMBER = 18;
  public static final int ROOM_RATE = 19;
  public static final int AGE_RANGE = 20;
  public static final int STATUS = 21;
  public static final int LEVEL = 22;
  public static final int USER = 23;
  public static final int ACCOUNT = 24;
  public static final int COST_ACCOUNT = 25;
  public static final int VAT = 26;
  public static final int COURSE_CODE = 27;
  public static final int REHEARSAL = 28;
  public static final int RENTOBJECT = 29;
  
  protected int operation;
  protected int type;
  private Object object;

  public GemEvent(Object src) {
    super(src);
  }

  /**
   * Constructs a basic event.
   * 
   * @param src source of event
   * @param operation type of operation
   * @param type object destination type
   */
  public GemEvent(Object src, int operation, int type) {
    super(src);
    this.operation = operation;
    this.type = type;
  }

  /**
   * Constructs a basic event associated with the object {@code object}.
   * @param src source of event
   * @param operation type of operation
   * @param type object destination type
   * @param object related object
   */
  public GemEvent(Object src, int operation, int type, Object object) {
    this(src, operation, type);
    this.object = object;
  }

  /**
   * Gets the type of the current operation.
   * @return an integer representing the type of operation
   */
  public int getOperation() {
    return operation;
  }

  /**
   * Gets the type of the current destination.
   * @return an integer representing the type of destination
   */
  public int getType() {
    return type;
  }
  
  /**
   * Gets the object associated with this event.
   * @return an object instance
   */
  public Object getObject() {
    return object;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName()+":"+type+","+operation+" "+getSource();
  }
}
