
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.algem.rental;

import net.algem.course.*;

/**
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @version 2.17.1
 * @since 2.17.1 29/08/2019
 */
class RentException 
  extends Exception 
{

  public RentException(String message) {
    super(message);
  }

  public RentException() {
  }
  
}
