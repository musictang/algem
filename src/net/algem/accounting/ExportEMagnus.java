/*
 * @(#) ExportEMagnus.java Algem 2.17.0 12/07/19
 *
 * Copyright (c) 1999-2019 Musiques Tangentes. All Rights Reserved.
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
package net.algem.accounting;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;
import net.algem.contact.Person;
import net.algem.contact.member.Member;
import net.algem.planning.DateFr;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.model.Model;

/**
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @version 2.17.0
 * @since 2.17.0 01/07/2019
 */
public class ExportEMagnus
        extends CommonAccountExportService {

    private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final NumberFormat nf = NumberFormat.getInstance(Locale.FRENCH);
    private final DataCache dataCache;

    public ExportEMagnus(DataConnection dc) {
        dbx = dc;

        dataCache = DataCache.getInitializedInstance();

        journalService = new JournalAccountService(dc);
        nf.setGroupingUsed(false);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
    }

    @Override
    public String getFileExtension() {
        return ".txt";
    }

    @Override
    public void export(String path, Vector<OrderLine> lines, String codeJournal, Account documentAccount) throws IOException {
        OrderLine e = null;

        Collections.sort(lines);
        int mnt=0;
        int asso=0;
        int cptasso=0;
        String libel="";
        OrderLine prec;
        Person payer = new Person();
        //try (PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(path), "windows-1252"), true)) {
        try (PrintWriter out = new PrintWriter(path, "cp1252")) {
            StringBuilder sb = new StringBuilder();
            prec = lines.elementAt(0);
            mnt = prec.getAmount();
            libel = prec.getLabel();
            for (int i = 1, n = lines.size(); i < n; i++) {
                e = lines.elementAt(i);
                try {
                    payer = (Person) dataCache.findId(prec.getPayer(), Model.Person);
                } catch (SQLException ex) {
                    System.out.println("ExportEMagnus:"+ex);
                }
                if (payer.getOrganization() != null && payer.getOrganization().getId() != 0 && (asso == 0 || e.getPayer() == asso)) {
                    mnt += e.getAmount();
                    asso = payer.getOrganization().getId();
                    cptasso++;
                    System.out.println("ORGANISATION payer="+payer.getId()+" asso="+asso+" cptasso="+cptasso+" mnt="+mnt);
                } else if (prec.getPayer() == e.getPayer() && prec.getMember() == e.getMember()) {
                    mnt += e.getAmount();
                    if (libel.startsWith("Formule complète")) {
                        libel = "FC "+libel.substring(17);
                    } else if (libel.startsWith("formule complète")) {
                        libel = "FC "+libel.substring(17);
                    } else if (libel.startsWith("Location")) {
                        libel = libel.substring(8,10);
                    }
                    if (e.getLabel().startsWith("Formule complète")) {
                        libel+= "FC "+e.getLabel().substring(17);
                    } else if (e.getLabel().startsWith("formule complète")) {
                        libel = "FC "+libel.substring(17);
                    } else if (e.getLabel().startsWith("Location")) {
                        libel+= e.getLabel().substring(8,10);
                    } else
                        libel += " "+e.getLabel();
                    System.out.println("concat payer="+prec.getPayer()+" adh="+prec.getMember()+" mnt="+mnt+" libel="+libel);
                } else {
                    if (cptasso > 0) {
                        libel = "Inscriptions "+cptasso+" adhérents";
                        cptasso=0;
                    }
                addLine(payer, asso, out, sb, prec, i, mnt, libel);
                asso=0;
                prec = e;
                libel=e.getLabel();
                mnt = e.getAmount();
                try {
                    payer = (Person) dataCache.findId(prec.getPayer(), Model.Person);
                } catch (SQLException ex) {
                    System.out.println("ExportEMagnus:"+ex);
                }
                }
            }
            addLine(payer, asso, out, sb, prec, lines.size()-1, mnt, libel);

        }

    }

    public void addLine(Person payer, int asso, PrintWriter out, StringBuilder sb, OrderLine prec, int i, int mnt, String libel) {
                
                Person student = new Person();
                Member member = new Member(0);
                try {
                    student = prec.getPayer() == prec.getMember() ? payer : (Person) dataCache.findId(prec.getMember(), Model.Person);
                    member = (Member) dataCache.findId(prec.getMember(), Model.Member);
                } catch (SQLException ex) {
                    System.out.println("ExportEMagnus:"+ex);
                }
                //System.out.println("ExportEMagnus payeur="+payer+" student="+student);
                //colonne A
                sb.append(i + 1);
                sb.append(";T;O;CCMDL;MDL;");
                //colonne F
                sb.append(prec.getDate().getYear()).append(";");
                sb.append(';');
                //colonne H
                sb.append('"').append(payer.getNickName()).append('"').append(";");

                sb.append(";;;;;;;;;");
                //colonne R
                sb.append('"').append("Facturation école ressouce semestre année ").append(dataCache.getSchoolYearLabel()).append('"').append(';');
                sb.append(';');
                //colonne T
//                String label = e.getLabel() == null ? "" : e.getLabel().replaceAll("\"", "");
                String label = libel == null ? "" : libel.replaceAll("\"", "");
                if (label.length() > 50) {
                    System.out.println("LABEL SIZE="+label.length()+" "+label);
                    label = label.substring(0,50);
                }
                if (asso > 0)
                    sb.append('"').append(label).append('"').append(';');
                else
                    sb.append('"').append(student.getFirstnameName()).append(' ').append(label).append('"').append(';');
                sb.append(";;;R;");
                //colonne Y
                sb.append(dateFormat.format(new Date())).append(';');
                //sb.append(new DateFr(new Date())).append(';');

                //colonne Z/AB/AC à mettre en config
                sb.append("7062;;BP3001;33;");

                //colonne AD Mnt HT
//                sb.append(nf.format(e.getAmount() / 100.0)).append(';');
                sb.append(nf.format(mnt / 100.0)).append(';');

                sb.append("0;0;0;0;9;;-1;");

                //colonne AL
                sb.append("0;;;;;;;;;;;;;;;;");

                //colonne BB à mettre en config
                sb.append("94;");

                sb.append(";;;;;;;;;;;;;");

                //colonne BP
                sb.append('"').append("le tribunal administratif").append('"').append(';');

                sb.append(";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;");

                //colonne DC
                if (member != null && member.getBirth() != null) {
                    sb.append(dateFormat.format(member.getBirth().getDate())).append(';');
                    //sb.append(member.getBirth()).append(';');
                } else {
                    sb.append(';');
                }

                sb.append(";;0;0;;;;;;;;;;;;;;0;");

                // fin = colonne DU
                sb.append("\r\n");

                out.print(sb.toString());

                sb.delete(0, sb.length());        
    }
    
    @Override
    public int tiersExport(String path, Vector<OrderLine> lines) throws IOException, SQLException {
        System.out.println("ExportEMagnus.tiersExport");
        int errors = 0;
        return errors;
    }

}
