/*
 * @(#) SigningSheetCtrl.java Algem 2.10.0 15/06/16
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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingWorker;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.contact.Person;
import net.algem.contact.member.MemberService;
import static net.algem.enrolment.MemberEnrolmentEditor.getCss;
import net.algem.planning.DateFr;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.FileUtil;
import net.algem.util.GemLogger;
import net.algem.util.jdesktop.DesktopBrowseHandler;
import net.algem.util.jdesktop.DesktopHandlerException;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.10.0
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
      pw.println(FileUtil.getHtmlHeader(BundleUtil.getLabel("Enrolment.label"), getCss() + addCss()));

      DateFr from = new DateFr(ConfigUtil.getConf(ConfigKey.PRE_ENROLMENT_START_DATE.getKey()));
      DateFr to = new DateFr(ConfigUtil.getConf(ConfigKey.END_YEAR.getKey()));
      List<Enrolment> all = service.getProEnrolments(from.getDate(), to.getDate());
      DateFr start = dlg.getPeriod().getStart();
      DateFr end = dlg.getPeriod().getEnd();
      for (int i = 0, len = all.size(); i < len; i++) {
        Enrolment e = all.get(i);
        pw.println(getEnrolmentInfo(e));
        pw.println(MemberEnrolmentEditor.catchActivity(e.getMember(), start, end, getActions(e.getCourseOrder()), memberService));
        pw.println("<div class=\"pageBreak\"></div>");
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

  private String getEnrolmentInfo(Enrolment enrol) throws SQLException {
    StringBuilder sb = new StringBuilder();
    Person p = (Person) DataCache.findId(enrol.getMember(), Model.Person);
    String nickName = p.getNickName();
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
      node.setInfo(" <font color=\"#666666\">" + BundleUtil.getLabel("Stopped.label") + " : " + mo.getEnd().toString() + "</font>");
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

}
