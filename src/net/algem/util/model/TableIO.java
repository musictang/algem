/*
 * @(#)TableIO.java	2.9.2.1 20/02/15
 *
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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
package net.algem.util.model;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import net.algem.util.DataConnection;
//import org.apache.commons.lang.StringEscapeUtils;

/**
 * Abstract class for persistence.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.2.1
 */
public abstract class TableIO
{

  protected final static String STARTING_QUOTE = "'";
  protected final static String LEFT_COL_SEPARATOR = ",'";
  protected final static String RIGHT_COL_SEPARATOR = "','";
  protected final static String END_OF_QUERY = "')";

  /**
   * Escapes quotes and backslashes in the string {@code s}.
   * @param s
   * @return a string
   */
  public static String escape(String s) {
    if (s == null || (s.indexOf('\'') < 0 && s.indexOf('\\') < 0)) {
      return s;
    }

    /*StringBuilder b = new StringBuilder(s);
    for (int i = 0; i < b.length(); i++) {
      if (b.charAt(i) == '\'' || b.charAt(i) == '\\') {
        b.insert(i++, '\\');
        i++;
      }
    }
    return b.toString();*/
    s = s.replace("\\", "\\\\");
    s = s.replace("\'", "\\'");
    return s;

  }

  /**
   * Unescape quote characters in the string {@code s}.
   * @param s
   * @return a string
   * @deprecated 
   */
  public static String unEscape(String s) {
    /*if (s == null || s.indexOf('\'') < 0) {
      return s;
    }
    StringBuilder b = new StringBuilder(s);
    for (int i = 0; i < b.length(); i++) {
      if (b.charAt(i) == '\\') {
        b.deleteCharAt(i);
        i++;
      }
    }
    return b.toString();
    */
    return s;
  }

  /**
   * Replaces chars with accents in the string {@code s } with generic ascii characters.
   * @param s the string to replace
   * @return a string in lower case without accents
   */
  public static String normalize(String s) {
    char[] from = {'à', 'â', 'ä', 'é', 'è', 'ê', 'ë', 'î', 'ï', 'ô', 'ö', 'ù', 'û', 'ü', 'ç'};
    char[] to   = {'a', 'a', 'a', 'e', 'e', 'e', 'e', 'i', 'i', 'o', 'o', 'u', 'u', 'u', 'c'};
    char[] str = s.toLowerCase().toCharArray();
    for (int i = 0; i < str.length; i++) {
      for (int j = 0; j < from.length; j++) {
        if (str[i] == from[j]) {
          str[i] = to[j];
        }
      }
    }
    return escape(new String(str));
  }

  /**
   * Gets the last sequence number for the sequence {@code seq_name}.
   * @param seq_name
   * @param dc
   * @return an integer
   * @throws SQLException
   */
  public static int nextId(String seq_name, DataConnection dc) throws SQLException {

    int nextid = 0;

    String idquery = "SELECT nextval('" + seq_name + "')";
    ResultSet rs = dc.executeQuery(idquery);
    rs.next();
    nextid = rs.getInt(1);

    return nextid;
  }

  /**
   * Gets the column names of the table {@code tableName}.
   * @param dc
   * @param tableName
   * @return an array of strings
   * @throws SQLException
   */
  public static String[] getColumnNames(String tableName, DataConnection dc) throws SQLException {

    String query = "SELECT * FROM " + tableName;
    ResultSet rs = dc.executeQuery(query);

    ResultSetMetaData md = rs.getMetaData();
    int cols = md.getColumnCount();
    String[] names = new String[cols];

    for (int i = 1; i <= cols; i++) {
      String name = md.getColumnName(i);
      names[i - 1] = name;
    }
    return names;
  }
}
