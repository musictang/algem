/*
 * @(#)MailUtil.java	2.17.0 08/04/2019
 *                      2.15.2 27/09/17
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

package net.algem.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import net.algem.config.Config;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.contact.*;
import net.algem.contact.member.MemberService;
import net.algem.group.Musician;
import net.algem.planning.Schedule;
import net.algem.planning.ScheduleIO;
import net.algem.planning.ScheduleRangeIO;
import net.algem.planning.ScheduleRangeObject;
import net.algem.security.User;
import net.algem.util.jdesktop.DesktopMailHandler;
import net.algem.util.model.Model;

/**
 * Utility class for sending emails.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @version 2.17.0
 * @since 2.8.k 26/07/13
 */
public class MailUtil {

  private DataCache dataCache;
  private DataConnection dc;
  private MemberService memberService;
  private DesktopMailHandler mailHandler;

  public MailUtil(DataCache dataCache) {
    this(dataCache, new MemberService(DataCache.getDataConnection()));
  }

  public MailUtil(DataCache dataCache, MemberService service) {
    this.dataCache = dataCache;
    dc = DataCache.getDataConnection();
    mailHandler = new DesktopMailHandler();
    memberService = service;
  }

  public static String urlEncode(String str) {
    try {
      return URLEncoder.encode(str, "UTF-8").replace("+", "%20");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
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
   * @param schedule
   * @return a mailto-string
   * @throws java.sql.SQLException
   */
  public String mailToMembers(Vector<ScheduleRangeObject> ranges, Schedule schedule) throws SQLException {

    String message = "";
    StringBuilder bcc = new StringBuilder();

    // recherche de l'email du professeur
    if (Schedule.COURSE == schedule.getType() || Schedule.WORKSHOP == schedule.getType() || Schedule.TRAINING == schedule.getType()) {
      String teacherEmail = memberService.getEmail(schedule.getIdPerson());
      if (teacherEmail != null && teacherEmail.indexOf('@') != -1) {// si contient @
        bcc.append(teacherEmail);
      }
    }
    // recherche des emails des adherents
    for (ScheduleRangeObject pg : ranges) {
      if (pg.getMember().getId() == 0) {// les pauses ne sont pas prises en compte
        continue;
      }
      Person m = pg.getMember();
//      PersonFile pf = ((PersonFileIO) DataCache.getDao(Model.PersonFile)).findMember(m.getId(), true);
      PersonFile pf = ((PersonFileIO) DataCache.getDao(Model.PersonFile)).findId(m.getId());
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
   * @return a mailto-string
   */
  public String mailToGroupMembers(List<Musician> mus) {
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

  public String mailToGroupMembers(List<Musician> mus, int action) throws SQLException {
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
    String where = "SELECT DISTINCT e.email FROM " + ScheduleRangeIO.TABLE + " pg, " + ScheduleIO.TABLE + " p, " + EmailIO.TABLE + " e"
            + " WHERE p.action = " + action
            + " AND pg.idplanning = p.id"
            + " AND p.ptype = " + Schedule.TECH
            + " AND pg.adherent = e.idper";
    ResultSet rs = dc.executeQuery(where);
    while (rs.next()) {
      bcc.append((bcc.length() > 0) ? "," + rs.getString(1) : rs.getString(1));
    }
    sendMailTo(bcc.toString());

    return message;
  }

  public static String getSignature(User user) {
    String org = ConfigUtil.getConf(ConfigKey.ORGANIZATION_NAME.getKey());
    /*String address1 = ConfigUtil.getConf(ConfigKey.ORGANIZATION_ADDRESS1.getKey());
    String address2 = ConfigUtil.getConf(ConfigKey.ORGANIZATION_ADDRESS2.getKey());
    String zipcode = ConfigUtil.getConf(ConfigKey.ORGANIZATION_ZIPCODE.getKey());
    String city = ConfigUtil.getConf(ConfigKey.ORGANIZATION_CITY.getKey());*/
    String name = user == null ? "" : user.getFirstName();
    return org == null ? "\n" + name : "\n" + org + "\n" + name;
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
      to = EmailIO.findId(dataCache.getUser().getId(), dc);
    } catch (SQLException ex) {
      System.err.println(ex.getMessage());
    }
    if (to == null) {
      String domain = ConfigUtil.getConf(ConfigKey.ORGANIZATION_DOMAIN.getKey());
      to = dataCache.getUser().getLogin() + "@" + (domain == null ? BundleUtil.getLabel("Domain") : domain.trim().toLowerCase());
    }
    mailHandler.send(to, bcc);
  }

  public static Session SmtpInitSession() { //ERIC 2.17.0 08/04/2019

/* FAI Orange
        prop.put("mail.transport.protocol", "smtp");
        prop.put("mail.smtp.host", "smtp.orange.fr");
        prop.put("mail.smtp.port", "465");
        prop.put("mail.smtp.auth", true); 
        prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.ssl.trust", "smtp.orange.fr");
/**/
/* FAI Free 
        prop.put("mail.transport.protocol", "smtp");
        prop.put("mail.smtp.host", "smtp.free.fr");
        prop.put("mail.smtp.port", "465");
        prop.put("mail.smtp.auth", false); // inutile chez free
        prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.ssl.trust", "smtp.free.fr");
/**/
/* FAI OVH 
        prop.put("mail.transport.protocol", "smtp");
        prop.put("mail.smtp.host", "ssl0.ovh.net");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", true); // obligatoire chez ovh
        prop.put("mail.smtp.starttls.enable", "true");
/**/  
      
    Config config;

    Properties prop = new Properties();
    prop.put("mail.transport.protocol", "smtp");
    prop.put("mail.smtp.host", ConfigUtil.getConf(ConfigKey.SMTP_SERVER_NAME.getKey()));
    prop.put("mail.smtp.port", ConfigUtil.getConf(ConfigKey.SMTP_SERVER_PORT.getKey()));
    prop.put("mail.smtp.auth", ConfigUtil.getConf(ConfigKey.SMTP_SERVER_AUTH.getKey()));
    if (ConfigUtil.getConf(ConfigKey.SMTP_SERVER_SECURITY.getKey()).equals("SSL")) {
        prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.ssl.trust", ConfigUtil.getConf(ConfigKey.SMTP_SERVER_NAME.getKey()));
        prop.put("mail.smtps.connectiontimeout", "2000");
        prop.put("mail.smtps.timeout", "5000");
        
    } else if (ConfigUtil.getConf(ConfigKey.SMTP_SERVER_SECURITY.getKey()).equals("TLS")) {
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtps.connectiontimeout", "2000");
        prop.put("mail.smtps.timeout", "5000");
    } else {
        prop.put("mail.smtp.connectiontimeout", "2000");
        prop.put("mail.smtp.timeout", "5000");
    }

    //System.out.println("auth="+ConfigUtil.getConf(ConfigKey.SMTP_SERVER_AUTH.getKey()) +" security="+ConfigUtil.getConf(ConfigKey.SMTP_SERVER_SECURITY.getKey()));
    //System.out.println("user="+ConfigUtil.getConf(ConfigKey.SMTP_SERVER_USER.getKey()) +" pswd="+ConfigUtil.getConf(ConfigKey.SMTP_SERVER_PSWD.getKey()));
    Session session = Session.getInstance(prop, new Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(ConfigUtil.getConf(ConfigKey.SMTP_SERVER_USER.getKey()), ConfigUtil.getConf(ConfigKey.SMTP_SERVER_PSWD.getKey()));
        }
    });
    //session.setDebug(true);
    
    return session;
  }
}
