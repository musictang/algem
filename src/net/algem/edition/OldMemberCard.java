/*
 * @(#)OldMemberCard.java	2.8.w 08/07/14
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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import javax.imageio.ImageIO;
import net.algem.contact.*;
import net.algem.contact.member.Member;
import net.algem.course.Course;
import net.algem.course.CourseCodeType;
import net.algem.course.CourseIO;
import net.algem.enrolment.CourseOrder;
import net.algem.enrolment.Enrolment;
import net.algem.enrolment.EnrolmentIO;
import net.algem.planning.PlanningService;
import net.algem.util.DataCache;
import net.algem.util.ImageUtil;
import net.algem.util.model.Model;
import net.algem.util.module.GemDesktop;

/**
 * Canvas for old member card.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 * @deprecated
 *
 */
public class OldMemberCard
        extends Canvas {

  int margeh = 30;
  int marged = 50;
  int th;
  FontMetrics fm;
  Image bim;
  Graphics bg;
  Font bigFont;
  Font normalFont;
  Font smallFont;
  Font boldFont;
  Frame parent;
  PersonFile dossier;
  Toolkit tk;
  Properties props = new Properties();
  BufferedImage photo;
  private GemDesktop desktop;

  public OldMemberCard(GemDesktop desktop, PersonFile _dossier) {
    this.desktop = desktop;
    parent = desktop.getFrame();
    dossier = _dossier;

    tk = Toolkit.getDefaultToolkit();

    props.put("awt.print.paperSize", "a4");

    bigFont = new Font("Helvetica", Font.PLAIN, 10);
    normalFont = new Font("TimesRoman", Font.PLAIN, 10);
    smallFont = new Font("Helvetica", Font.PLAIN, 8);
  }

  public void edit(DataCache cache) {
    int mgh = 15;
    int mpl = 270;
    int mgm = 485;
    Address adr = null;
    Vector tel = null;
    MemberCardService service = new MemberCardService(desktop);

    PrintJob prn = tk.getPrintJob(parent, "Carte adhérent", props);
    if (prn == null) {
      return;
    }

    Dimension d = prn.getPageDimension();
    //System.out.println("h:"+d.height+" w:"+d.width);
    //System.out.println("resolution:"+prn.getPageResolution());
    Graphics g = prn.getGraphics();

    g.setColor(Color.black);
    //g.drawRect(20, 20, 320, 120);

    /*
     * CARTE ADHERENT HAUT
     */
    Contact adh = dossier.getContact();

    // marge droite info adherent
    int xadhinfo = 160;

    Member member = dossier.getMember();
    URL url_photo = getPhotoPath(adh.getId());
    photo = getPhoto(url_photo);

    g.setFont(normalFont);
    //g.drawString("",xadhinfo, 40);

    if (photo != null) {
      g.drawImage(photo, 20, 28, this);
    }

    g.drawString("Carte d'adhérent " + String.valueOf(adh.getId()), xadhinfo - 30, 35);
    //g.setFont(bigFont);
    g.drawString(adh.getGender() + " " + adh.getFirstName() + " " + adh.getName(), xadhinfo - 30, 60);
    //g.setFont(bigFont);
    g.drawString(cache.getInstrumentName(member.getFirstInstrument()), xadhinfo - 30, 125);

    adr = adh.getAddress();
    try {
      if (adr == null && member.getPayer() != adh.getId()) {
        Vector<Address> v = AddressIO.findId(member.getPayer(), DataCache.getDataConnection());
        if (v != null && v.size() > 0) {
          adr = (Address) v.elementAt(0);
        }
      }
      tel = adh.getTele();
      if (tel == null && member.getPayer() != adh.getId()) {
        tel = TeleIO.findId(member.getPayer(), DataCache.getDataConnection());
      }
    } catch (SQLException ex) {
      System.err.println(ex.getMessage());
    }
    if (adr != null) {
      //g.setFont(bigFont);
      g.drawString(adr.getAdr1(), xadhinfo - 30, 75);
      g.drawString(adr.getAdr2(), xadhinfo - 30, 85);
      g.drawString(adr.getCdp() + " " + adr.getCity(), xadhinfo - 30, 95);
    }


    /*
     * PLANNING
     */
    g.setFont(normalFont);
    String where = "WHERE adh=" + dossier.getId() + " AND creation >= '01-07-" + cache.getStartOfYear().getYear() + "'";
    System.out.println(where);
    Vector<Enrolment> v = null;
    try {
      v = EnrolmentIO.find(where, DataCache.getDataConnection()); // recherche des inscriptions
    } catch (SQLException ex) {
      System.err.println(ex.getMessage());
    }
    if (v.size() > 0) {
      Enrolment ins = (Enrolment) v.elementAt(0);
      Vector cmc = ins.getCourseOrder();// recherche des commande_cours
      int cpt1, cpt2, cpt3, cpt4;
      cpt1 = cpt2 = cpt3 = cpt4 = 0;

      String[] journom = PlanningService.WEEK_DAYS;
      Enumeration enu = cmc.elements();
      while (enu.hasMoreElements()) {
        CourseOrder cc = (CourseOrder) enu.nextElement();
        // Récupération du jour correspondant à chaque cours
        Course c = null;
        try {
          c = ((CourseIO) DataCache.getDao(Model.Course)).findId(cc.getAction());

          int[] infos = service.getInfos(cc.getId(), adh.getId());
          int jj = infos[1];
          if (jj == 0) {
            jj = 1;
          }
          String lib = c.getLabel() + ", le " + journom[jj] + ", à " + cc.getStart() + " h";
          if (c.getCode() == CourseCodeType.INS.getId()) {
            g.drawString(lib, 115, mpl + 0 + cpt1);
            cpt1 += 10;
//          } else if (c.getCode().startsWith("AT")
//                  || c.getCode().bufferEquals("Evei")) {
//            g.drawString(lib, 115, mpl + 40 + cpt2);
//            cpt2 += 10;
//          } else if (c.getCode().bufferEquals("F.M.")) {
//            g.drawString(lib, 115, mpl + 80 + cpt3);
//            cpt3 += 10;
//          } else if (c.getCode().bufferEquals("B.B.")
//                  || c.getCode().bufferEquals("AcRe")) {
//            g.drawString(lib, 115, mpl + 120 + cpt4);
            cpt4 += 10;
          }
        } catch (SQLException ex) {
          System.err.println(getClass().getName() + "#edite :" + ex.getMessage());
        }
      }
    }

    /*
     * CARTE MUSTANG BAS
     */
    //int x = 160;
    if (photo != null) {
      g.drawImage(photo, 20, mgm - 7, this);
    }

    g.setFont(normalFont);
    //int yoffset = g.getFontMetrics().getDescent();
    g.drawString(String.valueOf(adh.getId()), xadhinfo, mgm);//correction marge droite
    //g.drawString(f.getBirth().toString(), xadhinfo + 100, mgm);//correction marge droite
    g.drawString(adh.getGender() + " " + adh.getFirstName() + " " + adh.getName(), xadhinfo, mgm + 20);

    if (adr != null) {
      g.setFont(normalFont);
      g.drawString(adr.getAdr1(), xadhinfo, mgm + 35);
      g.drawString(adr.getAdr2(), xadhinfo, mgm + 45);
      g.drawString(adr.getCdp() + " " + adr.getCity(), xadhinfo, mgm + 55);
    }
    //g.drawString("Tel",x,mgm+85);
    if (tel != null && tel.size() > 0) {
      Telephone t = (Telephone) tel.elementAt(0);
      g.setFont(normalFont);
      g.drawString(t.getTypeTel() + " : " + t.getNumber(), xadhinfo, mgm + 70);
    }
    if (tel != null && tel.size() > 1) {
      Telephone t = (Telephone) tel.elementAt(1);
      g.setFont(normalFont);
      g.drawString(t.getTypeTel() + " : " + t.getNumber(), xadhinfo, mgm + 80);
    }

    g.drawString(cache.getInstrumentName(member.getFirstInstrument()), xadhinfo, mgm + 95);

    g.dispose();
    prn.end();
  }

  private BufferedImage getPhoto(URL url) {
    BufferedImage img = null;
    try {
      img = ImageIO.read(new File(url.getPath()));
      // recadrer si nécessaire
      if (ImageUtil.PHOTO_ID_HEIGHT != img.getHeight()) {
        System.out.println("rescaling !");
        BufferedImage bi2 = ImageUtil.rescale(img);
        img = ImageUtil.formatPhoto(bi2);
      }
    } catch (Exception ex) {
    }
    return img;
  }

  private URL getPhotoPath(int id) {
    String photo_path = ImageUtil.PHOTO_PATH + id + ".jpg";
    return getClass().getResource(photo_path);
  }
}
