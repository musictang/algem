/*
 * @(#) ImportCsvPreview.java Algem 2.13.0 22/03/2017
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import static net.algem.edition.ImportCsvCtrl.IMPORT_FIELDS;
import net.algem.util.BundleUtil;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GridBagHelper;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.13.0
 * @since 2.13.0 22/03/2017
 */
public class ImportCsvPreview
  extends GemPanel
{

  private GemPanel configPanel;
  private JLabel idLabel;
  private JLabel titleLabel;
  private JLabel nameLabel;
  private JLabel firstNameLabel;
  private JLabel streetLabel;
  private JLabel additionalAddressLabel;
  private JLabel zipCodeLabel;
  private JLabel cityLabel;
  private JLabel homePhoneLabel;
  private JLabel mobilePhoneLabel;
  private JLabel email1Label;
  private JLabel email2Label;
  private JComboBox[] matchingBoxes;
  private JLabel[] preview;
  private GridBagHelper gb;
  private int cols;
  private List<String> model;
  private ActionListener cbListener;

  public ImportCsvPreview(int cols) {
    this.cols = cols;
    this.cbListener = new PreviewCsvFieldListener();
    matchingBoxes = new JComboBox[cols];
    preview = new JLabel[cols];
  }

  public void createUi() {
    configPanel = new GemPanel(new GridBagLayout());
    configPanel.setBorder(BorderFactory.createTitledBorder("Configurer"));
    gb = new GridBagHelper(configPanel);

    GemPanel mp = new GemPanel();
    mp.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    mp.add(configPanel);

    add(mp, BorderLayout.CENTER);
  }

  void reload(List<String> csvHeader, List<String> model) {
    this.model = model;
    clear();
     idLabel = new JLabel(IMPORT_FIELDS[0]);
    titleLabel = new JLabel(IMPORT_FIELDS[1]);
    nameLabel = new JLabel(IMPORT_FIELDS[2]);
    firstNameLabel = new JLabel(IMPORT_FIELDS[3]);
    streetLabel = new JLabel(IMPORT_FIELDS[4]);
    additionalAddressLabel = new JLabel(IMPORT_FIELDS[5]);
    zipCodeLabel = new JLabel(IMPORT_FIELDS[6]);
    cityLabel = new JLabel(IMPORT_FIELDS[7]);
    homePhoneLabel = new JLabel(IMPORT_FIELDS[8]);
    mobilePhoneLabel = new JLabel(IMPORT_FIELDS[9]);
    email1Label = new JLabel(IMPORT_FIELDS[10]);
    email2Label = new JLabel(IMPORT_FIELDS[11]);

    gb.add(new JLabel("<html><b>Algem</b></html>"), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel("<html><b>"+BundleUtil.getLabel("Matching.label")+"<html><b>"), 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel("<html><b>"+BundleUtil.getLabel("Preview.label")+"<html><b>"), 2, 0, 1, 1, GridBagHelper.WEST);
    gb.add(idLabel, 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(titleLabel, 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(nameLabel, 0, 3, 1, 1, GridBagHelper.WEST);
    gb.add(firstNameLabel, 0, 4, 1, 1, GridBagHelper.WEST);
    gb.add(streetLabel, 0, 5, 1, 1, GridBagHelper.WEST);
    gb.add(additionalAddressLabel, 0, 6, 1, 1, GridBagHelper.WEST);
    gb.add(zipCodeLabel, 0, 7, 1, 1, GridBagHelper.WEST);
    gb.add(cityLabel, 0, 8, 1, 1, GridBagHelper.WEST);
    gb.add(homePhoneLabel, 0, 9, 1, 1, GridBagHelper.WEST);
    gb.add(mobilePhoneLabel, 0, 10, 1, 1, GridBagHelper.WEST);
    gb.add(email1Label, 0, 11, 1, 1, GridBagHelper.WEST);
    gb.add(email2Label, 0, 12, 1, 1, GridBagHelper.WEST);

    for (int i = 0; i < cols; i++) {
      matchingBoxes[i] = new JComboBox(csvHeader.toArray());
      matchingBoxes[i].addActionListener(cbListener);
      gb.add(matchingBoxes[i], 1, i + 1, 1, 1);
    }
    int def_preview_width = 200;
    for (int i = 0; i < cols; i++) {
      preview[i] = new JLabel();
      preview[i].setPreferredSize(new Dimension(def_preview_width, 20));
      gb.add(preview[i], 2, i + 1, 1, 1, GridBagHelper.HORIZONTAL, GridBagHelper.WEST);
    }
    revalidate();
  }

  void setMatchings(Map<String,Integer> importMap) {
    for (int i = 0 ; i < IMPORT_FIELDS.length ; i++) {
      int index = matchingBoxes[i].getSelectedIndex();
      importMap.put(IMPORT_FIELDS[i], index  == 0 ? -1 : index -1);
    }
  }

  class PreviewCsvFieldListener
          implements ActionListener
  {

    @Override
    public void actionPerformed(ActionEvent e) {
      JComboBox cb = (JComboBox) e.getSource();
      //System.out.println(cb.getSelectedIndex());
      assert (matchingBoxes.length == preview.length);
      int index = 0;
      for (int i = 0; i < matchingBoxes.length; i++) {
        JComboBox c = matchingBoxes[i];
        if (c == cb) {
          index = cb.getSelectedIndex();
          if (index > 0) {
            preview[i].setText(model.get(index - 1));
          } else {
            preview[i].setText(null);
          }
          break;
        }
      }

    }
  }

  private void clear() {
    configPanel.removeAll();
    if (matchingBoxes != null) {
      for (int i = 0; i < matchingBoxes.length; i++) {
        if (matchingBoxes[i] != null) {
          matchingBoxes[i].removeActionListener(cbListener);
          matchingBoxes[i] = null;
        }
      }
    }
    if (preview != null) {
      for (int i = 0; i < preview.length; i++) {
        if (preview[i] != null) {
          preview[i] = null;
        }
      }
    }
  }
}
