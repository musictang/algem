/*
 * @(#) PhotoIO.java Algem 2.9.4.14 09/12/2015
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
 */
package net.algem.contact;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import javax.imageio.ImageIO;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.DataException;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.14
 * @since 2.9.4.14 09/12/2015
 */
public class PhotoIO
{

  public final static String TABLE = "personne_photo";
  private final PreparedStatement saveStmt;
  private final PreparedStatement loadStmt;
  private DataConnection dc;

  public PhotoIO(DataConnection dc) {
    this.dc = dc;
    saveStmt = dc.prepareStatement("INSERT INTO " + TABLE + " VALUES(?,?)");
    loadStmt = dc.prepareStatement("SELECT photo FROM " + TABLE + " WHERE idper = ?");
  }

  void save(int idper, byte[] data) throws SQLException {
    saveStmt.setInt(1, idper);
    saveStmt.setBytes(2, data);
    saveStmt.executeUpdate();
    saveStmt.close();
  }

  int save(Map<Integer, byte[]> map)  {
    PreparedStatement foundStmt = dc.prepareStatement("SELECT idper FROM " + TABLE + " WHERE idper = ?");
    int saved = 0;
    try {
//      dc.setAutoCommit(false);
      for (Map.Entry<Integer, byte[]> entry : map.entrySet()) {
        foundStmt.setInt(1, entry.getKey());
        ResultSet rs = foundStmt.executeQuery();
        if (rs.next()) {
          continue;
        }
        saveStmt.setInt(1, entry.getKey());
        saveStmt.setBytes(2, entry.getValue());
//        saveStmt.addBatch();
        saveStmt.executeUpdate();
        saved++;
        
      }
//      saveStmt.executeBatch();

      return saved;
    } catch (SQLException ex) {
      GemLogger.logException(ex);
      return -1;
//      throw new DataException(ex.getMessage());
    } finally {
//      dc.setAutoCommit(true);
    }
  }


  BufferedImage find(int idper) throws DataException {
    BufferedImage img = null;
    try {
      loadStmt.setInt(1, idper);
      ResultSet rs = loadStmt.executeQuery();

      while (rs.next()) {
        byte[] data = rs.getBytes(1);
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        img = ImageIO.read(in);
      }
      rs.close();
      loadStmt.close();

    } catch (SQLException | IOException ex) {
      GemLogger.logException(ex);
      throw new DataException(ex.getMessage());
    } 
    return img;
  }

}
