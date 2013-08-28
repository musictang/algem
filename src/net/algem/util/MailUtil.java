/*
 * @(#)MailUtil.java	2.8.k 27/08/13
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

package net.algem.util;

import java.sql.SQLException;
import java.util.Vector;
import net.algem.contact.*;
import net.algem.contact.member.MemberService;
import net.algem.group.Musician;
import net.algem.planning.ScheduleRangeObject;
import net.algem.util.jdesktop.DesktopMailHandler;
import net.algem.util.model.Model;

/**
 * Utility class for sending emails.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.k
 * @since 2.8.k 26/07/13
 */
public class MailUtil {
  
  private DataCache dataCache;
  private DataConnection dc;
  private MemberService memberService;
  private DesktopMailHandler mailHandler;

  public MailUtil(DataCache dataCache) {
    this(dataCache, new MemberService(dataCache.getDataConnection()));
  }
  
  public MailUtil(DataCache dataCache, MemberService service) {
    this.dataCache = dataCache;
    dc = dataCache.getDataConnection();
    mailHandler = new DesktopMailHandler();
    memberService = service;
  }
  
  /**
   * Gets member's emails.
   *
   * @param pf
   * @return a sequence of emails, separated by a comma
   */
  private String getEmail(PersonFile pf) {

    String email = "";

    Vector<Email> emails = pf.getContact().getEmail();
    if (emails != null) {
      for (Email e : emails) {
        if (!e.isArchive()) {
          email += (email.length() > 0) ? "," + e.getEmail() : e.getEmail();
        }
      }
    } else {
      // Si l'adherent est lié au payeur
      if (pf.isPayerLinked()) {
        try {
          email = EmailIO.findId(pf.getMember().getPayer(), dc);
        } catch (SQLException e) {
          GemLogger.logException(e);
        }
      }
    }

    return email;
  }

  /**
   * Sends an email to selected schedule's participants.
   *
   * @param ranges selected schedule
   */
  public String mailToMembers(Vector<ScheduleRangeObject> ranges, int teacherId) throws SQLException {

    String message = "";
    StringBuilder bcc = new StringBuilder();

    // recherche de l'email du professeur
    String teacherEmail = memberService.getEmail(teacherId);
    if (teacherEmail != null && teacherEmail.indexOf('@') != -1) {// si contient @
      bcc.append(teacherEmail);
    }
    // recherche des emails des adherents
    for (ScheduleRangeObject pg : ranges) {
      if (pg.getMember().getId() == 0) {// les pauses ne sont pas prises en compte
        continue;
      }
      Person m = pg.getMember();
      PersonFile pf = ((PersonFileIO) DataCache.getDao(Model.PersonFile)).findMember(m.getId(), true);
      if (pf != null) {
        String email = getEmail(pf);
        if (email != null && email.length() > 0) {
          bcc.append((bcc.length() > 0) ? "," + email : email);
        } else {
          message += pf.getContact().getFirstnameName() + "\n";
        }
      } else {
        message += MessageUtil.getMessage("member.null.exception", new Object[]{m.getId()});
      }
    }
    sendMailTo(bcc.toString());
    
    return message;

  }

  /**
   * Sends an email to the members of the group.
   * 
   * @param mus the list of musicians (members)
   */
  public String mailToGroupMembers(Vector<Musician> mus) {
    String message = "";
    StringBuilder bcc = new StringBuilder();

    for (Musician m : mus) {
      PersonFile member = ((PersonFileIO) DataCache.getDao(Model.PersonFile)).findId(m.getId(), true);
      if (member != null) {
        String email = getEmail(member);
        if (email != null && email.length() > 0) {
          bcc.append((bcc.length() > 0) ? "," + email : email);
        } else {
          message += member.getContact().getFirstnameName() + "\n";
        }
      }
    }
    sendMailTo(bcc.toString());
    
    return message;
  }
  
  /**
   * Sends an email to recipients in bcc.
   *
   * @param bcc blind carbon copy
   */
  private void sendMailTo(String bcc) {
    if (bcc.length() <= 0) {
      return;
    }
    String to = null;
    // on recherche le 1er email de l'utilisateur
    try {
      to = EmailIO.findId(dataCache.getUser().getId(), dataCache.getDataConnection());
    } catch (SQLException ex) {
      System.err.println(ex.getMessage());
    }
    if (to == null) {
      to = dataCache.getUser().getLogin() + "@" + BundleUtil.getLabel("Domain");
    }
    mailHandler.send(to, bcc);
    
  }

}
