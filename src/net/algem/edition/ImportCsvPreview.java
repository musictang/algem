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
import javax.swing.JScrollPane;
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
 static final String[]IMPORT_TIPS = {
    BundleUtil.getLabel("Import.number.tip"),
    BundleUtil.getLabel("Import.gender.tip"),
    BundleUtil.getLabel("Name.label"),
    BundleUtil.getLabel("First.name.label"),
    BundleUtil.getLabel("Parent.gender.label"),
    BundleUtil.getLabel("Import.parent.tip"),
    BundleUtil.getLabel("Parent.first.name.label"),
    BundleUtil.getLabel("Import.address.tip"),
    BundleUtil.getLabel("Address2.label"),
    BundleUtil.getLabel("Zipcode.label"),
    BundleUtil.getLabel("City.label"),
    BundleUtil.getLabel("Home.phone.label"),
    BundleUtil.getLabel("Mobile.phone.label"),
    BundleUtil.getLabel("Email.label"),
    BundleUtil.getLabel("Parent.email.label")
  };
  private GemPanel configPanel;
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
    configPanel.setMinimumSize(new Dimension(780, 380));
    gb = new GridBagHelper(configPanel);

    GemPanel mp = new GemPanel();
    mp.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
mp.add(configPanel);

    JScrollPane scp = new JScrollPane(mp);
    scp.setPreferredSize(new Dimension(840, 400));
    add(scp, BorderLayout.CENTER);
  }

  void reload(List<String> csvHeader, List<String> model) {
    this.model = model;
    clear();
    gb.add(new JLabel("<html><b>Algem</b></html>"), 0, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel("<html><b>"+BundleUtil.getLabel("Matching.label")+"<html><b>"), 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel("<html><b>"+BundleUtil.getLabel("Preview.label")+"<html><b>"), 2, 0, 1, 1, GridBagHelper.WEST);
    for (int i=0; i< IMPORT_FIELDS.length; i++) {
      JLabel lb = new JLabel(IMPORT_FIELDS[i]);
      lb.setToolTipText(IMPORT_TIPS[i]);
      gb.add(lb, 0, i+1, 1, 1, GridBagHelper.WEST);
    }

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
