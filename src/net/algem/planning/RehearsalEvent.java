
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.algem.planning;

import net.algem.util.event.GemEvent;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.p
 * @since 2.7.p 08/10/2014
 */
public class RehearsalEvent 
  extends GemEvent
{

  public RehearsalEvent(Object src, int operation, int type) {
    super(src, operation, type);
  }

}
