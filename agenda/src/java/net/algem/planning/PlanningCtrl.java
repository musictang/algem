/*
 * @(#)PlanningCtrl.java	1.0.1 06/03/13
 *
 * Copyright (c) 2013 Musiques Tangentes. All Rights Reserved.
 *
 * This file is part of Algem Agenda.
 * Algem Agenda is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Algem Agenda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Algem Agenda. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.algem.planning;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * MVC Controller for planning view.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 1.0.1
 * @since 1.0.0 11/02/13
 */
@Controller
public class PlanningCtrl
{

  @Autowired
  private PlanningService service;
  
  private String estabFilter = " AND id IN (SELECT DISTINCT etablissement FROM salle WHERE public = TRUE)";

  public void setService(PlanningService service) {
    this.service = service;
  }

  /**
   * Adds attributes to model for displaying day's schedule.
   *
   * @param request http request
   * @param model Spring MVC model
   * @return a string representing the view
   * @throws ParseException
   */
  @RequestMapping(method = RequestMethod.GET, value = "/today.html")
  String loadDaySchedule(HttpServletRequest request, Model model) throws ParseException {
    
    String dateParam = request.getParameter("d");
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat dayNameFormat = new SimpleDateFormat("EEEE");
    Date date = dateFormat.parse(request.getParameter("d"));
    String dayName = dayNameFormat.format(date);
    int estab = Integer.parseInt(request.getParameter("e"));

    HashMap<Integer, Collection<ScheduleElement>> schedules = service.getDaySchedule(date, estab);

    model.addAttribute("now", dateParam);
    model.addAttribute("estab", estab);
    model.addAttribute("dayName", dayName);
    model.addAttribute("planning", schedules);
    model.addAttribute("estabList", service.getEstablishments(estabFilter));
    model.addAttribute("freeroom", service.getFreeRoom(date, estab));

    return "day";
  }

  /**
   * Adds to model the list of establishments.
   *
   * @param model
   */
  @RequestMapping(method = RequestMethod.GET, value = "/index.html")
  void loadEstablishment(Model model) {
    model.addAttribute("estabList", service.getEstablishments(estabFilter));
  }
}
