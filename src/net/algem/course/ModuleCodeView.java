/*
 * @(#)ModuleCodeView.java	2.7.a 08/01/13
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

import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import net.algem.util.BundleUtil;
import net.algem.util.GemLogger;
import net.algem.util.ui.GemBorderPanel;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GridBagHelper;

/**
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 */
public class ModuleCodeView
        extends GemBorderPanel
{

  private JComboBox type;
  private JComboBox instrument;
  private JComboBox workshop;
  private JComboBox singleWorkshop;
  private JComboBox musicalFormation;
  private JComboBox others;
  private GemField code;

  public ModuleCodeView() {

    code = new GemField(7);

    type = new JComboBox();
    type.addItem("Cycles de loisir");
    type.addItem("F.Professionnelle");

    instrument = new JComboBox();
    instrument.addItem("Sans");
    instrument.addItem("20 mn");
    instrument.addItem("30 mn");
    instrument.addItem("45 mn");
    instrument.addItem("60 mn");
    instrument.addItem("90 mn");
    instrument.addItem("120 mn");
    instrument.addItem("180 mn");
    instrument.addItem("240 mn");
    instrument.addItem("270 mn");
    instrument.addItem("300 mn");
    instrument.addItem("330 mn");
    instrument.addItem("360 mn");
    instrument.addItem("390 mn");
    instrument.addItem("420 mn");
    instrument.addItem("450 mn");
    instrument.addItem("480 mn");

    workshop = new JComboBox();
    workshop.addItem("Sans");
    workshop.addItem("15 mn");
    workshop.addItem("30 mn");
    workshop.addItem("45 mn");
    workshop.addItem("1 h");
    workshop.addItem("1 h 15");
    workshop.addItem("1 h 30");
    workshop.addItem("1 h 45");
    workshop.addItem("2 h");
    workshop.addItem("2 h 15");
    workshop.addItem("2 h 30");
    workshop.addItem("2 h 45");
    workshop.addItem("3 h");

    singleWorkshop = new JComboBox();
    singleWorkshop.addItem("NON");
    singleWorkshop.addItem("OUI");

    musicalFormation = new JComboBox();
    musicalFormation.addItem("NON");
    musicalFormation.addItem("OUI");

    others = new JComboBox();
    others.addItem(" ");
    others.addItem("Grande formation");
    others.addItem("Accompagnement");
    others.addItem("Répétitions");

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    gb.add(new GemLabel(BundleUtil.getLabel("Type.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Instrument.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Workshop.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Single.workshop.label")), 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Musical.training.label")), 0, 4, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Others.label")), 0, 5, 1, 1, GridBagHelper.WEST);

    gb.add(type, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(instrument, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(workshop, 1, 2, 1, 1, GridBagHelper.WEST);
    gb.add(singleWorkshop, 1, 3, 1, 1, GridBagHelper.WEST);
    gb.add(musicalFormation, 1, 4, 1, 1, GridBagHelper.WEST);
    gb.add(others, 1, 5, 1, 1, GridBagHelper.WEST);

    setBorder(BorderFactory.createTitledBorder(BundleUtil.getLabel("Code.label")));
  }

  public String getCode() {
    StringBuilder s = new StringBuilder();
    int i = type.getSelectedIndex();
    switch(i) {
      case 0 :
        s.append("L");
        break;
      case 1:
        s.append("P");
        break;    
    }
    
    i = instrument.getSelectedIndex();
    switch (i) {
      case 0:
        s.append("000");
        break;
      case 1:
        s.append("020");
        break;
      case 2:
        s.append("030");
        break;
      case 3:
        s.append("045");
        break;
      case 4:
        s.append("060");
        break;
      case 5:
        s.append("090");
        break;
      case 6:
        s.append("120");
        break;
      case 7:
        s.append("180");
        break;
      case 8:
        s.append("240");
        break;
      case 9:
        s.append("270");
        break;
      case 10:
        s.append("300");
        break;
      case 11:
        s.append("330");
        break;
      case 12:
        s.append("360");
        break;
      case 13:
        s.append("390");
        break;
      case 14:
        s.append("420");
        break;
      case 15:
        s.append("450");
        break;
      case 16:
        s.append("480");
        break;
    }
    //
    i = workshop.getSelectedIndex();
    if (i == 0) {
      s.append("000");
    } else if (i == 1) {
      s.append("015");
    } else if (i == 2) {
      s.append("030");
    } else if (i == 3) {
      s.append("045");
    } else if (i == 4) {
      s.append("060");
    } else if (i == 5) {
      s.append("075");
    } else if (i == 6) {
      s.append("090");
    } else if (i == 7) {
      s.append("105");
    } else if (i == 8) {
      s.append("120");
    } else if (i == 9) {
      s.append("135");
    } else if (i == 10) {
      s.append("150");
    } else if (i == 11) {
      s.append("165");
    } else if (i == 12) {
      s.append("180");
    }
    i = singleWorkshop.getSelectedIndex();
    if (i == 0) {
      s.append("0");
    } else if (i == 1) {
      s.append("1");
    }
    i = musicalFormation.getSelectedIndex();
    if (i == 0) {
      s.append("0");
    } else if (i == 1) {
      s.append("1");
    }
    i = others.getSelectedIndex();
    if (i == 0) {
      s.append(" ");
    } else if (i == 1) {
      s.append("B");
    } else if (i == 2) {
      s.append("A");
    } else if (i == 3) {
      s.append("R");
    }

    return s.toString();
  }

  public void setCode(String _code) {

    try {
      if (_code.startsWith("L")) {
        type.setSelectedIndex(0);
      } else if (_code.startsWith("P")) {
        type.setSelectedIndex(1);
      }
      String s = _code.substring(1, 4);
      if (s.equals("000")) {
        instrument.setSelectedIndex(0);
      } else if (s.equals("020")) {
        instrument.setSelectedIndex(1);
      } else if (s.equals("030")) {
        instrument.setSelectedIndex(2);
      } else if (s.equals("045")) {
        instrument.setSelectedIndex(3);
      } else if (s.equals("060")) {
        instrument.setSelectedIndex(4);
      } else if (s.equals("090")) {
        instrument.setSelectedIndex(5);
      } else if (s.equals("120")) {
        instrument.setSelectedIndex(6);
      } else if (s.equals("180")) {
        instrument.setSelectedIndex(7);
      } else if (s.equals("240")) {
        instrument.setSelectedIndex(8);
      } else if (s.equals("270")) {
        instrument.setSelectedIndex(9);
      } else if (s.equals("300")) {
        instrument.setSelectedIndex(10);
      } else if (s.equals("330")) {
        instrument.setSelectedIndex(11);
      } else if (s.equals("360")) {
        instrument.setSelectedIndex(12);
      } else if (s.equals("390")) {
        instrument.setSelectedIndex(13);
      } else if (s.equals("420")) {
        instrument.setSelectedIndex(14);
      } else if (s.equals("450")) {
        instrument.setSelectedIndex(15);
      } else if (s.equals("480")) {
        instrument.setSelectedIndex(16);
      } 

      s = _code.substring(4, 7);
      if (s.equals("000")) {
        workshop.setSelectedIndex(0);
      } else if (s.equals("015")) {
        workshop.setSelectedIndex(1);
      } else if (s.equals("030")) {
        workshop.setSelectedIndex(2);
      } else if (s.equals("045")) {
        workshop.setSelectedIndex(3);
      } else if (s.equals("060")) {
        workshop.setSelectedIndex(4);
      } else if (s.equals("075")) {
        workshop.setSelectedIndex(5);
      } else if (s.equals("090")) {
        workshop.setSelectedIndex(6);
      } else if (s.equals("105")) {
        workshop.setSelectedIndex(7);
      } else if (s.equals("120")) {
        workshop.setSelectedIndex(8);
      } else if (s.equals("135")) {
        workshop.setSelectedIndex(9);
      } else if (s.equals("150")) {
        workshop.setSelectedIndex(10);
      } else if (s.equals("165")) {
        workshop.setSelectedIndex(11);
      } else if (s.equals("180")) {
        workshop.setSelectedIndex(12);
      }
      
      s = _code.substring(7, 8);
      if (s.equals("0")) {
        singleWorkshop.setSelectedIndex(0);
      } else if (s.equals("1")) {
        singleWorkshop.setSelectedIndex(1);
      }
      s = _code.substring(8, 9);
      if (s.equals("0")) {
        musicalFormation.setSelectedIndex(0);
      } else if (s.equals("1")) {
        musicalFormation.setSelectedIndex(1);
      }
      
      if (_code.length() > 9) {
        if (_code.endsWith(" ")) {
          others.setSelectedIndex(0);
        } else if (_code.endsWith("B")) {
          others.setSelectedIndex(1);
        } else if (_code.endsWith("A")) {
          others.setSelectedIndex(2);
        } else if (_code.endsWith("R")) {
          others.setSelectedIndex(3);
        }
      } else {
        others.setSelectedIndex(0);
      }
    } catch (Exception ex) {
      GemLogger.log(getClass().getName()+"#setCode :"+ex.getMessage());
    }
  }

  public void clear() {
    code.setText("");
  }
}
