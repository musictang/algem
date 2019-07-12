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

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;
import net.algem.billing.VatIO;
import net.algem.contact.Person;
import net.algem.contact.member.Member;
import net.algem.planning.DateFr;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.MessageUtil;
import net.algem.util.TextUtil;
import net.algem.util.model.Model;
import net.algem.util.ui.MessagePopup;

/**
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @version 2.17.0
 * @since 2.17.0 01/07/2019
 */
public class ExportEMagnus
        extends CommonAccountExportService
{

  private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
  private final NumberFormat nf = NumberFormat.getInstance(Locale.FRENCH);
  private DataCache dataCache;

  public ExportEMagnus(DataConnection dc) {
    dbx = dc;
    
    dataCache = DataCache.getInitializedInstance();
    
    journalService = new JournalAccountService(dc);
    nf.setGroupingUsed(false);
    nf.setMinimumFractionDigits(4);
    nf.setMaximumFractionDigits(4);
  }

  @Override
  public String getFileExtension() {
    return ".txt";
  }

  @Override
  public void export(String path, Vector<OrderLine> lines, String codeJournal, Account documentAccount) throws IOException {
    int totalDebit = 0;
    int totalCredit = 0;
    String number = (documentAccount == null) ? "" : documentAccount.getNumber();
    OrderLine e = null;
    
    //try (PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(path), "windows-1252"), true)) {
//    try (PrintWriter out = new PrintWriter(new FileWriter(path))) {
    try (PrintWriter out = new PrintWriter(path,"cp1252")) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0, n = lines.size(); i < n; i++) {
        e = lines.elementAt(i);
      
        Person payer=new Person();
        Person student=new Person();
        Member member=new Member(0);
        try {
            payer = (Person)dataCache.findId(e.getPayer(), Model.Person);
            student = (Person)dataCache.findId(e.getMember(), Model.Person);
            member = (Member)dataCache.findId(e.getMember(), Model.Member);
        } catch (SQLException ex) {}

        //colonne A
        sb.append(i+1);
        sb.append(";T;O;CCMDL;MDL;");
        //colonne F
        sb.append(e.getDate().getYear()).append(";");
        sb.append(';');
        //colonne H
        sb.append('"').append(payer.getFirstnameName()).append('"').append(";");
        //sb.append('"').append(payeur.getNickName()).append('"').append(";");
        
        sb.append(";;;;;;;;;");
        //colonne R
        sb.append('"').append("Facturation école ressouce semestre année ").append(dataCache.getSchoolYearLabel()).append('"').append(';');
        sb.append(';');
        //colonne T
        String label = e.getLabel() == null ? "" : e.getLabel().replaceAll("\"", "");
        sb.append('"').append(student.getFirstnameName()).append(' ').append(label).append('"').append(';');
        
        sb.append(";;;R;");
        //colonne Y
        sb.append(new DateFr(new Date())).append(';');
        
        //colonne Z/AB/AC à mettre en config
        sb.append("7062;;BP3001;33;");
        
        //colonne AD Mnt HT
        sb.append(nf.format(e.getAmount() / 100.0)).append(';');
        
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
            sb.append(member.getBirth()).append(';');
        } else {
            sb.append(';');
        }
        
        sb.append(";;0;0;;;;;;;;;;;;;;0;");
                
        // fin = colonne DU

        sb.append("\r\n");
        out.print(sb.toString());
//        out.println(sb.toString());
        sb.delete(0, sb.length());
      }
    }

  }

  @Override
  public int tiersExport(String path, Vector<OrderLine> lines) throws IOException, SQLException {
      System.out.println("ExportEMagnus.tiersExport");
    int errors = 0;
    return errors;
  }

}
