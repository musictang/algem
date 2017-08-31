/*
 * @(#) TrainingContractEditor.java Algem 2.15.0 30/08/2017
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
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.algem.accounting.AccountUtil;
import net.algem.planning.DateFrField;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemModule;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemNumericField;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GemToolBar;
import net.algem.util.ui.GridBagHelper;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.0
 * @since 2.15.0 30/08/2017
 */
public class TrainingContractEditor
  extends JDialog
  implements ActionListener {

  private TrainingContract contract;
  private JLabel trainingLabel;

  private JTextField funding;
  private JFormattedTextField total;
  private JFormattedTextField amount;
  private DateFrField startDate;
  private DateFrField endDate;
  private DateFrField signDate;
  private GemNumericField internalLength;
  private JFormattedTextField length;
  private GemButton btSave;
  private GemButton btCancel;

  private GemDesktop desktop;

  public TrainingContractEditor(GemDesktop desktop, String title, boolean modal) {
    super(desktop.getFrame(), title, modal);
    this.desktop = desktop;
  }

  public void createUI() {
    GemToolBar bar = new GemToolBar();
    bar.setFloatable(false);
    bar.addIcon(BundleUtil.getLabel("Training.contract.print.icon"),
            GemCommand.PRINT_CMD,
            BundleUtil.getLabel("Preview.pdf.label"));

    JPanel p = new JPanel(new GridBagLayout());
    trainingLabel = new JLabel();
    startDate = new DateFrField();
    endDate = new DateFrField();
    funding = new JTextField(20);
    NumberFormat nf = AccountUtil.getNumberFormat(2, 2);
    int minSize = 100;
    total = new JFormattedTextField(nf);
    total.setEditable(false);
     total.setPreferredSize(new Dimension(minSize,total.getPreferredSize().height));
    amount = new JFormattedTextField(nf);

    amount.setPreferredSize(new Dimension(minSize,amount.getPreferredSize().height));
//    internalLength = new GemNumericField(3);
    length = new JFormattedTextField(AccountUtil.getDefaultNumberFormat());
    length.setPreferredSize(new Dimension(minSize,length.getPreferredSize().height));
    signDate = new DateFrField();

    GridBagHelper gb = new GridBagHelper(p);
    gb.add(new JLabel(BundleUtil.getLabel("Heading.label")), 0, 0, 1, 1, GridBagHelper.WEST);
    //gb.add(new JLabel(BundleUtil.getLabel("Heading.label")), 0,0,1,1);//saison ??
    gb.add(new JLabel(BundleUtil.getLabel("Start.label")), 0, 1, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("End.label")), 0, 2, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Funding.label")), 0, 3, 1, 1, GridBagHelper.WEST);
    JLabel totalLabel = new JLabel(BundleUtil.getLabel("Total.label"));
    totalLabel.setToolTipText(BundleUtil.getLabel("Training.total.amount.tip"));
    gb.add(totalLabel, 0, 4, 1, 1, GridBagHelper.WEST);
    JLabel amountLabel = new JLabel(BundleUtil.getLabel("Amount.label"));
    amountLabel.setToolTipText(BundleUtil.getLabel("Amount.supported.tip"));
    gb.add(amountLabel, 0, 5, 1, 1, GridBagHelper.WEST);
    JLabel extVolumeLabel = new JLabel(BundleUtil.getLabel("External.training.length.label"));
    extVolumeLabel.setToolTipText(BundleUtil.getLabel("External.training.length.tip"));
    gb.add(extVolumeLabel, 0, 6, 1, 1, GridBagHelper.WEST);
    gb.add(new JLabel(BundleUtil.getLabel("Signature.date.label")), 0, 7, 1, 1, GridBagHelper.WEST);

//    JLabel intVolumeLabel = new JLabel(BundleUtil.getLabel("Internal.training.length.label"));
//    intVolumeLabel.setToolTipText(BundleUtil.getLabel("Internal.training.length.tip"));
//    gb.add(intVolumeLabel, 0, 7, 1, 1, GridBagHelper.WEST);

    gb.add(trainingLabel, 1, 0, 1, 1, GridBagHelper.WEST);
    gb.add(startDate, 1, 1, 1, 1, GridBagHelper.WEST);
    gb.add(endDate, 1, 2, 1, 1, GridBagHelper.WEST);
    gb.add(funding, 1, 3, 1, 1, GridBagHelper.WEST);
    gb.add(total, 1, 4, 1, 1, GridBagHelper.WEST);
    gb.add(amount, 1, 5, 1, 1, GridBagHelper.WEST);
    gb.add(length, 1, 6, 1, 1, GridBagHelper.WEST);
    gb.add(signDate, 1, 7, 1, 1, GridBagHelper.WEST);

    setLayout(new BorderLayout());

    GemPanel buttons = new GemPanel(new GridLayout(1, 2));
    btSave = new GemButton(GemCommand.SAVE_CMD);
    btSave.addActionListener(this);

    btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btCancel.addActionListener(this);

    buttons.add(btSave);
    buttons.add(btCancel);

    add(bar,BorderLayout.NORTH);
    add(p, BorderLayout.CENTER);
    add(buttons, BorderLayout.SOUTH);
    setSize(GemModule.S_SIZE);

    setVisible(true);
    setLocationRelativeTo(desktop.getFrame());
  }

  public void setContract(TrainingContract c) {
    this.contract = c;



    if (c != null) {
      trainingLabel.setText(c.getLabel());
      total.setValue(c.getTotal());
//      internalLength.setText(String.valueOf(c.getIntVolume()));
      if (c.getId() > 0) {
        trainingLabel.setText(c.getLabel());
        startDate.setDate(c.getStart());
        endDate.setDate(c.getEnd());
        signDate.setDate(c.getSignDate());
        funding.setText(c.getFunding());
        amount.setValue(c.getAmount());
        length.setValue(c.getExternalVolume());
      }
    }

  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    if (src == btCancel) {
      setVisible(false);
    }
  }
}
