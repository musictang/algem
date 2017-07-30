/*
 * @(#) OrganizationCtrl.java Algem 2.15.0 30/07/2017
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
package net.algem.contact;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JDialog;
import net.algem.util.BundleUtil;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.MessagePopup;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.0
 * @since 2.15.0 30/07/2017
 */
public class OrganizationCtrl
  extends JDialog {

  private OrganizationView orgView;
  private PersonView personView;
  private Person person;
  private OrganizationIO orgIO;
  private boolean isCreation;

  public OrganizationCtrl(Frame owner, boolean modal, PersonView personView) {
    super(owner, modal);
    setTitle(BundleUtil.getLabel("Organization.details.label"));
    this.personView = personView;
    this.orgIO = personView.getDao();
  }

  public void setIsCreation(boolean isCreation) {
    this.isCreation = isCreation;
  }

  void setView() {
    try {
      person = personView.get();
      List<Person> pers = orgIO.findMembers(person.getOrganization().getId());
      if (pers.isEmpty()) {
        pers.add(person);
      }
      orgView = new OrganizationView(pers);
      Organization org = orgIO.findId(person.getOrganization().getId());

      if (org == null || isCreation) {
        org = new Organization(0);
        org.setName(personView.getOrgName());
        org.setReferent(person.getId());
      } else {
        isCreation = false;
      }

      orgView.createUI();
      orgView.setDetails(org);
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
  }

  void createUI() {
    setView();
    GemPanel btPanel = new GemPanel(new GridLayout(1, 2));
    GemButton btSave = new GemButton(GemCommand.SAVE_CMD);
    btSave.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int idper = personView.get().getId();
        if (idper == 0) {
          MessagePopup.warning(null, MessageUtil.getMessage("organization.null.id.warning"));
          setVisible(false);
          return;
        }
        Organization o = orgView.getDetails();
        updateOrganization(o, orgIO);
        setVisible(false);
      }
    });

    GemButton btCancel = new GemButton(GemCommand.CANCEL_CMD);
    btCancel.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
      }
    });
    btPanel.add(btSave);
    btPanel.add(btCancel);

    add(orgView, BorderLayout.CENTER);
    add(btPanel, BorderLayout.SOUTH);
    pack();
    setLocationRelativeTo(this);
    setVisible(true);
  }

  Organization getOrganization() {
    return orgView.getDetails();
  }

  void updateOrganization(Organization target, OrganizationIO orgIO) {
    try {
      Organization o = orgIO.findId(target.getId());
      if (o == null) {
        orgIO.create(target);
      } else {
        orgIO.update(target);
      }
      personView.setOrganization(target);
    } catch (SQLException ex) {
      GemLogger.logException(ex);
      if ("23505".equals(ex.getSQLState())) {
        MessagePopup.warning(null, MessageUtil.getMessage("organization.unique.violation.exception"));
      } else {
        MessagePopup.warning(null, ex.getMessage());
      }
    }
  }
}
