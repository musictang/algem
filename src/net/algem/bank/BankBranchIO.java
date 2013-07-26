/*
 * @(#)BankBranchIO.java 2.8.i 08/07/13
 * 
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
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
package net.algem.bank;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import net.algem.contact.AddressIO;
import net.algem.contact.ContactIO;
import net.algem.contact.Person;
import net.algem.contact.TeleIO;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.TableIO;

/**
 * IO methods class {@link net.algem.bank.BankBranch}.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.i
 */
public class BankBranchIO
        extends TableIO
{

  private DataConnection dc;
  private ContactIO contactIO;

  public BankBranchIO(DataConnection dc, ContactIO contactIO) {
    this.dc = dc;
    this.contactIO = contactIO;
  }

  public BankBranchIO(DataConnection dc) {
    this.dc = dc;
    contactIO = new ContactIO(dc);
  }

  public void insert(BankBranch a) throws SQLException {
    dc.setAutoCommit(false);

    try {
      contactIO.insert(a);
      BranchIO.insert(a, dc);
      dc.commit();
    } catch (SQLException e1) {
      GemLogger.logException("transaction insert agence", e1);
      dc.rollback();
      throw e1;
    } finally {
      dc.setAutoCommit(true);
    }
  }

  public void update(BankBranch a, BankBranch newBranch) throws SQLException {

    try {
      dc.setAutoCommit(false);
      contactIO.update(a, newBranch);
      if (!a.equals(newBranch)) { //XXX
        newBranch.setId(a.getId());
        BranchIO.update(newBranch, dc);
      }
      dc.commit();
    } catch (SQLException e1) {
      dc.rollback();
      GemLogger.logException("transaction update agence", e1);
      throw e1;
    } finally {
      dc.setAutoCommit(true);
    }
  }

  public void update(int branchId, String bicCode) throws SQLException {
    BranchIO.update(branchId, bicCode, dc);
  }

  void delete(BankBranch a) throws Exception {
    dc.setAutoCommit(false);

    try {      
      contactIO.delete(a);
      BranchIO.delete(a, dc);
      dc.commit();
    } catch (Exception e1) {
      dc.rollback();
      GemLogger.logException("transaction delete agence", e1);
      throw e1;
    } finally {
      dc.setAutoCommit(true);
    }
  }

  public BankBranch findId(int n) {
    String query = "WHERE p.id = " + n;
    Vector<BankBranch> v = find(query, true);
    if (v.size() > 0) {
      return v.elementAt(0);
    }
    return null;
  }

  public Vector<BankBranch> findRib(Rib r) {
    return find(" WHERE b.code='" + r.getEstablishment() + "' and g.code='" + r.getBranch() + "'", true);
  }

  /**
   *
   * @param b bank code
   * @param c branch code
   * @return a list of bankbranch
   */
  public Vector<BankBranch> findCode(String b, String c) {
    return find("WHERE g.code='" + c + "' AND b.code='" + b + "'", true);
  }

  public Vector<BankBranch> find(String where, boolean complet) {
    Vector<BankBranch> v = new Vector<BankBranch>();
    // 2.1a AJOUT DISTINCT pour éviter les doublons dans la vue.
    String query = "SELECT DISTINCT p.id,p.ptype,b.code, b.nom,b.multiguichet,g.code,g.domiciliation,g.bic FROM personne p, guichet g, banque b ";
    if (where != null && where.length() > 0) {
      query += where + " AND p.id = g.id";
    } else {
      query += " WHERE p.id = g.id";
    }

    query += " AND p.ptype=" + Person.BANK + " AND b.code = g.banque ORDER BY b.code,g.code";
//    System.out.println(query);
    try {
      ResultSet rs = dc.executeQuery(query);
      while (rs.next()) {
        BankBranch a = new BankBranch();
        a.setId(rs.getInt(1));
        a.setType(rs.getShort(2));
        Bank b = new Bank();
        b.setCode(rs.getString(3).trim());
        b.setName(unEscape(rs.getString(4)).trim());
        b.setMulti(rs.getBoolean(5));
        a.setBank(b);
        a.setCode(rs.getString(6).trim());
        a.setDomiciliation(rs.getString(7).trim());
        if (complet) {
          a.setAddress(AddressIO.findId(a.getId(), dc));
          a.setTele(TeleIO.findId(a.getId(), dc));
        }
        a.setBicCode(rs.getString(8));
        v.addElement(a);
      }
      rs.close();
    } catch (SQLException e) {
      GemLogger.logException(query, e);
    }
    return v;
  }
}
