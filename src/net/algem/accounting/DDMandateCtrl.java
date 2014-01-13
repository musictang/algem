/*
 * @(#)DDMandateCtrl.java 2.8.r 10/01/14
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
package net.algem.accounting;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.List;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.FileTabDialog;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.MessagePopup;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.8.r
 * @since 2.8.r 08/01/14
 */
public class DDMandateCtrl
        extends FileTabDialog
{

	protected DDMandateListCtrl listCtrl;
	
  private List<DDMandate> mandates;
  private DirectDebitService service;
  private GemButton btDelete;
	
	public DDMandateCtrl(GemDesktop desktop) {
		super(desktop);
	}
	
  public DDMandateCtrl(GemDesktop desktop, DirectDebitService service) {
    super(desktop);
    this.service = service;
    btValidation.setText(GemCommand.EDIT_CMD);
    btCancel.setText(GemCommand.CLOSE_CMD);
    btDelete = new GemButton(GemCommand.DELETE_CMD);
    btDelete.addActionListener(this);
    buttons.add(btDelete, 0);
		listCtrl = new DDMandateListCtrl(false, service);

    setLayout(new BorderLayout());
    GemPanel panel = new GemPanel(new BorderLayout());

    panel.add(listCtrl, BorderLayout.CENTER);
    panel.add(buttons, BorderLayout.SOUTH);
    add(panel, BorderLayout.CENTER);
  }

  @Override
  public void validation() {
    List<DDMandate> selected = listCtrl.getSelected();
    if(selected.isEmpty()) {
      MessagePopup.warning(this, MessageUtil.getMessage("no.line.selected"));
      return;
    }
    
    DDMandate mandate = selected.get(0);
    DDMandateEditor editor = new DDMandateEditor(desktop.getFrame(), true, mandate, (selected.size() > 1));
  
    if (editor.isValidated()) {
      try {
        if (selected.size() == 1) {
          service.update(editor.get());
          listCtrl.updateRow(mandate);
        } else {
          service.update(selected, editor.get().getSeqType());
          listCtrl.loadResult(service.getMandates());
        }
      } catch (SQLException ex) {
        GemLogger.logException(ex);
      }
    }
  }

  @Override
  public void cancel() {
    desktop.removeCurrentModule();
  }

  @Override
  public boolean isLoaded() {
    return mandates != null && mandates.size() > 0;
  }

  @Override
  public void load() {   
    try {
      mandates = service.getMandates();
      if (mandates != null) {
        listCtrl.loadResult(mandates);
      }
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
  }
  
  @Override
  public void actionPerformed(ActionEvent e) {
    super.actionPerformed(e);
    if (e.getSource() == btDelete) {
      DDMandate dd = listCtrl.getMandate();
      if (dd == null) {
        MessagePopup.warning(this, MessageUtil.getMessage("no.line.selected"));
        return;
      }
      try {
        if (MessagePopup.confirm(this, MessageUtil.getMessage("direct.debit.delete.mandate.confirmation", dd.getIdper()))) {
          service.deleteMandate(dd.getId());
          listCtrl.deleteRow(dd);
        }
      } catch (SQLException ex) {
        GemLogger.logException(ex);
      }
    }
  }
}
