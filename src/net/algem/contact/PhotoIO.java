/*
 * @(#) PhotoIO.java Algem 2.9.4.14 16/12/15
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
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.DataException;
import net.algem.util.model.TableIO;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.14
 * @since 2.9.4.14 09/12/2015
 */
public class PhotoIO
  extends TableIO {

  public final static String TABLE = "personne_photo";
  private final static String LOAD_QUERY = "SELECT photo FROM " + TABLE + " WHERE idper = ?";
  private final static String SAVE_QUERY = "INSERT INTO " + TABLE + " VALUES(?,?)";
  private DataConnection dc;

  public PhotoIO(DataConnection dc) {
    this.dc = dc;
  }

  /**
   * Save an individual photo.
   *
   * @param idper person id
   * @param data content in bytes
   * @throws SQLException
   */
  void save(int idper, byte[] data) throws SQLException {
    PreparedStatement ps = dc.prepareStatement(SAVE_QUERY);
    ps.setInt(1, idper);
    ps.setBytes(2, data);
    ps.executeUpdate();
    closeStatement(ps);
  }

  /**
   * Save a set of photos.
   * Each photo is represented by its id and its contents in bytes.
   *
   * @param map the pictures to save encapsultated in a map
   * @return the number of pictures saved
   */
  int save(Map<Integer, byte[]> map) {
    PreparedStatement foundStmt = dc.prepareStatement("SELECT idper FROM " + TABLE + " WHERE idper = ?");
    PreparedStatement saveStmt = dc.prepareStatement(SAVE_QUERY);
    int saved = 0;
    ResultSet rs = null;
    try {
      dc.setAutoCommit(false);
      for (Map.Entry<Integer, byte[]> entry : map.entrySet()) {
        foundStmt.setInt(1, entry.getKey());
        rs = foundStmt.executeQuery();
        if (rs.next()) {
          continue;
        }
        saveStmt.setInt(1, entry.getKey());
        saveStmt.setBytes(2, entry.getValue());
        saveStmt.addBatch();
        saved++;
      }
      saveStmt.executeBatch();
      dc.commit();
      return saved;
    } catch (SQLException ex) {
      dc.rollback();
      GemLogger.logException(ex);
      return -1;
    } finally {
      dc.setAutoCommit(true);
      closeRS(rs);
      closeStatement(foundStmt);
      closeStatement(saveStmt);
    }
  }

  /**
   * Find the photo whose id is equal to the person's number @{code idper}.
   *
   * @param idper person id
   * @return an image
   * @throws DataException
   */
  BufferedImage find(int idper) throws DataException {
    BufferedImage img = null;
    PreparedStatement loadStmt = null;
    ResultSet rs = null;
    try {
      loadStmt = dc.prepareStatement(LOAD_QUERY);
      loadStmt.setInt(1, idper);
      rs = loadStmt.executeQuery();

      while (rs.next()) {
        byte[] data = rs.getBytes(1);
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        img = ImageIO.read(in);
      }
    } catch (SQLException | IOException ex) {
      GemLogger.logException(ex);
      throw new DataException(ex.getMessage());
    } finally {
      closeRS(rs);
      closeStatement(loadStmt);
    }
    return img;
  }

  /**
   * Find all the photos saved in database.
   *
   * @return a set of photos encapsulated in a map or an empty map if no photo was found
   * @throws SQLException
   */
  Map<Integer, byte[]> findAll() throws SQLException {

    Map<Integer, byte[]> saved = new HashMap<Integer, byte[]>();
    String query = "SELECT idper, photo FROM " + TABLE;
    try (ResultSet rs = dc.executeQuery(query)) {
      while (rs.next()) {
        saved.put(rs.getInt(1), rs.getBytes(2));
      }
    }
    return saved;
  }

}
