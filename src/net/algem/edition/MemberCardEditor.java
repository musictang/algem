/*
 * @(#)MemberCardEditor.java 2.13.3 18/05/17
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
package net.algem.edition;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import net.algem.config.ConfigKey;
import net.algem.contact.Address;
import net.algem.contact.Contact;
import net.algem.contact.PersonFile;
import net.algem.contact.Telephone;
import net.algem.contact.member.Member;
import net.algem.planning.Hour;
import net.algem.util.BundleUtil;
import net.algem.util.GemLogger;
import net.algem.util.ImageUtil;
import net.algem.util.module.GemDesktop;

/**
 * Member card editor.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.13.3
 * @since 2.2.p
 */
public class MemberCardEditor implements Printable {

  static Stroke DASHED = new BasicStroke(0.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1.0f, new float[]{5.0f}, 0.0f);
  static Stroke LINE = new BasicStroke();

  static int COURSE_LIST_X = ImageUtil.mmToPoints(50);//50mm
  private static final int MARGIN = 5; // marge en mm
  private static final int WIDTH = 210; // largeur par défaut en mm
  private static final int HEIGHT = 297;// hauteur par défaut en mm

  private static int TOP_CUTTING_MARK_Y = ImageUtil.mmToPoints(96);//95mm +1 à cause de la perte de précision entière
  private static int BOTTOM_CUTTING_MARK_Y = ImageUtil.mmToPoints(HEIGHT - 76f);//75mm

  private PersonFile dossier;
  private List<DrawableElement> blocks;
  private MemberCardService service;
  private Contact contact;
  private Member member;
  private Address address;
  private List<Telephone> tels;
  private BufferedImage photo;
  private List<PlanningInfo> infos;

  public MemberCardEditor(GemDesktop desktop, PersonFile dp) {
    dossier = dp;
    blocks = new ArrayList<DrawableElement>();
    service = new MemberCardService(desktop);
    load();
  }

  public void edit() {
    PrinterJob job = PrinterJob.getPrinterJob();
    PrintRequestAttributeSet attSet = new HashPrintRequestAttributeSet();
    attSet.add(MediaSizeName.ISO_A4);
    attSet.add(OrientationRequested.PORTRAIT);
    MediaPrintableArea printableArea
      = new MediaPrintableArea(MARGIN, MARGIN, WIDTH - (MARGIN * 2), HEIGHT - (MARGIN * 2), MediaSize.MM);
    attSet.add(printableArea);

    job.setPrintable(this);

    if (job.printDialog()) {
      try {
        job.print(attSet);
      } catch (PrinterException e) {
        // The job did not successfully complete
        GemLogger.log("MemberCardEditor#edit[PrinterException] : " + e.getMessage());
      }
    }

  }

  @Override
  public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
    if (pageIndex > 0) {
      // We have only one page, and 'page' is zero-based
      return NO_SUCH_PAGE;
    }
    Graphics2D g2d = (Graphics2D) g;
    g2d.setStroke(DASHED);
    g2d.drawLine(-ImageUtil.mmToPoints(MARGIN), TOP_CUTTING_MARK_Y, (int) pf.getWidth(), TOP_CUTTING_MARK_Y);
    g2d.drawLine(-ImageUtil.mmToPoints(MARGIN), BOTTOM_CUTTING_MARK_Y, (int) pf.getWidth(), BOTTOM_CUTTING_MARK_Y);

    g2d.setStroke(LINE);
    g2d.translate(pf.getImageableX(), pf.getImageableY());
    // only for test
    //int pageW = (int) pf.getImageableWidth();
    //int pageH = (int) pf.getImageableHeight();
    //Rectangle2D border = new Rectangle(0, 0, pageW, pageH);
    //g2d.draw(border);
    // dessin impression
    addTop(pf, 35);
    addBottom(pf, 5);
    for (DrawableElement e : blocks) {
      e.draw(g);
    }
    blocks.clear();
    return PAGE_EXISTS;

  }

  private void load() {
    contact = dossier.getContact();
    member = dossier.getMember();
    try {
      address = service.getAddress(dossier);
      tels = service.getTels(dossier);
      photo = service.getPhoto(dossier.getId());
    } catch (IOException | SQLException ex) {
      GemLogger.logException(ex);
    }
    infos = service.getPlanningInfo(dossier);
    Collections.sort(infos, new Comparator<PlanningInfo>() {
      @Override
      public int compare(PlanningInfo o1, PlanningInfo o2) {
        Integer d1 = o1.getDow();
        Integer d2 = o2.getDow();
        int c = d1.compareTo(d2);
        if (c == 0) {
          Hour s1 = new Hour(o1.getStart());
          Hour s2 = new Hour(o2.getStart());
          if (s1.after(s2)) {
            c = 1;
          } else if (s1.before(s2)) {
            c = -1;
          }
        }
        return c;
      }

    });
  }

  private void addTop(PageFormat pf, int vOffset) {//35
    int x = 0;
    int y = 0;

    // titre
    String start = service.getBeginningYear();
    String end = service.getEndYear();
    String firmName = service.getConf(ConfigKey.ORGANIZATION_NAME.getKey());
    String args[] = {firmName, start, end};
    String title = BundleUtil.getLabel("Member.card.title.label", args).toUpperCase();
    double headX = (pf.getWidth() / 2);
    TitleElement titleElement = new TitleElement(title, 0, y);
    titleElement.setCenter(headX);
    addBlock(titleElement);
    // identité
    if (contact != null) {
      addBlock(new IdentityElement(contact, x, y + vOffset));
    }

    // instrument
    addBlock(new InstrumentElement(service.getInstrument(member), x, y + vOffset + 10));
    // adresse
    if (address != null) {
      addBlock(new AddressElement(address, x, y + vOffset + 25));
    }
    // telephones
    if (tels != null) {
      for (int i = 0, yoffset = vOffset + 65; i < tels.size(); i++, yoffset += 10) {
        addBlock(new TelElement(tels.get(i), x, y + yoffset));
      }
    }
    //id
    addBlock(new IdElement(dossier.getId(), x, vOffset + 105));
    //photo
    addBlock(new PhotoElement(photo, x, vOffset + 110));//
    // cours
    int yoffset = y + vOffset;
    if (infos != null) {
      for (int i = 0; i < infos.size(); i++, yoffset += 10) {
        addBlock(new PlanningElement(infos.get(i), COURSE_LIST_X, y + yoffset));
      }
    }
    // signature
    int topSignPos = vOffset + 140;
    if (yoffset + 10 < topSignPos) {
      addBlock(new SignatureElement(COURSE_LIST_X, topSignPos));
    } else {
      addBlock(new SignatureElement(COURSE_LIST_X, yoffset + 10));
    }

  }

  private void addBottom(PageFormat pf, int vOffset) {
    int x = 0;
    int y = BOTTOM_CUTTING_MARK_Y;

    // identité
    if (contact != null) {
      addBlock(new IdentityElement(contact, x, y + vOffset));
    }

    // instrument
    addBlock(new InstrumentElement(service.getInstrument(member), x, y + vOffset + 10));
    // adresse
    if (address != null) {
      addBlock(new AddressElement(address, x, y + vOffset + 25));
    }
    // cours
    int yoffset = vOffset;
    for (int i = 0; i < infos.size(); i++, yoffset += 10) {
      addBlock(new PlanningElement(infos.get(i), COURSE_LIST_X, y + yoffset));
    }
    // signature
    int bottomPos = (int) pf.getHeight() - 65;
    if (y + yoffset < bottomPos) {
      addBlock(new AccessElement(COURSE_LIST_X, bottomPos));
    } else {
      addBlock(new AccessElement(COURSE_LIST_X, y + yoffset));
    }
    //id
    addBlock(new IdElement(dossier.getId(), x, y + vOffset + 65));
    //photo
    addBlock(new PhotoElement(photo, x, y + vOffset + 70));//

  }

  private void addBlock(DrawableElement element) {
    blocks.add(element);
  }
}
