/*
 * @(#)SqlList.java	2.6.a 25/09/12
 * 
 * Copyright (c) 1999-2012 Musiques Tangentes. All Rights Reserved.
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

import java.util.Vector;
import javax.swing.JList;
import net.algem.util.DataCache;

/**
 * Selection list.
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a

 */
public class SqlList
        extends JList
{

  private DataCache dataCache;
  private Vector<SQLkey> values;

  public SqlList(int l, DataCache _dc) {
    dataCache = _dc;
    values = new Vector<SQLkey>();
    setVisibleRowCount(l);
  }

  public void loadSQL(Vector<SQLkey> sqlist) {
//    int key = 0;
//    String lib = null;
    //System.out.println(query);
    //removeAll();
    values = sqlist;
    setListData(values);
//    try {
//      ResultSet rs = dc.executeQuery(query);
//      while (rs.next()) {
//        key = rs.getInt(1);
//        lib = rs.getString(2);
//        values.addElement(new SQLkey(key, lib));
//        //add(lib);
//      }
//    } catch (SQLException e) {
//      dc.logException(query, e);
//    }
  }

  @Override
  public void removeAll() {
    values = new Vector<SQLkey>();
    setListData(values);
  }

  public Vector<SQLkey> getValues() {
    return values;
  }

  public int getKey() {
    if (getSelectedIndex() < 0) {
      return -1;
    }
    SQLkey k = values.elementAt(getSelectedIndex());
    return k.getKey();
  }

  public String getLabel() {
    if (getSelectedIndex() < 0) {
      return "";
    }
    SQLkey k = values.elementAt(getSelectedIndex());
    return k.getLabel();
  }

  public void setLabel(String l) {
    SQLkey key = null;
    for (int i = 0; i < values.size(); i++) {
      key = values.elementAt(i);
      if (l.equals(key.getLabel())) {
        setSelectedIndex(i);
        return;
      }
    }
  }

  public void setKey(int k) {
    selectKey(k);
  }

  public SQLkey getElementAt(int index) {
    if (values.isEmpty()) {
      return null;
    }
    return values.elementAt(index);
  }

  public void selectKey(int k) {
    SQLkey key = null;
    for (int i = 0; i < values.size(); i++) {
      key = values.elementAt(i);
      if (key.getKey() == k) {
        setSelectedIndex(i);
        return;
      }
    }
  }
}
