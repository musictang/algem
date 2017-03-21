/*
 * @(#) ImportCsvHandler.java Algem 2.12.1 15/03/2017
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
package net.algem.edition;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.algem.util.SimpleCharsetDecoder;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.UniqueHashCode;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.comment.CommentStartsWith;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.12.1
 * @since 2.12.1 15/03/2017
 */
public class ImportCsvHandler {

  public static String IMPORT_FILE_NAME = "/home/jm/algem/src/git/trunk/doc/test-import1.csv";
  //public static String IMPORT_FILE_NAME = "/home/jm/dev/algem/git/doc/test-import1.csv";
  private String fileName;
  private Charset charset;

//  private ICsvListReader listReader;

  public ImportCsvHandler() {
  }

  
  public ImportCsvHandler(String fileName) throws FileNotFoundException {
    this.fileName = fileName;
    //listReader = new CsvListReader(new FileReader(fileName), CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
  }


  /**
   * An example of reading a file with a variable number of columns using CsvListReader. It demonstrates that you can
   * still use cell processors, but you must execute them by calling the executeProcessors() method on the reader,
   * instead of supplying processors to the read() method. In this scenario, the last column (birthDate) is sometimes
   * missing.
   */
  public static void main(String[] args) throws Exception {

    final CellProcessor[] allProcessors = new CellProcessor[]{new UniqueHashCode(), // customerNo (must be unique)
      new NotNull(), // firstName
      new NotNull(), // lastName
      new ParseDate("dd/MM/yyyy")}; // birthDate

    final CellProcessor[] noBirthDateProcessors = new CellProcessor[]{allProcessors[0], // customerNo
      allProcessors[1], // firstName
      allProcessors[2]}; // lastName

    ICsvListReader listReader = null;
    try {
      listReader = new CsvListReader(new FileReader(IMPORT_FILE_NAME), new CsvPreference.Builder('"', ';', "\n").build());

      String[] header = listReader.getHeader(true); // skip the header (can't be used with CsvListReader)
      System.out.printf("nombre de colonnes : %d\n", header.length);
      for (String s : header) {
        System.out.println(s.trim());
      }
      while( (listReader.read()) != null ) {

                        // use different processors depending on the number of columns
                        final CellProcessor[] processors;
                        if( listReader.length() == noBirthDateProcessors.length ) {
                                processors = noBirthDateProcessors;
                        } else {
                                processors = allProcessors;
                        }

                        final List<Object> customerList = listReader.executeProcessors(processors);
                        System.out.println(String.format("lineNo=%s, rowNo=%s, columns=%s, customerList=%s",
                                listReader.getLineNumber(), listReader.getRowNumber(), customerList.size(), customerList));
                }

    } finally {
      if (listReader != null) {
        listReader.close();
      }
    }
  }
  public ICsvListReader getReader() throws FileNotFoundException, UnsupportedEncodingException {
    //listReader = new CsvListReader(new FileReader(fileName), CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
    InputStreamReader input = new InputStreamReader(new FileInputStream(fileName), charset != null ? charset.name() : "UTF-8");
    return new CsvListReader(input, new CsvPreference.Builder('"', ';', "\n").ignoreEmptyLines(true).skipComments(new CommentStartsWith("#")).build());
  }

  public List<String> getPreview(ICsvListReader listReader) throws IOException {
    if (listReader != null) {
      List<String> data = listReader.read();
      listReader.close();
      return data;
  }
    
    return null;
  }
  
  void setOptions(String fileName, Charset c) {
    this.fileName = fileName;
    this.charset = c;
  }
  
  public boolean create(CellProcessor[] processors, Map<String,Integer> map) throws IOException {
    ICsvListReader listReader = getReader();
    listReader.getHeader(true);
    while((listReader.read()) != null ) {
      final List<Object> rowData = listReader.executeProcessors(processors);
      int idx = map.get(ImportCsvCtrl.IMPORT_FIELDS[0]);
      Integer id = 0;
      if (idx > -1) {
        id = (Integer) rowData.get(map.get(ImportCsvCtrl.IMPORT_FIELDS[0]));
      }  
      idx = map.get(ImportCsvCtrl.IMPORT_FIELDS[2]);
      String name = null;
      if (idx > -1) {
        name = (String) rowData.get(map.get(ImportCsvCtrl.IMPORT_FIELDS[2]));
      }
      if (name == null) {
        return false;
      }
      
      idx = map.get(ImportCsvCtrl.IMPORT_FIELDS[4]);
      String adr1 = null;
      if (idx > -1) {
        adr1 = (String) rowData.get(map.get(ImportCsvCtrl.IMPORT_FIELDS[4]));
      }
      
      idx = map.get(ImportCsvCtrl.IMPORT_FIELDS[5]);
      String adr2 = null;
      if (idx > -1) {
        adr2 = (String) rowData.get(map.get(ImportCsvCtrl.IMPORT_FIELDS[5]));
      }
      idx = map.get(ImportCsvCtrl.IMPORT_FIELDS[6]);
      String cdp = null;
      if (idx > -1) {
        cdp = (String) rowData.get(map.get(ImportCsvCtrl.IMPORT_FIELDS[6]));
      }
      idx = map.get(ImportCsvCtrl.IMPORT_FIELDS[7]);
      String city = null;
      if (idx > -1) {
        city = (String) rowData.get(map.get(ImportCsvCtrl.IMPORT_FIELDS[7]));
      }
      System.out.println("Id : "+ id);
      System.out.println("Nom : "+ name);
      System.out.println("Adr1 : "+ adr1);
      System.out.println("Adr2 : "+ adr2);
      System.out.println("Cdp : "+ cdp);
      System.out.println("Ville : "+ city);
      
    }
    
    return true;
  }

  public CellProcessor[] createProcessorFromSource(int length) {

    return new CellProcessor[length];
  }
}
