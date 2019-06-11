/*
 * @(#) SigningSheetCtrl.java Algem 2.15.6 29/11/17
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import javax.swing.SwingWorker;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.contact.Person;
import net.algem.contact.member.MemberService;
import net.algem.planning.DateFr;
import net.algem.planning.ScheduleRangeObject;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.FileUtil;
import net.algem.util.GemLogger;
import net.algem.util.jdesktop.DesktopBrowseHandler;
import net.algem.util.jdesktop.DesktopHandlerException;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;
import org.apache.commons.codec.binary.Base64;
import org.apache.poi.util.IOUtils;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.6
 * @since 2.10.0 01/06/16
 */
public class SigningSheetCtrl
        extends SwingWorker<Void, Void>
{

  private final EnrolmentService service;
  private final DataCache dataCache;
  private final MemberService memberService;
  private File temp;
  private PrintWriter pw;
  private SigningSheetView dlg;
  private final DateFormat dateFormat = new SimpleDateFormat("MMM yyyy");
  //private Company company;

  public SigningSheetCtrl(GemDesktop desktop) {
    this.dataCache = desktop.getDataCache();
    this.service = new EnrolmentService(dataCache);
    DataConnection dc = DataCache.getDataConnection();
    this.memberService = new MemberService(dc);
    /*try {
      company = new OrganizationIO(dc).getDefault();
    } catch (SQLException ex) {
      GemLogger.log(ex.getMessage());
    }*/
    this.dlg = new SigningSheetView(desktop.getFrame(), true);
    //dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
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
      SigningSheetType sheetType = dlg.getPageModel();
      pw.println(FileUtil.getHtmlHeader(BundleUtil.getLabel("Enrolment.label"), getCss(sheetType)));

      DateFr from = new DateFr(ConfigUtil.getConf(ConfigKey.PRE_ENROLMENT_START_DATE.getKey()));
      DateFr to = new DateFr(ConfigUtil.getConf(ConfigKey.END_YEAR.getKey()));
      List<Enrolment> all = service.getProEnrolments(from.getDate(), to.getDate());
      DateFr start = dlg.getPeriod().getStart();
      DateFr end = dlg.getPeriod().getEnd();
      String headerImgData = getBase64StringFromImgDataSource("emargement-header.png");
      //String headerImgData = ImageUtil.getLogoAsBase64(company);
      String footerImgData = getBase64StringFromImgDataSource("emargement-footer.png");
      //String companyName = company != null ? company.getOrg().getName() : "";
      Comparator<ScheduleRangeObject> dateComparator = new Comparator<ScheduleRangeObject>() {
        @Override
        public int compare(ScheduleRangeObject o1, ScheduleRangeObject o2) {
          if (o1.getDate().before(o2.getDate())) {
            return -1;
          } else if (o1.getDate().after(o2.getDate())) {
            return 1;
          } else {
            return 0;
          }
        }

      };
      for (int i = 0, len = all.size(); i < len; i++) {
        Enrolment e = all.get(i);
        List<ScheduleRangeObject> ranges = MemberEnrolmentEditor.getActivityRanges(e.getMember(), start, end, getActions(e.getCourseOrder()), memberService);

        if (SigningSheetType.STANDARD.equals(sheetType)) {
          pw.println(getEnrolmentInfo(e, SigningSheetType.STANDARD, start));
          pw.println(MemberEnrolmentEditor.fillActivityFull(ranges));
          //pw.println("<div class=\"pageBreak\"></div>");
        } else {
          // maybe include rehearsals
          boolean individual = dlg.withIndividualRehearsals();
          boolean group = dlg.withGroupRehearsals();
          if (individual || group) {
            List<ScheduleRangeObject> rehearsals = MemberEnrolmentEditor.completeActivityRanges(e.getMember(),start, end, memberService,individual, group);
            if (rehearsals.size() > 0) {
              ranges.addAll(rehearsals);
              Collections.sort(ranges, dateComparator);
            }
          }
          pw.print("<header>");
          if (!headerImgData.isEmpty()) {
            pw.print("<img class=\"logo\" src=\"data:image/png;base64," + headerImgData + "\" width=100 />");
          }
          pw.print(getEnrolmentInfo(e, SigningSheetType.DETAILED, start));
          pw.println("</header>");
          pw.println(MemberEnrolmentEditor.fillActivityAMPM(ranges, dlg.getOptions()));
          if (!footerImgData.isEmpty()) {
            pw.println("<div class=\"banner\">");
            pw.println("<img src=\"data:image/png;base64," + footerImgData + "\" width=\"620\"/></div>");
          }
        }
//        pw.println(MemberEnrolmentEditor.catchActivity(e.getMember(), start, end, getActions(e.getCourseOrder()), memberService));
        int p = (i + 1) * 100 / len;
        setProgress(p);
      }
      pw.println("</body></html>");
    } catch (SQLException | IOException ex) {
      GemLogger.log(ex.getMessage());
    }
    return null;
  }

  /**
   * Converts image bytes to base64-string.
   *
   * @param fileName simple name of the image
   * @return a string
   */
  private String getBase64StringFromImgDataSource(String fileName) {
    try {
      InputStream in = getClass().getResourceAsStream("/resources/img/" + fileName);
      if (in == null) {
        return "";
      }
      return Base64.encodeBase64String(IOUtils.toByteArray(in));
    } catch (Exception ex) {
      GemLogger.log(Level.WARNING, ex.getMessage());
      return "";
    }

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
      case STANDARD:
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
      case DETAILED:
        sb.append("<table class=\"top\"><tr><td><h1>").append(dateFormat.format(from.getDate())).append("</h1></td><td>").append(BundleUtil.getLabel("Signing.sheet.center.label")).append("<br />");
        sb.append("<b>").append(p.getFirstnameName()).append("</b><br />");
        if (enrol.getModule().size() > 0) {
          sb.append("<b>").append(enrol.getModule().get(0).getTitle()).append("</b>");
//          sb.append(getModuleInfo(enrol.getModule().get(0), enrol.getMember()));
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
    String amPmCss = "body {font-family: Arial, Helvetica, sans-serif;font-size: 1em}"
            + " table {border-spacing: 0;border-collapse: collapse;font-size: 0.8em}"
            + " td, th { border-left: 1px solid #888; }"
            + " thead th, thead td {text-align: center;vertical-align: top}"
            + " thead td, thead th, tfoot td, tfoot th { border-bottom: 1px solid #ccc}"
            + " tbody td, tbody th { border-bottom: 1px solid #ccc; text-align: center}"
            + " tfoot tr th, tfoot tr td{border-top: 2px solid #888;text-align: center}"
            + " table.top {width:80%;margin:0;padding:0;}"
            + " table.top td, table.top th {border:0;vertical-align: bottom}"
            + " table.top td:first-child {text-align: center;width:50%}"
            + " table.top td:last-child {text-align: right;width:30%}"
            + " table.content {width:100%;border:2px solid #888;page-break-after: always;}"
            + " table.content tbody tr {height: 2.2em}"
            + " table.content tbody tr:first-child td {border-top: 2px solid #888;}"
            + " table.content tbody tr td:nth-child(1),tbody tr td:nth-child(4),tbody tr td:nth-child(7) {width: 22%}"
      + " table.content tbody tr td:nth-child(2),tbody tr td:nth-child(3),tbody tr td:nth-child(5), tbody tr td:nth-child(6) {width: 8%}"
            + " th.signature {text-align: left;height: 3em;padding-left: 1em}"
            + " textarea {border: 1px solid #ddd}"
            + " .total {font-family: monospace,sans-serif;text-align: right}"
            + " .logo {float:left;vertical-align: bottom;margin-bottom: 1em}"
            + " .banner {text-align: center;}"
            + " @page { margin: 1cm } @media print {.banner {position: fixed;bottom: 0;left:1cm}";
    switch (type) {
      case STANDARD:
        return defCss;
      case DETAILED:
        return amPmCss;
      default:
        return "";
    }
  }

  enum SigningSheetType
  {
    STANDARD, DETAILED
  }

}
