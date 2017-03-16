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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import org.supercsv.cellprocessor.ParseDate;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.UniqueHashCode;
import org.supercsv.cellprocessor.ift.CellProcessor;
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

  public static String IMPORT_FILE_NAME = "/home/jm/dev/algem/git/doc/test-import1.csv";
  private String fileName;

  private ICsvListReader listReader;

  public ImportCsvHandler(String fileName) throws FileNotFoundException {
    listReader = new CsvListReader(new FileReader(fileName), CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
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
      /*while( (listReader.read()) != null ) {

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
                }*/

    } finally {
      if (listReader != null) {
        listReader.close();
      }
    }
  }

  public String[] getHeader() throws IOException {
    //listReader = new CsvListReader(new FileReader(fileName), CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
      return listReader.getHeader(true);
  }

  public List<String> getPreview() throws IOException {
    if (listReader != null) {
      List<String> data = listReader.read();
      return data;
    }
    return null;
  }

  public CellProcessor[] createProcessorFromSource(int length) {

    return new CellProcessor[length];
  }
}
