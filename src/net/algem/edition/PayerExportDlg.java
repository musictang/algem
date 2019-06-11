/*
 * @(#)PayerExportDlg.java	2.15.0 26/07/2017
 *
 * Copyright (c) 1999-2017 Musiques Tangentes. All Rights Reserved.
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
 */
package net.algem.edition;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.util.Date;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import net.algem.accounting.OrderLineIO;
import net.algem.config.ModeOfPaymentCtrl;
import net.algem.config.ParamTableIO;
import net.algem.planning.DateFr;
import net.algem.planning.DateRangePanel;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 * Export mailling payers.
 * GemPanel entry for selecting payers (paid/not payed) in period
 * with a specific mode of payment or not.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.0
 * @since 1.0a 14/12/1999
 */
public class PayerExportDlg
        extends ExportDlg
{

  private static final String PAYER_TITLE = BundleUtil.getLabel("Export.payer.title");
  private static Object[] criteria = {
    MessageUtil.getMessage("export.criterium.payer.uncollected"),
    MessageUtil.getMessage("export.criterium.payer.collected"),
    MessageUtil.getMessage("export.criterium.payer.all")
  };
  private GemPanel pCriterion;
  private JComboBox cbCriterion;
  private DateRangePanel dateRange;
  private JComboBox payment;

  private static String all_payment = MessageUtil.getMessage("all.payment.label");

  public PayerExportDlg(GemDesktop desktop) {
    super(desktop, PAYER_TITLE);
  }

  public PayerExportDlg(Dialog _parent, DataCache _cache) {
    super(_parent, PAYER_TITLE);
  }

  @Override
  public GemPanel getCriterion() {

    pCriterion = new GemPanel(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(pCriterion);

    cbCriterion = new JComboBox(criteria);
    payment = new JComboBox(ParamTableIO.getValues(ModeOfPaymentCtrl.TABLE, ModeOfPaymentCtrl.COLUMN_NAME, dc));
    payment.addItem(all_payment);

    initDateRange();
    gb.add(new JLabel(BundleUtil.getLabel("Type.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(cbCriterion, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Mode.of.payment.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(payment, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Date.From.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(dateRange, 1, 2, 1, 1, GridBagHelper.WEST);

    cbCriterion.setPreferredSize(new Dimension(dateRange.getPreferredSize().width, cbCriterion.getPreferredSize().height));
    payment.setPreferredSize(cbCriterion.getPreferredSize());

    return pCriterion;
  }

  @Override
  public String getRequest() {
    String query = null;
    String r = (String)payment.getSelectedItem();
    switch (cbCriterion.getSelectedIndex()) {
      case 0 : // Payeurs non encaissés
        //query = "where id in (SELECT distinct p.id from personne p,adresse a,echeancier2 e where e.payeur=p.id and p.id=a.idper and p.arch='f' and e.reglement='CHQ' and e.paye = 'f' and e.echeance <= '" + d2 + "')";
        query = "WHERE p.id IN (SELECT DISTINCT payeur FROM " + OrderLineIO.TABLE + " WHERE paye = 'f' AND";
        break;
      case 1: // Les payeurs encaissés
        //query = "where id in (SELECT distinct p.id from personne p,adresse a,echeancier2 e where e.payeur=p.id and p.id=a.idper and p.arch='f' and e.echeance >= '" + dc.getDebutPeriode() + "')";
        query = "WHERE p.id IN (SELECT DISTINCT payeur FROM " + OrderLineIO.TABLE + " WHERE paye = 't' AND";
        break;
      case 2 : // Tous les payeurs
        //query = "where id in (SELECT distinct p.id from personne p,adresse a,echeancier2 e where e.payeur=p.id and p.id=a.idper and p.arch='f')";
        query = "WHERE p.id IN (SELECT DISTINCT payeur FROM " + OrderLineIO.TABLE + " WHERE";
        break;
    }
    if (!all_payment.equals(r)) {
      query += " reglement='"+r+"' AND";
    }
    query += " echeance BETWEEN '"+dateRange.getStartFr().toString()+"' AND '"+dateRange.getEndFr().toString()+"')";
    //System.out.println(query);
    return query;

  }

  /**
   * Inits the period.
   * By default, the period corresponds to current month.
   */
  private void initDateRange() {
    DateFr b = new DateFr(new Date());
    b.setDay(1);
    DateFr e = new DateFr(b);
    e.incMonth(1);
    e.decDay(1);
    dateRange = new DateRangePanel(b,e);
  }

  @Override
  protected String getFileName() {
    return BundleUtil.getLabel("Export.payer.file");
  }

}
