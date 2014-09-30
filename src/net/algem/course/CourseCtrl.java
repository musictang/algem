/*
 * @(#)CourseCtrl.java	2.8.y 29/09/14
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
package net.algem.course;

import java.awt.event.ActionEvent;
import java.sql.SQLException;
import net.algem.enrolment.CourseEnrolmentView;
import net.algem.enrolment.EnrolmentService;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.GemCommand;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.event.GemEvent;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import net.algem.util.ui.CardCtrl;
import net.algem.util.ui.MessagePopup;
import net.algem.util.ui.SearchCtrl;

/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.y
 */
public class CourseCtrl
        extends CardCtrl
{

  private CourseView cv;
  private CourseEnrolmentView iv;
  private Course course;
  private String [] errors = new String[3];
  
  private final GemDesktop desktop;
  private final DataCache dataCache;
  private final EnrolmentService enrolService;
  private final ModuleService service;

  public CourseCtrl(GemDesktop desktop) {
    this.desktop = desktop;
    dataCache = desktop.getDataCache();
    enrolService = new EnrolmentService(dataCache);
    service = new ModuleService(DataCache.getDataConnection());

    cv = new CourseView(
            dataCache.getList(Model.CourseCode),
            dataCache.getList(Model.School));
    iv = new CourseEnrolmentView(enrolService);

    addCard("", cv);
    addCard(BundleUtil.getLabel("Course.enrolment.list.label"), iv);

    select(0);
  }

  @Override
  public boolean next() {
    switch (step) {
      default:
        select(step + 1);
        btPrev.setText(GemCommand.BACK_CMD);
        btPrev.setActionCommand(GemCommand.BACK_CMD);
        break;
    }
    return true;
  }

  @Override
  public boolean cancel() {
    
    if (actionListener != null) {
      if (actionListener instanceof SearchCtrl) {
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "CtrlValider"));
      } else if (actionListener instanceof GemDesktop) {
        clear();
        actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, GemCommand.CANCEL_CMD));
      }
    }

    return true;
  }

  @Override
  public boolean prev() {
    switch (step) {
      case 0:
        
        try {
          if (dataCache.authorize("Course.suppression.auth")) {
            service.delete(cv.get());
            dataCache.remove(course);
            desktop.postEvent(new CourseEvent(this, GemEvent.SUPPRESSION, course));
          } else {
            MessagePopup.warning(this, MessageUtil.getMessage("action.authorization.error", dataCache.getUser().getLogin()));
          }
          close();
        } catch (CourseException cex) {
          MessagePopup.warning(this, cex.getMessage());
          return false;
        } catch (SQLException ex) {
          GemLogger.logException(ex);
          return false;
        }
      case 1:
        btPrev.setActionCommand(GemCommand.DELETE_CMD);
        btPrev.setText(GemCommand.DELETE_CMD);
        select(step - 1);
        break;
      default:
        select(step - 1);
        break;
    }
    return true;
  }

  @Override
  public boolean validation() {
    course = cv.get();
    if (course == null || !isValid(course)) {
      String msg = "";
      for (String e : errors) {
        if (e != null) {
          msg += e + "\n";
        }
      }
      MessagePopup.error(cv, msg);
      errors = new String[3];
      return prev();
    }

    try {
      if (course.getId() == 0) {
        service.create(course);
        dataCache.add(course);
        desktop.postEvent(new CourseEvent(this, GemEvent.CREATION, course));
      } else {
        service.update(course);
        dataCache.update(course);
        desktop.postEvent(new CourseEvent(this, GemEvent.MODIFICATION, course));
      }
    } catch (SQLException e1) {
      GemLogger.logException(getClass().getSimpleName() + "#validation", e1, this);
      return false;
    }
    cancel();
    return true;
  }
  
  private boolean isValid(Course c) {
    
    boolean ok = true;
    
    String t = c.getTitle();
    
    if (t == null || t.length() < Course.MIN_TITLE_LENGTH || t.length() > Course.MAX_TITLE_LENGTH) {
      ok = false;
      errors[0] = MessageUtil.getMessage("course.invalid.title", 
              new Object[] {Course.MIN_TITLE_LENGTH, Course.MAX_TITLE_LENGTH} );
    }
    
    if (c.getLabel() != null && c.getLabel().length() > Course.MAX_LABEL_LENGTH) {
      ok = false;
      errors[1] = MessageUtil.getMessage("course.invalid.label", Course.MAX_LABEL_LENGTH);
    }
    
    if (c.getCode() <= 0) {
      ok = false;
      errors[2] = MessageUtil.getMessage("course.invalid.code");
    }
    
    return ok;

  }

  public void clear() {
    cv.clear();
    iv.clear();
  }

  @Override
  public boolean loadCard(Object o) {
    clear();

    if (o == null || !(o instanceof Course)) {
      return false;
    }

    course = (Course) o;

    cv.set(course);

    if (course.getId() > 0) {
      btPrev.setText(GemCommand.DELETE_CMD);
      btPrev.setActionCommand(GemCommand.DELETE_CMD);
    } else {
      btPrev.setText("");
    }
    select(0);
    iv.load(course.getId());
    return true;
  }

  @Override
  public boolean loadId(int id) {
    try {
      return loadCard(((CourseIO) DataCache.getDao(Model.Course)).findId(id));
    } catch (SQLException ex) {
      GemLogger.log(getClass().getName() + "#loadId :" + ex.getMessage());
    }
    return false;
  }
  
  private void close() {
     if (actionListener != null) {
      actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, GemCommand.CANCEL_CMD));
    } 
  }

}
