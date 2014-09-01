/*
 * @(#)ModuleDlg.java	2.8.w 23/07/14
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
package net.algem.enrolment;

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
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.contact.PersonFile;
import net.algem.course.Module;
import net.algem.course.ModuleChoice;
import net.algem.planning.DateFr;
import net.algem.planning.DateFrField;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemLogger;
import net.algem.util.model.Model;
import net.algem.util.ui.*;

/**
 * Selection dialog for modules during enrolment.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 * @since 1.0a 07/07/1999
 */
public class ModuleDlg
        extends PopupDlg
        implements ItemListener
{

  private GemPanel maskPanel;
  private GemChoice moduleChoice;
  private DateFrField dateStart;
  private DateFrField dateEnd;
  private JFormattedTextField price;
  private JComboBox payment;
  private JComboBox frequency;
  private Module module;
  private PersonFile personFile;
  private EnrolmentService service;

  public ModuleDlg() {
  }
  
  public ModuleDlg(Component c, PersonFile p, EnrolmentService service, DataCache dataCache) throws SQLException {
    super(c, BundleUtil.getLabel("Module.label"));
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
    frequency = new JComboBox(new Enum[]{PayFrequency.MONTH, PayFrequency.QUARTER, PayFrequency.YEAR});

    frequency.addItemListener(this);

    maskPanel = new GemPanel();
    maskPanel.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(maskPanel);
    gb.insets = GridBagHelper.SMALL_INSETS;
    
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
    gb.add(frequency, 1, 5, 1, 1, GridBagHelper.WEST);

    //pour faire apparaître le prix du premier module à l'ouverture
    module = service.getModule(moduleChoice.getKey());
    price.setValue(initPrice(module));
    init();
  }

  @Override
  public GemPanel getMask() {
    return maskPanel;
  }

  public void setField(int n, Object val) {
    switch (n) {
      case 0:
        moduleChoice.setKey((Integer) val);
        break;
      case 1:
        //choixForfait.setLibel(val);
        break;
      case 2:
        dateStart.set((DateFr) val);
        break;
      case 3:
        dateEnd.set((DateFr) val);
        break;
      case 4:
        price.setValue((Double) val);
        break;
      case 5:
        payment.setSelectedItem(val);
        break;
      case 6:
        frequency.setSelectedItem(val);
        break;
      case 7:
        moduleChoice.setSelectedIndex((Integer) val);
        break;
    }
  }

  public Object getField(int n) {
    switch (n) {
      case 0:
        return moduleChoice.getKey();//id module
      case 1:
        Module m = (Module) moduleChoice.getSelectedItem();
        return m.getTitle();
      case 2:
        return dateStart.get();
      case 3:
        return dateEnd.get();
      case 4:
        return price.getValue();
      case 5:
        return payment.getSelectedItem();
      case 6:
        return frequency.getSelectedItem();
      // ajout d'un case pour l'index sélectionné dans la ComboBox choixModule
      case 7:
//        return String.valueOf(((ModuleChoice) moduleChoice).getSelectedKey());
        return ((ModuleChoice) moduleChoice).getSelectedKey();
    }
    return null;
  }

  @Override
  public void itemStateChanged(ItemEvent evt) {
    if (evt.getSource() == moduleChoice) {
      try {
        module = service.getModule(moduleChoice.getKey());
      } catch (SQLException ex) {
        GemLogger.log(getClass().getName() + "#itemStateChanged " + ex.getMessage());
      }
      if (module == null || personFile == null) {
        return;
      }
    }
//    calculatePrice(module.getBasePrice()); // commenté 2.8.w
    price.setValue(calculatePayment(module, (String) getField(5), (PayFrequency) getField(6), getDefaultFrequency()));
  }

  /**
   * Calculates the base price for an order line.
   *
   * @param normalPrice
   */
  private void calculatePrice(double normalPrice) {
    String rs = (String) payment.getSelectedItem();
    String ps = (String) frequency.getSelectedItem();
    double reducPrice = normalPrice;

    if (!ModeOfPayment.NUL.toString().equals(rs)) {
      if ((PayFrequency.QUARTER.getName().equals(ps))) {
        if (ModeOfPayment.PRL.toString().equals(rs)) {
          reducPrice = normalPrice - (normalPrice * (module.getQuarterReducRate() / 100));
        }
      } else if (PayFrequency.MONTH.getName().equals(ps)) {
        if (ModeOfPayment.PRL.toString().equals(rs)) {
          reducPrice = (normalPrice - (normalPrice * module.getMonthReducRate() / 100)) / 3;
        } else {
          reducPrice = normalPrice / 3;
        }
      } else {//(ps.bufferEquals("ANNU"))// si annuel
        reducPrice = normalPrice * 3; // 3 trimestres // prix normal ou prix trimestriel ?
      }
      price.setValue(new Double(reducPrice));
    } else {
      price.setValue(new Double(0.0)); // si NUL
    }
  }
  
  double calculatePayment(Module m, String mp, PayFrequency pf, PayFrequency def) {
    double price = m.getBasePrice();
    double reduc = m.getBasePrice();
    if (ModeOfPayment.PRL.toString().equals(mp)) {
      switch (pf) {
        case YEAR:
          break;
        case SEMESTER:
          break;
        case QUARTER:
          price = price - (price * m.getQuarterReducRate() / 100);
          break;
        case MONTH:
          price = price - (price * m.getMonthReducRate() / 100);
          break;
      }
    }
    
    if (def.equals(PayFrequency.YEAR)) {
      switch (pf) {
        case YEAR:
          reduc = price;
          break;
        case SEMESTER:
          reduc = price;
          break;
        case QUARTER:
          reduc = price / 3;
          break;
        case MONTH:
          reduc = price / 9;
          break;
      }
    } else if (def.equals(PayFrequency.SEMESTER)) {
      switch (pf) {
        case YEAR:
          reduc = price;
          break;
        case SEMESTER:
          reduc = price;
          break;
        case QUARTER:
          reduc = price / 2;
          break;
        case MONTH:
          reduc = price / 6;
          break;
      }
    } else if (def.equals(PayFrequency.QUARTER)) {
      switch (pf) {
        case YEAR:
          reduc = price * 3;
          break;
        case SEMESTER:
          reduc = price * 2;
          break;
        case QUARTER:
          reduc = price;
          break;
        case MONTH:
          reduc = price / 3;
          break;
      }
    } else if (def.equals(PayFrequency.MONTH)) {
      switch (pf) {
        case YEAR:
          reduc = price * 9;
          break;
        case SEMESTER:
          reduc = price * 6;
          break;
        case QUARTER:
          reduc = price * 3;
          break;
        case MONTH:
          reduc = price;
          break;
      }
    }

    return reduc;
  }

  private Double initPrice(Module f) {
//    return new Double((f.getBasePrice() - (f.getBasePrice() * (f.getMonthReducRate() / 100))) / 3);
    return calculatePayment(f, ModeOfPayment.CHQ.toString(), PayFrequency.MONTH, getDefaultFrequency());
  }

  @Override
  public boolean isEntryValid() {
    return moduleChoice.getKey() > 0;
  }
  
  void setTitle(String t) {
    dlg.setTitle(t);
  }
  
  private PayFrequency getDefaultFrequency() {
    String conf = ConfigUtil.getConf(ConfigKey.BASIC_RATE_FREQUENCY.getKey());
    return conf != null ? ModuleOrderIO.getFrequencyByName(conf) : PayFrequency.QUARTER;
  }
}
