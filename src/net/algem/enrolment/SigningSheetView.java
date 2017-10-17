/*
 * @(#) SigningSheetView.java Algem 2.15.4 16/10/17
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
package net.algem.enrolment;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.prefs.Preferences;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import net.algem.config.Company;
import net.algem.contact.OrganizationIO;
import net.algem.enrolment.SigningSheetCtrl.SigningSheetType;
import net.algem.planning.DateFr;
import net.algem.planning.DateFrField;
import net.algem.planning.DateRange;
import net.algem.planning.DateRangePanel;
import net.algem.planning.Hour;
import net.algem.planning.HourField;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemCommand;
import net.algem.util.MessageUtil;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemLabel;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.4
 * @since 2.10.0 02/06/2016
 */
public class SigningSheetView
        extends JDialog
        implements ActionListener

{

  private DateRangePanel datePanel;
  private GemButton btOk;
  private GemButton btCancel;
  private boolean validation;
  private Component parent;
  private JRadioButton rStandard, rDetailed;
  private JPanel options;
  private JTextField referent;
  private HourField pm = new HourField("14:00");
  private final static Preferences SIGNING_SHEET_PREFS = Preferences.userRoot().node("/algem/emargement");

  public SigningSheetView() {
  }

  public SigningSheetView(Frame owner, boolean modal) {
    super(owner, modal);
    this.parent = owner;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    validation = (btOk == src);
    setVisible(false);
  }

  public boolean isValidation() {
    return validation;
  }

  SigningSheetType getPageModel() {
    if (rStandard.isSelected()) {
      return SigningSheetType.STANDARD;
    } else if (rDetailed.isSelected()) {
      return SigningSheetType.DETAILED;
    }
    return SigningSheetType.STANDARD;
  }

  void createUI() {
    setTitle(BundleUtil.getLabel("Signing.sheet.label"));
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.DAY_OF_MONTH, 1);
    DateFr start = new DateFr(cal.getTime());
    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
    DateFr end = new DateFr(cal.getTime());
    datePanel = new DateRangePanel(start, end);

    GemPanel buttons = new GemPanel(new GridLayout(1, 2));
    btOk = new GemButton(GemCommand.VALIDATION_CMD);
    btOk.addActionListener(this);
    btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btCancel.addActionListener(this);
    buttons.add(btOk);
    buttons.add(btCancel);

    ButtonGroup stype = new ButtonGroup();
    stype.add(rStandard = new JRadioButton(BundleUtil.getLabel("Signing.sheet.standard.type.label")));
    stype.add(rDetailed = new JRadioButton(BundleUtil.getLabel("Signing.sheet.detailed.type.label")));
    final JTextArea radioHelp = new JTextArea(5, 30);

    final JPanel p = new JPanel(new GridBagLayout());
    radioHelp.setEditable(false);
    radioHelp.setLineWrap(true);
    radioHelp.setWrapStyleWord(true);
    radioHelp.setBackground(p.getBackground());
    radioHelp.setPreferredSize(new Dimension(300, 100));

    referent = new JTextField();
    referent.setPreferredSize(new Dimension(radioHelp.getPreferredSize().width, referent.getPreferredSize().height));

    final GridBagHelper gb = new GridBagHelper(p);

    options = new JPanel(new GridBagLayout());
    GridBagHelper gb2 = new GridBagHelper(options);
    gb2.add(new GemLabel(BundleUtil.getLabel("Signing.sheet.pm.label")), 1, 0, 1, 1, GridBagHelper.WEST);
    gb2.add(pm, 2, 0, 1, 1, GridBagHelper.WEST);
    gb2.add(new GemLabel(BundleUtil.getLabel("Name.and.quality.of.training.manager.label")), 1, 1, 2, 1, GridBagHelper.WEST);
    gb2.add(referent, 1, 2, 2, 1, GridBagHelper.WEST);

    setLayout(new BorderLayout());
    ActionListener typeListener = new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == rStandard) {
          radioHelp.setText(MessageUtil.getMessage("signing.sheet.standard.type.info"));
          p.remove(options);
          revalidate();
        } else if (src == rDetailed) {
          radioHelp.setText(MessageUtil.getMessage("signing.sheet.detailed.type.info"));
          gb.add(options, 1, 3, 2, 1, GridBagHelper.WEST);
          revalidate();
        } else {
          radioHelp.setText(null);
        }

      }
    };
    rStandard.addActionListener(typeListener);
    rDetailed.addActionListener(typeListener);

    gb.add(new GemLabel(BundleUtil.getLabel("Period.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(datePanel, 1, 0, 2, 1, GridBagHelper.WEST);
    gb.add(rStandard, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(rDetailed, 2, 1, 1, 1, GridBagHelper.WEST);
    gb.add(radioHelp, 1, 2, 2, 1, GridBagHelper.WEST);

    String pmPref = SIGNING_SHEET_PREFS.get("pm", null);
    String refPref = SIGNING_SHEET_PREFS.get("ref", null);
    boolean detailPref = SIGNING_SHEET_PREFS.getBoolean("detailed", false);
    if (pmPref != null) {
      pm.set(new Hour(pmPref));
    }
    if (refPref != null) {
      referent.setText(refPref);
    }
    if (detailPref) {
      rDetailed.setSelected(true);
      radioHelp.setText(MessageUtil.getMessage("signing.sheet.detailed.type.info"));
      gb.add(options, 1, 3, 2, 1, GridBagHelper.WEST);
    } else {
      rStandard.setSelected(true);
      radioHelp.setText(MessageUtil.getMessage("signing.sheet.standard.type.info"));
    }

    final JCheckBox cbMemo = new JCheckBox(BundleUtil.getLabel("Signing.sheet.save.options.label"));
    cbMemo.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (cbMemo.isSelected()) {
          SIGNING_SHEET_PREFS.put("pm", pm.getText());
          SIGNING_SHEET_PREFS.put("ref", referent.getText());
          SIGNING_SHEET_PREFS.putBoolean("detailed", rDetailed.isSelected());
        }
      }
    });
    gb.add(cbMemo, 1, 4, 2, 1, GridBagHelper.WEST);

    add(p, BorderLayout.CENTER);
    add(buttons, BorderLayout.SOUTH);

    setSize(440, 380);
    setLocationRelativeTo(parent);

    setVisible(true);

  }

  DateRange getPeriod() {
    return new DateRange(datePanel.getStartFr(), datePanel.getEndFr());
  }

  String[] getOptions() {
    return new String[]{pm.get().toString(), referent.getText()};
  }

}
