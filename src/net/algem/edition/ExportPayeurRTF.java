/*
 * @(#)ExportPayeurRTF.java	2.7.a 26/11/12
 *
 * Copyright (c) 2001-2012 Musiques Tangentes. All Rights Reserved.
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
import java.text.NumberFormat;
import java.util.Vector;
import net.algem.accounting.GemAmount;
import net.algem.accounting.OrderLine;
import net.algem.accounting.OrderLineIO;
import net.algem.contact.*;
import net.algem.planning.DateFr;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.FileUtil;
import net.algem.util.model.Model;

/**
 * Export payer infos to RTF.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 */
public class ExportPayeurRTF
{

  private NumberFormat nf;
  private DataConnection dc;
  private PrintWriter out;
  private PersonFile payer;
  private int payerId;
  private String path;

  public ExportPayeurRTF(String _path, PersonFile p, DataConnection dc)
          throws FileNotFoundException {
    this(_path, p.getId(), dc);
    payer = p;
  }

  public ExportPayeurRTF(String _path, int p, DataConnection dc)
          throws FileNotFoundException {
    this.dc = dc;
    payerId = p;
    path = _path + "/payeur" + payerId + ".rtf";

    out = new PrintWriter(new FileOutputStream(path));

    out.println("{\\rtf1\\ansi\\deflang1024\\deff0{\\fonttbl{\\f0\\froman Times Roman;}{\\f1\\fnil Times;}{\\f2\\fswiss Helvetica;}}{\\colortbl;\\red0\\green0\\blue0;\\red255\\green255\\blue255;}{\\stylesheet{\\sa144\\sl240\\slmult1\\brdrt0\\brdrl0\\brdrr0\\brdrb0\\brdrbtw0 \\f1\\fs24\\cf1 ");
    out.println("\\snext0 Normal;}{\\s1\\sl240\\slmult1\\brdrt0\\brdrl0\\brdrr0\\brdrb0\\brdrbtw0 \\f1\\fs24\\cf1 \\sbasedon0\\snext1 Cellule;}{\\s2\\sa144\\sl240\\slmult1\\tqc\\tx5044\\tqr\\tx9636\\brdrt0\\brdrl0\\brdrr0\\brdrb0\\brdrbtw0 \\f1\\fs24\\cf1 \\sbasedon0\\snext2 EnTetePiedDePage;}{\\s3");
    out.println("\\sa144\\sl240\\slmult1\\brdrt0\\brdrl0\\brdrr0\\brdrb0\\brdrbtw0 \\f1\\fs24\\cf1 \\sbasedon0\\snext3 Paragraphe;}{\\s4\\sa144\\sl240\\slmult1\\brdrt0\\brdrl0\\brdrr0\\brdrb0\\brdrbtw0 \\b\\f1\\fs28\\cf1 \\sbasedon5\\snext4 Sous-titre;}{\\s5\\qc\\sa144\\sl240\\slmult1\\brdrt0\\brdrl0");
    out.println("\\brdrr0\\brdrb0\\brdrbtw0 \\b\\f1\\fs36\\cf1 \\sbasedon0\\snext5 Titre;}}");
    out.println("{\\info{\\title ax11047g.aw}{\\author eric}{\\doccomm Created by Mustang v4.3 RTF Export}}");
    out.println("\\paperw11904\\paperh16836\\margl1133\\margr1133\\margt1416\\margb849\\widowctrl\\ftnbj\\sectd\\marglsxn1133\\margrsxn1133\\margtsxn1416\\margbsxn849\\pgwsxn11904\\pghsxn16836\\pghsxn16836\\sbknone\\headery708\\footery849\\endnhere{\\footer \\pard\\plain \\s2\\sa144\\slmult1");
    DateFr today = new DateFr(new java.util.Date());
    out.println("\\tqc\\tx4534\\tqr\\tx8503 \\f1\\fs24\\cf1 payeur " + p + " \\tab page {\\field{\\fldinst PAGE}{\\fldrslt}}/{\\field{\\fldinst NUMPAGES}{\\fldrslt 1}}\\tab " + today);

  }

  public String getPath() {
    return path;
  }

  public void edit() {
    edit(new DateFr("01-01-1900"), new DateFr("31-12-2999"));
  }

  public void edit(DateFr start, DateFr end) {
    //DateFr today = new DateFr(new java.util.Date());
    Address adr = null;
    Vector<Telephone> tel = null;

    if (payer == null) {
      payer = ((PersonFileIO) DataCache.getDao(Model.PersonFile)).findPayer(payerId);
      if (payer == null) {
        return;
      }
    }

    out.println("\\par }\\pard\\plain \\s3\\qc\\sa144\\slmult1\\brdrt0\\brdrl0\\brdrr0\\brdrb0\\brdrbtw0\\shading6000\\cfpat1\\cbpat2 \\f1\\fs24\\cf1 {\\b\\f2\\fs36 Dossier Payeur n\\'b0 " + payerId + "}");

    out.println("\\par \\pard\\plain \\s3\\slmult1 \\f1\\fs24\\cf1 {\\b\\fs28 " + FileUtil.rtfReplaceChars(payer.getContact().getFirstnameName()) + "}");

    adr = payer.getContact().getAddress();
    if (adr != null) {
      String adresse = adr.getAdr1() + "," + adr.getAdr2() + "," + adr.getCdp() + " " + adr.getCity();
      out.println("\\par " + FileUtil.rtfReplaceChars(adresse));
    }
    tel = payer.getContact().getTele();

    if (tel != null) {
      out.print("\\par ");
      for (int i = 0; i < tel.size(); i++) {
        Telephone t = tel.elementAt(i);
        String type = "";
        try {
          type = TeleIO.getTypeTel(t.getTypeTel(), dc);
        } catch (SQLException ex) {
          System.err.println(ex.getMessage());
        }
        out.print(type + " : " + t.getNumber() + "  ");
      }
      out.println();
    }

    //out.println("\\par RIB: "+payeur.getInstrument1()+" - "+payeur.getInstrument2());
    out.println("\\par ");


    /* ADHERENT */
    out.println("\\par \\pard\\plain \\s3\\sa144\\slmult1 \\f1\\fs24\\cf1 ");
    out.println("\\par \\pard\\plain \\s3\\qc\\sa144\\slmult1 \\f1\\fs24\\cf1 {\\b\\f2\\fs28 Fiches Adh\\'e9rents rattach\\'e9es}");

    Vector<PersonFile> adhs = ((PersonFileIO) DataCache.getDao(Model.PersonFile)).findMembers("WHERE payeur=" + payerId);
    for (int i = 0; i < adhs.size(); i++) {
      PersonFile d = adhs.elementAt(i);
      out.println("\\par \\pard\\plain \\s3\\slmult1 \\f1\\fs24\\cf1 Adherent " + d.getId() + " " + FileUtil.rtfReplaceChars(d.getContact().getFirstnameName()));
    }
    out.println("\\par \\pard\\plain \\s3\\slmult1 \\f1\\fs24\\cf1");

    /* ECHEANCIER */
    out.println("\\par \\pard\\plain \\s3\\qc\\sa144\\slmult1 \\f1\\fs24\\cf1 {\\b\\f2\\fs28 Ech\\'e9ancier}");// du "+debut+" au "+fin+"}");
    out.println("\\par \\pard\\plain \\s3\\slmult1 \\f1\\fs24\\cf1");

    Vector<OrderLine> echs = OrderLineIO.find("WHERE payeur=" + payer.getId() + " AND echeance >='" + start + "' AND echeance <='" + end + "'", dc);

    for (int i = 0; i < echs.size(); i++) {
      OrderLine e = echs.elementAt(i);
      out.println("\\par " + e.getDate() + " " + e.getMember());
      out.println(" " + e.getModeOfPayment() + " " + e.getDocument() + " " + FileUtil.rtfReplaceChars(String.valueOf(e.getAccount())));
      GemAmount m = new GemAmount(e.getAmount());
      out.println(" " + m);
      if (e.isPaid()) {
        out.println(" PAYE");
      }
      if (e.isTransfered()) {
        out.println(" TRANS");
      }
    }

    out.println("\\par ");
    out.println("\\par }");

    out.close();
  }
}

