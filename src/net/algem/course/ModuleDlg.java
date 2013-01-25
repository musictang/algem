/*
 * @(#)ModuleDlg.java	2.7.a 23/11/12
 * 
 * Copyright (c) 1999-2012 Musiques Tangentes. All Rights Reserved.
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
package net.algem.course;

import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import net.algem.accounting.ModeOfPayment;
import net.algem.contact.PersonFile;
import net.algem.enrolment.EnrolmentService;
import net.algem.planning.DateFr;
import net.algem.planning.DateFrField;
import net.algem.planning.Periodicity;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.model.Model;
import net.algem.util.ui.*;

/**
 * Selection dialog for modules during enrolment.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 * @since 1.0a 07/07/1999
 */
public class ModuleDlg
        extends PopupDlg
        implements ItemListener
{

  private GemBorderPanel panel;
  private GemChoice moduleChoice;
  private DateFrField dateStart;
  private DateFrField dateEnd;
  private JFormattedTextField price;
  private JComboBox payment;
  private JComboBox periodicity;
  private Module module;
  private PersonFile personFile;
  private EnrolmentService service;

  public ModuleDlg(Component c, PersonFile p, EnrolmentService service, DataCache dataCache) throws SQLException {
    super(c, BundleUtil.getLabel("Enrolment.label"));
    this.service = service;
    personFile = p;

    moduleChoice = new ModuleChoice(dataCache.getList(Model.Module));
    moduleChoice.addItemListener(this);

    NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.FRANCE);
    nf.setMaximumFractionDigits(2);
    nf.setMinimumFractionDigits(2);
    price = new JFormattedTextField(nf);
    price.setColumns(8);

    Calendar deb = Calendar.getInstance(Locale.FRANCE);
    Calendar cal = Calendar.getInstance(Locale.FRANCE);
    DateFr d = dataCache.getStartOfYear();
    cal.set(d.getYear(), d.getMonth() - 1, d.getDay());
    cal.setTime(cal.getTime());
    if (deb.before(cal)) {
      deb.setTime(cal.getTime());
    }

    dateStart = new DateFrField(deb.getTime());
    dateEnd = new DateFrField(dataCache.getEndOfYear());
    payment = new JComboBox(service.getListOfPayment());
    payment.addItemListener(this);
    periodicity = new JComboBox(new String[]{"MOIS", "TRIM", "ANNU"});

    periodicity.addItemListener(this);

    panel = new GemBorderPanel();
    panel.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(panel);

    gb.add(new GemLabel(BundleUtil.getLabel("Module.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Start.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("End.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Module.basic.rate.label")), 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Mode.of.payment.label")), 0, 4, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Payment.schedule.label")), 0, 5, 1, 1, GridBagHelper.WEST);

    gb.add(moduleChoice, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(dateStart, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(dateEnd, 1, 2, 1, 1, GridBagHelper.WEST);
    gb.add(price, 1, 3, 1, 1, GridBagHelper.WEST);
    gb.add(payment, 1, 4, 1, 1, GridBagHelper.WEST);
    gb.add(periodicity, 1, 5, 1, 1, GridBagHelper.WEST);

    //pour faire apparaître le prix du premier module à l'ouverture
    module = service.getModule(moduleChoice.getKey());
    price.setValue(initPrice(module));

    init();
  }

  @Override
  public GemPanel getMask() {
    return panel;
  }

  public void setField(int n, String val) {
    switch (n) {
      case 0:
        moduleChoice.setKey(Integer.parseInt(val));
        break;
      case 1:
        //choixForfait.setLibel(val);
        break;
      case 2:
        dateStart.setText(val);
        break;
      case 3:
        dateEnd.setText(val);
        break;
      case 4:
        price.setValue(new Double(Double.parseDouble(val)));
        break;
      case 5:
        payment.setSelectedItem(val);
        break;
      case 6:
        periodicity.setSelectedItem(val);
        break;
      case 7:
        moduleChoice.setSelectedIndex(Integer.parseInt(val));
        break;
    }
  }

  public String getField(int n) {
    switch (n) {
      case 0:
        return String.valueOf(moduleChoice.getKey());//id module
      case 1:
        Module f = (Module) moduleChoice.getSelectedItem();
        return f.getTitle();
      case 2:
        return dateStart.getText();//date debut
      case 3:
        return dateEnd.getText();//date fin
      case 4:
        return String.valueOf(price.getValue());
      case 5:
        return (String) payment.getSelectedItem();
      case 6:
        return (String) periodicity.getSelectedItem();
      // ajout d'un case pour l'index sélectionné dans la ComboBox choixModule
      case 7:
        return String.valueOf(((ModuleChoice) moduleChoice).getSelectedKey());
    }
    return null;
  }

  @Override
  public void itemStateChanged(ItemEvent evt) {
    if (evt.getSource() == moduleChoice) {
      try {
        module = service.getModule(moduleChoice.getKey());
      } catch (SQLException ex) {
        System.err.println(getClass().getName() + "#itemStateChanged " + ex.getMessage());
      }
      if (module == null || personFile == null) {
        return;
      }
    }
    calculPrix(module.getBasePrice());
  }

  /**
   * Calculates the base price for an order line.
   *
   * @param normalPrice
   */
  private void calculPrix(double normalPrice) {
    String rs = (String) payment.getSelectedItem();
    String ps = (String) periodicity.getSelectedItem();
    double reducPrice = normalPrice;

    if (!ModeOfPayment.NUL.toString().equals(rs)) {
      if ((Periodicity.TRIM.toString().equals(ps))) {
        if (ModeOfPayment.PRL.toString().equals(rs)) {
          reducPrice = normalPrice - (normalPrice * (module.getQuarterReducRate() / 100));
        }
      } else if (Periodicity.MOIS.toString().equals(ps)) {
        if (ModeOfPayment.PRL.toString().equals(rs)) {
          reducPrice = (normalPrice - (normalPrice * module.getMonthReducRate() / 100)) / 3;
        } else {
          reducPrice = normalPrice / 3;
        }
      } else {//(ps.equals("ANNU"))// si annuel
        reducPrice = normalPrice * 3; // 3 trimestres // prix normal ou prix trimestriel ?
      }
      price.setValue(new Double(reducPrice));
    } else {
      price.setValue(new Double(0.0)); // si NUL
    }
  }

  Double initPrice(Module f) {
    return new Double((f.getBasePrice() - (f.getBasePrice() * (f.getMonthReducRate() / 100))) / 3);
  }

  @Override
  public boolean isEntryValid() {
    if (moduleChoice.getKey() == 0) {
      return false;
    }
    return true;
  }
}
