
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.algem.planning;

import java.awt.BorderLayout;
import net.algem.util.DataCache;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.GemPanel;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.p
 * @since 2.7.p 18/03/2015
 */
public class AdministrativeScheduleCtrl
  extends GemPanel
{

  protected DataCache dataCache;
  protected GemDesktop desktop;
  protected DateRangePanel datePanel;
  
  public AdministrativeScheduleCtrl(GemDesktop desktop) {
    this.desktop = desktop;
    this.dataCache = desktop.getDataCache();
    datePanel = new DateRangePanel(dataCache.getStartOfYear(), dataCache.getEndOfYear());
    AdministrativeTableView tableView = new AdministrativeTableView(dataCache);
    tableView.load();
    setLayout(new BorderLayout());
    add(datePanel, BorderLayout.NORTH);
    add(tableView, BorderLayout.CENTER);
  }
  

}
