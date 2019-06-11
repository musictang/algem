/*
 * @(#)MessageDialog.java	2.15.2 27/09/17
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
 *
 */
package net.algem.util.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import net.algem.util.GemCommand;

/**
 * Displays messages in a non modal frame.
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.2
 */
public class MessageDialog
        extends JDialog
        implements ActionListener
{

  private String label;
  private String infos;
  private JButton okButton;
  private JTextArea jtxt;
  private JPanel mainPanel;
  private JPanel infoPanel;
  private JPanel libellePanel;

  public MessageDialog(Frame parent, String title, boolean modal, String label, String infos) {
    super(parent, title, modal);

    this.label = label;
    this.infos = infos;

    initView();

    Point p = parent.getLocationOnScreen();
    p.setLocation(p.getX() + 100D, p.getY());
    setLocation(p);

    pack();
    setVisible(true);
  }

  public MessageDialog(Dialog owner, String title, boolean modal, String label, String infos) {
    super(owner, title, modal);

    this.label = label;
    this.infos = infos;

    initView();

    Point p = owner.getLocationOnScreen();
    p.setLocation(p.getX() + 100D, p.getY());
    setLocation(p);

    pack();
    setVisible(true);
  }

  private void initView() {

    mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    libellePanel = new JPanel();
    libellePanel.setBorder(BorderFactory.createLineBorder(Color.black));
    infoPanel = new JPanel(new BorderLayout());
    infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
    okButton = new JButton(GemCommand.OK_CMD);
    okButton.addActionListener(this);

    libellePanel.add(new JLabel(label));
    jtxt = new JTextArea(infos);
    jtxt.setMargin(new Insets(0, 10, 0, 10));
    infoPanel.add(jtxt, BorderLayout.WEST);
    mainPanel.add(libellePanel, BorderLayout.NORTH);
    mainPanel.add(infoPanel, BorderLayout.CENTER);

    JPanel ap = new JPanel();
    ap.add(okButton);
    mainPanel.add(ap, BorderLayout.SOUTH);

    add(mainPanel);

  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == okButton) {
      close();
    }
  }

  public void close() {
    setVisible(false);
    this.dispose();
  }
}