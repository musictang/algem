/*
 * @(#) SigningSheetCtrl.java Algem 2.10.2 23/06/16
 *
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
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

import com.sun.org.apache.bcel.internal.util.SecuritySupport;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.SwingWorker;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.contact.Person;
import net.algem.contact.member.MemberService;
//import static net.algem.enrolment.MemberEnrolmentEditor.getCss;
import net.algem.planning.DateFr;
import net.algem.planning.ScheduleRangeObject;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.FileUtil;
import net.algem.util.GemLogger;
import net.algem.util.jdesktop.DesktopBrowseHandler;
import net.algem.util.jdesktop.DesktopHandlerException;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import org.apache.commons.codec.binary.Base64;
import org.apache.poi.util.IOUtils;
import sun.awt.image.ByteArrayImageSource;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.10.2
 * @since 2.10.0 01/06/16
 */
public class SigningSheetCtrl
  extends SwingWorker<Void, Void> {

  private EnrolmentService service;
  private DataCache dataCache;
  private MemberService memberService;
  private File temp;
  private PrintWriter pw;
  private SigningSheetView dlg;

  public SigningSheetCtrl(GemDesktop desktop) {
    this.dataCache = desktop.getDataCache();
    this.service = new EnrolmentService(dataCache);
    this.memberService = new MemberService(DataCache.getDataConnection());
    this.dlg = new SigningSheetView(desktop.getFrame(), true);
    dlg.createUI();
  }

  public boolean isValidation() {
    return dlg.isValidation();
  }

  @Override
  protected Void doInBackground() throws Exception {

    try {
      temp = File.createTempFile(BundleUtil.getLabel("Signing.sheet.label"), ".html");
      pw = new PrintWriter(temp, StandardCharsets.UTF_8.name());
      SigningSheetType sheetType = SigningSheetType.AM_PM;//TODO GET TYPE
      pw.println(FileUtil.getHtmlHeader(BundleUtil.getLabel("Enrolment.label"), getCss(sheetType)));

      DateFr from = new DateFr(ConfigUtil.getConf(ConfigKey.PRE_ENROLMENT_START_DATE.getKey()));
      DateFr to = new DateFr(ConfigUtil.getConf(ConfigKey.END_YEAR.getKey()));
      List<Enrolment> all = service.getProEnrolments(from.getDate(), to.getDate());
      DateFr start = dlg.getPeriod().getStart();
      DateFr end = dlg.getPeriod().getEnd();
      for (int i = 0, len = all.size(); i < len; i++) {
        Enrolment e = all.get(i);
        List<ScheduleRangeObject> ranges = MemberEnrolmentEditor.getActivityRanges(e.getMember(), start, end, getActions(e.getCourseOrder()), memberService);
        if (SigningSheetType.FULL.equals(sheetType)) {
          pw.println(getEnrolmentInfo(e,SigningSheetType.FULL, start));
          pw.println(MemberEnrolmentEditor.fillActivityFull(ranges));
          //pw.println("<div class=\"pageBreak\"></div>");
        } else {

          String logoImgData = getImgDataSource("logo.png");
          pw.print("<header><img class=\"logo\" src=\""+logoImgData+"\" width=100 />");
          pw.print(getEnrolmentInfo(e,SigningSheetType.AM_PM, start));
          pw.println("</header>");
          pw.println(MemberEnrolmentEditor.fillActivityAMPM(ranges));
           String bannerImgData = getImgDataSource("partenaires.png");
           pw.println("<div class=\"banner\"><img src=\""+bannerImgData+"\" width=\"610\"/></div>");
        }

//        pw.println(MemberEnrolmentEditor.catchActivity(e.getMember(), start, end, getActions(e.getCourseOrder()), memberService));

        int p = (i + 1) * 100 / len;
        setProgress(p);
      }
      pw.println("</body></html>");
    } catch (SQLException ex) {
      GemLogger.log(ex.getMessage());
    } catch (IOException ex) {
      GemLogger.log(ex.getMessage());
    }
    return null;
  }

  private String getImgDataSource(String fileName) {
    try {
      InputStream logoStream = getClass().getResourceAsStream("/resources/img/" + fileName);
      String b64Src = Base64.encodeBase64String(IOUtils.toByteArray(logoStream));
      return "data:image/png;base64," + b64Src;
    } catch (IOException ex) {
      GemLogger.log(Level.WARNING, ex.getMessage());
    }
    return "";
  }

  @Override
  protected void done() {
    setProgress(100);
    if (pw != null) {
      pw.close();
      try {
        if (temp != null) {
          DesktopBrowseHandler browser = new DesktopBrowseHandler();
          browser.browse(temp.toURI().toString());
        }
      } catch (DesktopHandlerException de) {
        GemLogger.log(de.getMessage());
      }
    }
  }

  private String getEnrolmentInfo(Enrolment enrol, SigningSheetType type, DateFr from) throws SQLException {
    StringBuilder sb = new StringBuilder();
    Person p = (Person) DataCache.findId(enrol.getMember(), Model.Person);
    String nickName = p.getNickName();
    switch (type) {
      case FULL:
        sb.append("<h1>").append(p.getFirstnameName()).append(" : ").append(nickName == null ? "" : nickName).append("</h1>");
        sb.append("<h2>")
          .append(BundleUtil.getLabel("Enrolment.label")).append(" n° ")
          .append(enrol.getOrder().getId()).append(' ')
          .append(BundleUtil.getLabel("Date.From.label").toLowerCase()).append(' ')
          .append(enrol.getOrder().getCreation())
          .append("</h2>");
        if (enrol.getModule().size() > 0) {
          sb.append("<ul>");
          for (ModuleOrder mo : enrol.getModule()) {
            sb.append("<li>").append(getModuleInfo(mo, enrol.getMember())).append("</li>");
            if (enrol.getCourseOrder().size() > 0) {
              sb.append("<ul>");
              for (CourseEnrolmentNode c : getCourseNodes(enrol.getCourseOrder(), enrol.getMember())) {
                sb.append("<li>").append(c).append("</li>");
              }
              sb.append("</ul>");
            }
          }
          sb.append("</ul>");
        }
        break;
      case AM_PM:
        DateFormat df = new SimpleDateFormat("MMM YYYY");
        sb.append("<table class=\"top\"><tr><td><h1>").append(df.format(from.getDate())).append("</h1></td><td>Feuille d'émargement centre<br />");
        sb.append(p.getFirstnameName()).append("<br />");
        if (enrol.getModule().size() > 0) {
          sb.append(getModuleInfo(enrol.getModule().get(0), enrol.getMember()));
        }
        sb.append("</td></tr></table>");
        break;
    }

    return sb.toString();
  }

  private List<CourseEnrolmentNode> getCourseNodes(List<CourseOrder> co, int idper) throws SQLException {
    List<CourseEnrolmentNode> nodes = new ArrayList<>();
    for (CourseOrder cc : co) {
      int jj = service.getCourseDayMember(cc.getAction(), cc.getDateStart(), idper);
      //auto display end date
      DateFr last = new DateFr(service.getLastSchedule(idper, cc.getId()));
      if (!DateFr.NULLDATE.equals(last.toString()) && !last.equals(cc.getDateEnd())) {
        cc.setDateEnd(last);
        //service.update(cc); // no need to update
      }
      if (cc.getTitle() == null && cc.getAction() == 0) {
        cc.setTitle(MemberEnrolmentEditor.getUndefinedLabel(cc));
      }
      CourseEnrolmentNode n = new CourseEnrolmentNode(cc, jj);
      nodes.add(n);
    }
    return nodes;
  }

  private String getModuleInfo(ModuleOrder mo, int idper) throws SQLException {
    ModuleEnrolmentNode node = new ModuleEnrolmentNode(mo);
    // line below not needed
    //node.setLastScheduledDate(new DateFr(service.getLastScheduleByModuleOrder(idper, mo.getId())));
    if (mo.getTotalTime() > 0) {
      // do not restrict to end date
      node.setCompleted(service.getCompletedTime(idper, mo.getId(), dataCache.getStartOfYear().getDate()));
    }

    if (mo.isStopped()) {
      node.setInfo(" <font color=\"#666666\">" + BundleUtil.getLabel("Module.stopped.label") + " : " + mo.getEnd().toString() + "</font>");
    }
    String nodeInfo = node.toString();
    return nodeInfo.substring(6, nodeInfo.lastIndexOf('<'));

  }

  private String getActions(List<CourseOrder> co) {
    List<Integer> actions = new ArrayList<>();
    for (CourseOrder c : co) {
      actions.add(c.getAction());
    }
    StringBuilder sb = new StringBuilder();
    if (actions.isEmpty()) {
      sb.append("-1");
      return sb.toString();
    }
    for (int a : actions) {
      sb.append(a).append(',');
    }
    sb.deleteCharAt(sb.length() - 1);
    return sb.toString();
  }

  private String addCss() {
    return " @page { margin: 1cm } table {page-break-after: always;} tbody tr td:nth-child(4) {min-width: 70pt;height: 20pt}";
  }

  private String getCss(SigningSheetType type) {
    String defCss = " body {font-family: Arial, Helvetica, sans-serif;font-size: 1em}"
      + " table {width: 100%;border-spacing: 0;border-collapse: collapse;border:1px solid Gray;font-size: 0.8em}"
      + " td, th { border-left: 1px solid Gray;text-align :left }"
      + " tbody td, tbody th { border-bottom: 1px solid LightGray}"
      + " tbody tr:last-child td {border-bottom: 1px solid Gray}"
      + " thead td, thead th, tfoot td, tfoot th { border-bottom: 1px solid Gray}"
      + " tbody tr:nth-child(even) {background-color: #E6E6E6 !important}"
      + " tbody tr:nth-child(odd) {background-color: #FFF}"
      + " body  ul li {font-weight: bold}"
      + " body  ul ul li {font-weight: normal}"
      + " h1 {font-size: 1.2em}"
      + " h2 {font-size : 1.1em}"
      + " ul {font-size: 0.9em;line-height: 1.4em}"
      + " h1, h2 {background-color: #CCC !important}"
      + " tbody tr td:nth-child(4) {min-width: 70pt;height: 20pt} @page { margin: 1cm } table {page-break-after: always;} ";
    String amPmCss = " body {font-family: Arial, Helvetica, sans-serif;font-size: 1em}"
      + " table {border-spacing: 0;border-collapse: collapse;font-size: 0.8em}"
      + " td, th { border-left: 1px solid Gray; }"
      + " thead th, thead td {text-align: center;vertical-align: top}"
      + " thead td, thead th, tfoot td, tfoot th { border-bottom: 1px solid Gray}"
      + " tbody td, tbody th { border-bottom: 1px solid LightGray; text-align: center}"
      + " tbody tr:nth-child(even) {background-color: #E6E6E6 !important}"
      + " tbody tr:nth-child(odd) {background-color: #FFF}"
      + " tbody tr td:nth-child(1),tbody tr td:nth-child(4),tbody tr td:nth-child(7) {width: 20%}"
      + " tfoot tr td {border-top: 1px solid #222;text-align: right}"
      + " table.top {width:80%;margin:0;padding:0;}"
      + " table.top td, table.top th {border:0;vertical-align: bottom}"
      + " table.top td:first-child {text-align: center;width:50%}"
      + " table.top td:last-child {text-align: right;width:30%}"
      + " table.content {width:100%;border:1px solid Gray;page-break-after: always;}"
      + " .logo {float:left;vertical-align: bottom;margin-bottom: 1em}"
      + " .banner {text-align: center;}"
      + " @page { margin: 1cm } @media print {.banner {position: fixed;bottom: 0;left:1cm}";
    switch (type) {
      case FULL:
        return defCss;
      case AM_PM:
        return amPmCss;
      default:
        return "";
    }
  }


   private enum SigningSheetType {
     FULL,AM_PM
   }


}
