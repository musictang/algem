
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.algem.util.model;

import net.algem.planning.DateFr;
import net.algem.planning.HourRange;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.p
 * @since 2.7.p 14/04/2014
 */
public class GemDateTime {

  private DateFr date;
  private HourRange timeRange;

  public GemDateTime() {
  }

  public GemDateTime(DateFr date, HourRange timeRange) {
    this.date = date;
    this.timeRange = timeRange;
  }

  public DateFr getDate() {
    return date;
  }

  public void setDate(DateFr date) {
    this.date = date;
  }

  public HourRange getTimeRange() {
    return timeRange;
  }

  public void setTimeRange(HourRange timeRange) {
    this.timeRange = timeRange;
  }
  
  
}
