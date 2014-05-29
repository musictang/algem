/*
 * @(#)EmployeeView.java 2.8.n 04/10/13
 *
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
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
package net.algem.contact;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.text.ParseException;
import java.util.logging.Level;
import javax.swing.JFormattedTextField;
import javax.swing.text.MaskFormatter;
import net.algem.config.ColorPrefs;
import net.algem.config.EmployeeTypePanelCtrl;
import net.algem.planning.DateFr;
import net.algem.planning.DateFrField;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.FileUtil;
import net.algem.util.GemLogger;
import net.algem.util.jdesktop.DesktopHandler;
import net.algem.util.jdesktop.DesktopHandlerException;
import net.algem.util.jdesktop.DesktopOpenHandler;
import net.algem.util.ui.*;

/**
 * Employee view.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.n
 * @since 2.8.m 02/09/13
 */
public class EmployeeView
        extends GemPanel
{

  /** Default format for NIR. */
  private static final String NIR_FORMAT = "# ## ## AA AAA ### ##";

//  private static final String PARENT_DIR = "salaries/";

   /** Default CV dir name. */
  private static final String CV_DIR = "cv";

  /** Default residence permit dir name. */
  private static final String RESIDENCE_DIR = "sejour";

  /** Default DUE dir name. */
  static final String DUE_DIR = "due";

  private GemNumericField idper;

  /** National Identification number. */
  private JFormattedTextField nir;

  /** Birth date. */
  private DateFrField birth;

  /** Place of birth. */
  private GemField place;

  /** Guso number. */
  private GemField guso;

  private GemField nationality;

  private DesktopHandler handler = new DesktopOpenHandler();

  private GemButton cvBt;
  private GemButton dueBt;
  private GemButton residenceBt;

  private File cvFile;
  private File dueFile;
  private File residenceFile;

  private EmployeeService service;
  private EmployeeTypePanelCtrl typeCtrl;

  public EmployeeView(final EmployeeService service, DataCache dataCache) {
    idper = new GemNumericField(6);
    idper.setEditable(false);

      //    insee = new GemField(13, 15);
    nir = new JFormattedTextField(getMask());
    nir.setColumns(13);
    nir.setToolTipText(BundleUtil.getLabel("Nir.tip"));

    this.service = service;
    nir.addFocusListener(new FocusListener()
    {
      @Override
      public void focusLost(FocusEvent e) {
        try {
          nir.commitEdit();
        } catch (ParseException ex) {
          GemLogger.log(Level.WARNING, ex.getMessage());
        }
         markNir();
      }
      @Override
      public void focusGained(FocusEvent e) {
         markNir();
      }

    });
    nir.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        markNir();
      }

    });
    birth = new DateFrField();
    place = new GemField(true, 20);
    place.setToolTipText(BundleUtil.getLabel("Place.of.birth.tip"));
    guso = new GemField(13, 10);

    nationality = new GemField(true, 20);
    typeCtrl = new EmployeeTypePanelCtrl(dataCache);
    

    this.setLayout(new GridBagLayout());
    GridBagHelper gb = new GridBagHelper(this);

    gb.insets = GridBagHelper.SMALL_INSETS;

    GemLabel nirLabel = new GemLabel(BundleUtil.getLabel("Nir.label"));
    nirLabel.setToolTipText(BundleUtil.getLabel("Nir.tip"));
    GemLabel placeLabel = new GemLabel(BundleUtil.getLabel("Place.of.birth.label"));
    placeLabel.setToolTipText(BundleUtil.getLabel("Place.of.birth.tip"));
    gb.add(new GemLabel(BundleUtil.getLabel("Id.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(nirLabel, 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Guso.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Date.of.birth.label")), 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(placeLabel, 0, 4, 1, 1, GridBagHelper.WEST);
    gb.add(new GemLabel(BundleUtil.getLabel("Nationality.label")), 0, 5, 1, 1, GridBagHelper.WEST);
//    gb.add(new GemLabel(BundleUtil.getLabel("Category.label")), 0, 6, 1, 1, GridBagHelper.WEST);

    gb.add(idper, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(nir, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(guso, 1, 2, 1, 1, GridBagHelper.WEST);
    gb.add(birth, 1, 3, 1, 1, GridBagHelper.WEST);
    gb.add(place, 1, 4, 1, 1, GridBagHelper.WEST);
    gb.add(nationality, 1, 5, 1, 1, GridBagHelper.WEST);
    gb.add(typeCtrl, 0, 6, 2, 1, GridBagHelper.BOTH, GridBagHelper.WEST);

    ActionListener fileListener = new EmployeeDocListener();
    GemPanel buttons = new GemPanel(new GridLayout(1,3));

    cvBt = new GemButton(BundleUtil.getLabel("CV.label"));
    cvBt.setToolTipText(BundleUtil.getLabel("CV.tip"));
    cvBt.setEnabled(false);
    cvBt.addActionListener(fileListener);

    dueBt = new GemButton(BundleUtil.getLabel("Hiring.declaration.label"));
    dueBt.setToolTipText(BundleUtil.getLabel("Hiring.declaration.tip"));
    dueBt.setEnabled(false);
    dueBt.addActionListener(fileListener);

    residenceBt = new GemButton(BundleUtil.getLabel("Residence.permit.label"));
    residenceBt.setToolTipText(BundleUtil.getLabel("Residence.permit.tip"));
    residenceBt.setEnabled(false);
    residenceBt.addActionListener(fileListener);

    buttons.add(cvBt);
    buttons.add(dueBt);
    buttons.add(residenceBt);
    gb.insets = new Insets(10, 0, 0, 0);
    gb.add(buttons, 0, 7, 2, 1, GridBagHelper.BOTH, GridBagHelper.WEST);

  }

  private MaskFormatter getMask() {
    MaskFormatter mask = null;
    try {
      mask = new MaskFormatter(NIR_FORMAT);
    } catch (ParseException ex) {
      mask = new MaskFormatter();
      GemLogger.log(Level.SEVERE, ex.getMessage());
    }
    mask.setValueContainsLiteralCharacters(false);

    return mask;
  }

  void set(Employee e) {
    idper.setText(String.valueOf(e.getIdPer()));
    String n = e.getNir() == null ? null : e.getNir().trim();
    nir.setValue(n);
    if (n != null && !n.isEmpty()) {
      markNir();
    }
    birth.setDate(new DateFr(e.getDateBirth()));
    place.setText(e.getPlaceBirth());
    guso.setText(e.getGuso() == null ? null : e.getGuso().trim());
    nationality.setText(e.getNationality());

    for(Integer t : e.getTypes()) {
      typeCtrl.addPanel(t);
    }

    setButtonAccess(e.getIdPer());

  }

  /**
   * Sets the state of the file's access buttons.
   * If a particular file exists for this person, the button is enabled.
   * @param idper
   */
  private void setButtonAccess(int idper) {

    String parent = ((BasicEmployeeService)service).getDocumentPath();

    cvFile = FileUtil.findLastFile(parent, CV_DIR, idper);
    cvBt.setEnabled(cvFile != null && cvFile.canRead());

    dueFile = FileUtil.findLastFile(parent, DUE_DIR, idper);
    dueBt.setEnabled(dueFile != null && dueFile.canRead());

    residenceFile = FileUtil.findLastFile(parent, RESIDENCE_DIR, idper);
    residenceBt.setEnabled(residenceFile != null && residenceFile.canRead());

  }

  /**
   * Opens some file by java desktop.
   * @param path the path of the file to open
   */
  private void open(String path) {
     try {
      ((DesktopOpenHandler) handler).open(path);
      } catch (DesktopHandlerException ex) {
        GemLogger.log(ex.getMessage());
        MessagePopup.warning(this, ex.getMessage());
      }
  }

  public Employee get() {
    Employee e = new Employee(Integer.parseInt(idper.getText()));
    e.setNir(getNir());
    DateFr d = birth.getDateFr();
    if (d.bufferEquals(DateFr.NULLDATE)) {
      d = null;
    }
    e.setDateBirth(d == null ? null : new DateFr(d));
    e.setPlaceBirth(place.getText().trim().toUpperCase());
    e.setGuso(guso.getText().trim().toUpperCase());
    e.setNationality(nationality.getText().trim().toUpperCase());
    
    e.setTypes(typeCtrl.getTypes());

    return e;
  }

  /**
   * Gets the NIR number.
   * @return a string in uppercase
   */
  String getNir() {
    return nir.getValue() == null ? "" : nir.getValue().toString().toUpperCase();
  }

  void clear() {
    nir.setValue(null);
    birth.setDate(new DateFr());
    place.setText(null);
    guso.setText(null);
    nationality.setText(null);
    dueBt.setEnabled(true);
    residenceBt.setEnabled(true);
  }

  /**
   * Changes the display to notify or not an error on NIR number.
   */
  private void markNir() {
    boolean valid = service.checkNir(getNir());
    nir.setBackground(valid ? Color.WHITE : ColorPrefs.ERROR_BG_COLOR);
  }

  private class EmployeeDocListener implements ActionListener
  {
    @Override
    public void actionPerformed(ActionEvent e) {
      Object src = e.getSource();
      if (src == dueBt) {
        if (dueFile != null) {
          open(dueFile.getPath());
        }
      } else if (src == residenceBt) {
         if (residenceFile != null) {
          open(residenceFile.getAbsolutePath());
        }
      } else if (src == cvBt) {
        if (cvFile != null) {
          open(cvFile.getAbsolutePath());
        }
      }
    }

  }

}
