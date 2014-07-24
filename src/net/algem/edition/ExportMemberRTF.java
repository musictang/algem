/*
 * @(#)ExportMemberRTF.java 2.8.w 08/07/14
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
package net.algem.edition;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import net.algem.contact.Contact;
import net.algem.contact.Email;
import net.algem.contact.PersonFile;
import net.algem.contact.member.Member;
import net.algem.contact.member.MemberService;
import net.algem.course.Course;
import net.algem.enrolment.CourseOrder;
import net.algem.enrolment.Enrolment;
import net.algem.enrolment.EnrolmentService;
import net.algem.enrolment.ModuleOrder;
import net.algem.planning.DateFr;
import net.algem.planning.PlanningService;
import net.algem.planning.ScheduleRangeObject;
import net.algem.util.DataCache;
import net.algem.util.FileUtil;
import net.algem.util.GemLogger;
import net.algem.util.module.GemDesktop;

/**
 * Member file export to RTF.
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 * @since 1.0b 05/03/2002
 */
public class ExportMemberRTF
{

  static String[] dayNames = PlanningService.WEEK_DAYS;
  private DataCache dataCache;
  private PrintWriter out;
  private PersonFile member;
  private int memberId;
  private String path;
  private Vector<CourseOrder> courseOrderList = new Vector<CourseOrder>();
  private EnrolmentService enrolmentService;
  private MemberService memberService;
  private PlanningService planningService;

  public ExportMemberRTF(GemDesktop desktop, String _path, PersonFile m)
          throws FileNotFoundException {
    this(desktop, _path, m.getId());
    member = m;
  }

  public ExportMemberRTF(GemDesktop desktop, String _path, int m) throws FileNotFoundException {
    dataCache = desktop.getDataCache();
    enrolmentService = new EnrolmentService(dataCache);
    memberService = new MemberService(DataCache.getDataConnection());
    planningService = new PlanningService(DataCache.getDataConnection());
    memberId = m;
    path = _path + "/adh" + memberId + ".rtf";

    out = new PrintWriter(new FileOutputStream(path));

    out.println("{\\rtf1\\utf8\\deflang1024 \\deff0{\\fonttbl{\\f0\\froman Times Roman;}{\\f1\\fnil Times;}{\\f2\\fswiss Helvetica;}}{\\colortbl;\\red0\\green0\\blue0;\\red255\\green255\\blue255;}{\\stylesheet{\\sa144\\sl240\\slmult1\\brdrt0\\brdrl0\\brdrr0\\brdrb0\\brdrbtw0 \\f1\\fs24\\cf1 ");
    out.println("\\snext0 Normal;}{\\s1\\sl240\\slmult1\\brdrt0\\brdrl0\\brdrr0\\brdrb0\\brdrbtw0 \\f1\\fs24\\cf1 \\sbasedon0\\snext1 Cellule;}{\\s2\\sa144\\sl240\\slmult1\\tqc\\tx5044\\tqr\\tx9636\\brdrt0\\brdrl0\\brdrr0\\brdrb0\\brdrbtw0 \\f1\\fs24\\cf1 \\sbasedon0\\snext2 EnTetePiedDePage;}{\\s3");
    out.println("\\sa144\\sl240\\slmult1\\brdrt0\\brdrl0\\brdrr0\\brdrb0\\brdrbtw0 \\f1\\fs24\\cf1 \\sbasedon0\\snext3 Paragraphe;}{\\s4\\sa144\\sl240\\slmult1\\brdrt0\\brdrl0\\brdrr0\\brdrb0\\brdrbtw0 \\b\\f1\\fs28\\cf1 \\sbasedon5\\snext4 Sous-titre;}{\\s5\\qc\\sa144\\sl240\\slmult1\\brdrt0\\brdrl0");
    out.println("\\brdrr0\\brdrb0\\brdrbtw0 \\b\\f1\\fs36\\cf1 \\sbasedon0\\snext5 Titre;}}");
    out.println("{\\info{\\title ax11047g.aw}{\\author eric}{\\doccomm Created by Mustang v4.3 RTF Export}}");
    out.println("\\paperw11904\\paperh16836\\margl1133\\margr1133\\margt1416\\margb849\\widowctrl\\ftnbj\\sectd\\marglsxn1133\\margrsxn1133\\margtsxn1416\\margbsxn849\\pgwsxn11904\\pghsxn16836\\pghsxn16836\\sbknone\\headery708\\footery849\\endnhere{\\footer \\pard\\plain \\s2\\sa144\\slmult1");
    DateFr today = new DateFr(new java.util.Date());
    out.println("\\tqc\\tx4534\\tqr\\tx8503 \\f1\\fs24\\cf1 adherent 13251\\tab page {\\field{\\fldinst PAGE}{\\fldrslt}}/{\\field{\\fldinst NUMPAGES}{\\fldrslt 1}}\\tab " + today);

  }

  public String getPath() {
    return path;
  }

  public void edit() {
    edit(new DateFr("01-01-1900"), new DateFr("31-12-2999"));
  }

  public void edit(DateFr start, DateFr end) {

    Vector<Email> emails = null;

    if (member == null) {
      member = memberService.find(memberId);
      if (member == null) {
        return;
      }
    }

    Contact contact = member.getContact();
    out.println("\\par }\\pard\\plain \\s3\\qc\\sa144\\slmult1\\brdrt0\\brdrl0\\brdrr0\\brdrb0\\brdrbtw0\\shading6000\\cfpat1\\cbpat2 \\f1\\fs24\\cf1 {\\b\\f2\\fs36 Dossier Adh\\'e9rent n\\'b0 " + memberId + "}");

    out.println("\\par \\pard\\plain \\s3\\slmult1 \\f1\\fs24\\cf1 {\\b\\fs28 " + FileUtil.rtfReplaceChars(contact.getFirstName()) + " " + FileUtil.rtfReplaceChars(contact.getName()) + "}");
    // Ne pas faire figurer l'adresse
		/*
    adr = contact.getAddress();
    if (adr == null && adh.getMember().getPayer() != adh.getIdOrder()) {
    Vector v = AdresseIO.findId(dataCache, adh.getMember().getPayer());
    if (v != null && v.size() > 0)
    adr = (Adresse)v.elementAt(0);
    }
    if (adr != null) {
    out.println("\\par "+adr.getAdr1()+","+adr.getAdr2()+","+adr.getCdp()+" "+adr.getVille());
    }*/

    // Ne pas faire figurer le téléphone
		/*tel = contact.getTele();
    if (tel == null && adh.getMember().getPayer() != adh.getIdOrder())
    tel = TeleIO.findId(dataCache, adh.getMember().getPayer());

    if (tel != null) {
    out.print("\\par ");
    for (int i=0; i < tel.size(); i++) {
    Tele t = (Tele)tel.elementAt(i);
    out.print(t.getTypeTel()+" : "+t.getNumero()+"  ");
    }
    out.println();
    }*/
    emails = contact.getEmail();
    if (emails != null) {
      for (Email e : emails) {
        out.print("\\par Mail : " + e.getEmail());
        out.println();
      }
    }

    out.println();
    Member fiche = member.getMember();
    out.println("\\par N\\'e9 le : " + fiche.getBirth() + " - pratique " + fiche.getPractice() + " - niveau " + fiche.getLevel());
    out.println("\\par Instruments : ");
    for (Integer i : fiche.getInstruments()) {
      out.println(dataCache.getInstrumentName(i));
    }
    out.println("\\par ");


    /* PLANNING */
    //out.println("\\par \\pard\\plain \\s3\\qc\\sa144\\slmult1 \\f1\\fs24\\cf1 {\\b\\f2\\fs28 P\\'e9riode du "+debut+" au "+fin+"}");
    out.println("\\par \\pard\\plain \\s3\\slmult1 \\f1\\fs24\\cf1");

    Vector<Enrolment> ins = null;
    try {
      ins = memberService.getEnrolments(member.getId(), start, end);
    } catch (SQLException ex) {
      GemLogger.logException(ex);
    }
    if (ins != null && ins.size() > 0) {
      editEnrolment(ins);
    }

    out.println("\\par \\pard\\plain \\s3\\sa144\\slmult1 \\f1\\fs24\\cf1 ");
    out.println("\\par \\pard\\plain \\s3\\qc\\sa144\\slmult1 \\f1\\fs24\\cf1 {\\b\\f2\\fs28 Suivi p\\'e9dagogique}");

    List<Course> courseList = getCourseOrderList(courseOrderList);
    if (!courseList.isEmpty()) {
      out.println(getFollowUp(member, courseList));
    } else {
      out.print("\n\\par }");
    }
    out.close();
  }

  void editEnrolment(Vector<Enrolment> ins) {

    for (int j = 0; j < ins.size(); j++) {
      Enrolment i = ins.elementAt(j);
      out.println("\\par Inscription " + i.getId() + " du " + i.getOrder().getCreation());
      Enumeration<ModuleOrder> enu = i.getModule().elements();
      while (enu.hasMoreElements()) {
        try {
          ModuleOrder cm = enu.nextElement();
          out.println("\\par \\tab Module " + cm.getTitle());
          Vector<CourseOrder> v = enrolmentService.getCourseOrder(i.getId(), cm.getId());
          for (CourseOrder cc : v) {
            cc.setDay(enrolmentService.getCourseDayMember(cc.getAction(), cc.getDateStart(), i.getMember()));
            courseOrderList.addElement(cc);
            out.println("\\par \\tab\\tab Cours " + cc.getTitle() + " " + dayNames[cc.getDay()] + " " + cc.getStart() + "-" + cc.getEnd());
          }
        } catch (SQLException ex) {
          GemLogger.log(getClass().getName()+"#editeInscription "+ex.getMessage());
        }
      }
    }
  }

  private List<Course> getCourseOrderList(Vector<CourseOrder> listeCC) {
    List<Course> tri = new ArrayList<Course>();
    for (CourseOrder cc : listeCC) {
      try {
        Course c = planningService.getCourseFromAction(cc.getAction());
        if (!tri.contains(c) && c != null && !c.isUndefined()) {
          tri.add(c);
        }
      } catch(SQLException e) {
        GemLogger.logException(e);
      }
    }
    return tri;
  }

  private String getFollowUp(PersonFile member, List<Course> sortList) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < sortList.size(); i++) {
      try {
        Course c = sortList.get(i);
        sb.append("\n\\par \\pard\\plain \\s3\\slmult1 \\f1\\fs24\\cf1 Cours ").append(c.getTitle());
        //out.println("\\par \\pard\\plain \\s3\\sa144\\slmult1 \\f1\\fs24\\cf1 ");
        Vector<ScheduleRangeObject> v = new Vector<ScheduleRangeObject>();
        if (c.isCollective()) {
          v = memberService.findFollowUp(member.getId(), c.getId(), true);
        } else {
          v = memberService.findFollowUp(member.getId(), c.getId(), false);
        }

        for (int j = 0; j < v.size(); j++) {
          ScheduleRangeObject p = v.elementAt(j);

          String s = p.getFollowUp();
          if (s != null) {
            s = s.replace("\n", " - ").trim();

            s = FileUtil.rtfReplaceChars(s);
            if (s.length() > 0) {
              sb.append("\n\\par \\tab ").append(p.getDate()).append(" : ").append(s);
            }
          }
        }
      } catch (SQLException sqe) {
        GemLogger.logException(sqe);
      }
      sb.append("\n\\par \\pard\\plain \\s3\\slmult1 \\f1\\fs24\\cf1 ");
    }

    sb.append("\n\\par ");
    sb.append("\n\\par }");

    return sb.toString();
  }
}

