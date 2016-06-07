/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.algem.plugins;

import javax.swing.ProgressMonitor;
import net.algem.edition.HourEmployeeDlg;
import net.algem.edition.HoursTaskExecutor;
import net.algem.edition.TransferDlg;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version Expression projectVersion is undefined on line 13, column 15 in Templates/Classes/Class.java.
 * @since Expression projectVersion is undefined on line 14, column 13 in Templates/Classes/Class.java. 03/06/2016
 */
public class WorkingTimePlugin
  extends HoursTaskExecutor

{

  public static String INFO = "Heures jazz à tours";

  public WorkingTimePlugin() {
  }

  @Override
  public String getInfo() {
    return "Heures jazz à tours";
  }

  @Override
  public void execute() {

  }

}
