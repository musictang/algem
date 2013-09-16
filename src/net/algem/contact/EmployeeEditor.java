/*
 * @(#)EmployeeEditor.java 2.8.m 03/09/13
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

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.FileTab;
import net.algem.util.ui.GemButton;
import net.algem.util.ui.MessagePopup;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.m
 * @since 2.8.m 02/09/13
 */
public class EmployeeEditor 
  extends FileTab
{

  private EmployeeView view;
  private EmployeeService service;
  private Employee old;
  
  
  public EmployeeEditor(GemDesktop _desktop, ActionListener listener) {
    super(_desktop);
    service = new BasicEmployeeService(dc);
    view = new EmployeeView(service);
    
    this.setLayout(new BorderLayout());
    add(view, BorderLayout.CENTER);
    GemButton deleteBt = new GemButton(GemCommand.DELETE_CMD);
    deleteBt.setActionCommand("EmployeeDelete");
    deleteBt.addActionListener(listener);
    add(deleteBt, BorderLayout.SOUTH);
  }
  
  void setEmployee(int idper) {
    Employee e = null;
    try {
      e = service.find(idper);
      old = e;
    } catch (EmployeeException ex) {
      GemLogger.logException(ex);
    }
    if (e == null) {
      e = new Employee(idper);
    }
    view.set(e);
  }

  Employee get() {
    return view.get();
  }

  void update() {
    Employee e = view.get();
   
    try {
      if (old == null) {
        if (!e.isEmpty()) {
          service.create(e);
          old = e;
        }
      } else if (!old.equals(e)) {
        service.update(e);
        old = e;
      }
    } catch (EmployeeException ex) {
      MessagePopup.warning(this, "Mise à jour salarié impossible");
      GemLogger.logException(ex);
    }
  }
  
  String getNir() {
    return view.getInsee();
  }
  
  @Override
  public boolean isLoaded() {
    return true;
  }

  @Override
  public void load() {

  }
  
  void delete() {
    try {
      service.delete(view.get());
      old = null;
      view.clear();
    } catch (EmployeeException ex) {
      GemLogger.logException(ex);
    }
  }
  
  boolean hasChanged() {
    Employee e = view.get();
    if (e.isEmpty()) {
      return old != null && !old.isEmpty();
    } else {
      return !e.equals(old);
    }
  }

}
