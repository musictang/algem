/*
 * @(#) PageModelIO.java Algem 2.14.0 19/07/2017
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

package net.algem.config;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.algem.util.DataConnection;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.14.0
 * @since 2.14.0 19/07/2017
 */
public class PageTemplateIO {


  public static final String TABLE = "pagemodel";
  private DataConnection dc;

  public PageTemplateIO(DataConnection dc) {
    this.dc = dc;
  }

  public void insert(short type, byte[] document) throws SQLException {
    String query = "INSERT INTO " + TABLE + " VALUES(?,?)";
    try (PreparedStatement ps = dc.prepareStatement(query)) {
      ps.setShort(1, type);
      ps.setBytes(2, document);

      ps.executeUpdate();
    }
  }

   public void update(short type, byte[] document) throws SQLException {
    String query = "UPDATE " + TABLE + " SET page=? WHERE mtype = ?";
    try (PreparedStatement ps = dc.prepareStatement(query)) {
      ps.setBytes(1, document);
      ps.setShort(2, type);
      ps.executeUpdate();
    }
  }

  public PageTemplate find(short type) throws SQLException {
    String query = "SELECT page FROM " + TABLE + " WHERE mtype = ?";
    try (PreparedStatement ps = dc.prepareStatement(query)) {
      ps.setShort(1, type);

      try(ResultSet rs = ps.executeQuery()) {
        while(rs.next()) {
          PageTemplate p = new PageTemplate(type, rs.getBytes(1));
          return p;
        }
      }
      return null;
    }
  }

  public List<PageTemplate> findAll() throws SQLException {
    String query = "SELECT mtype,page FROM " + TABLE + " ORDER BY mtype";
    List<PageTemplate> pages = new ArrayList<>();
    try(ResultSet rs = dc.executeQuery(query)) {
        while(rs.next()) {
          PageTemplate p = new PageTemplate(rs.getShort(1), rs.getBytes(2));
          pages.add(p);
        }
      }
      return pages;
    }

}
