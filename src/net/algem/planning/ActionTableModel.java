
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.algem.planning;

import net.algem.room.Room;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.p
 * @since 2.7.p 18/03/2015
 */
public class ActionTableModel {
  
  
  private String day;
  private Hour start;
  private Hour end;
  private Room room;

  public ActionTableModel() {
  }

  public String getDay() {
    return day;
  }

  public void setDay(String day) {
    this.day = day;
  }

  public Hour getStart() {
    return start;
  }

  public void setStart(Hour start) {
    this.start = start;
  }

  public Hour getEnd() {
    return end;
  }

  public void setEnd(Hour end) {
    this.end = end;
  }

  public Room getRoom() {
    return room;
  }

  public void setRoom(Room room) {
    this.room = room;
  }
  
  

}
