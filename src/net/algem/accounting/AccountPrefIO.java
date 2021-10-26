/*
 * @(#)AccountPrefIO.java 2.14.0 02/06/17
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
 *
 */
package net.algem.accounting;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import net.algem.config.Param;
import net.algem.config.ParamTableIO;
import net.algem.config.Preference;
import net.algem.util.DataConnection;
import net.algem.util.model.TableIO;

/**
 * Default accounts persistence.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.14.0
 * @since 2.1.i
 *
 */
public class AccountPrefIO
        extends TableIO {

    public static final String TABLE = "comptepref";
    private static final String[] COLUMNS = {"id", "idcompte", "idanalytique"};
    public static final String MEMBERSHIP = "ADHÉSIONS";
    public static final String PRO_MEMBERSHIP = "ADHÉSIONS PRO";
    public static final String LEISURE = "FORMATION LOISIR";
    public static final String PRO = "FORMATION PROFESSIONNELLE";
    public static final String REHEARSAL = "RÉPÉTITIONS";
    public static final String CASH = "COMPTE DE CAISSE";
    public static final String BANK = "COMPTE DE BANQUE";
    public static final String PERSONAL = "DIVERS/TIERS";

    public static void insert(Preference p, String tablename, DataConnection dc) throws SQLException {
        Object[] values = p.getValues();
        String query = "INSERT INTO " + tablename + " VALUES('" + p.getKey() + "','" + values[0] + "','" + values[1] + "')";
        dc.executeUpdate(query);
    }

    public static void update(Preference p, DataConnection dc) throws SQLException {
        Object[] values = p.getValues();
        String query = "UPDATE " + TABLE + " SET " + COLUMNS[1] + " = '" + values[0] + "', "
                + COLUMNS[2] + " = '" + values[1] + "' WHERE " + COLUMNS[0] + " = '" + p.getKey() + "'";
        dc.executeUpdate(query);
    }

    /**
     * Retrieves the default accounts for the category {@code key}.
     *
     * @param dc
     * @param key the category
     * @return a preference
     * @throws SQLException
     */
/*
    public static Preference find(String key, DataConnection dc) throws SQLException {
        String query = "SELECT * FROM " + TABLE + " WHERE " + COLUMNS[0] + " = '" + key + "'";
        ResultSet rs = dc.executeQuery(query);

        if (!rs.next()) {
            return null;
        }
        Preference p = new Preference(rs.getString(1));
        p.setValues(new Object[]{rs.getInt(2), rs.getString(3)});
        return p;
    }
*/
    /**
     * Gets the account instance corresponding to preference value (account id).
     *
     * @param p preference
     * @param dc data connection
     * @return an account instance
     * @throws SQLException
     */
    public static Account getAccount(Preference p, DataConnection dc) throws SQLException {
        return AccountIO.find((Integer) p.getValues()[0], dc);
    }

    /**
     * Checks if an account is used in preferred accounts.
     *
     * @param accountId
     * @param dc data connection
     * @return true if this account has been set in preferred accounts
     * @throws SQLException
     */
    public static boolean containsAccount(int accountId, DataConnection dc) throws SQLException {
        String query = "SELECT idcompte FROM " + AccountPrefIO.TABLE + " WHERE idcompte = " + accountId;
        ResultSet rs = dc.executeQuery(query);
        return rs.next();
    }

    /**
     * Checks if a cost account is found in preferred accounts.
     *
     * @param accountId account number
     * @param dc data connection
     * @return true if this account has been set in preferred accounts
     * @throws SQLException
     */
    public static boolean containsCostAccount(String accountId, DataConnection dc) throws SQLException {
        String query = "SELECT idanalytique FROM " + AccountPrefIO.TABLE + " WHERE idanalytique = '" + accountId + "'";
        ResultSet rs = dc.executeQuery(query);
        return rs.next();
    }

    public static Account getCostAccount(Preference p, DataConnection dc) {
        return OrderLineIO.findAccount(CostAccountCtrl.tableName, CostAccountCtrl.columnKey, (String) p.getValues()[1], dc);
    }

    public static Account getCostAccount(int idCompte, DataConnection dc) throws SQLException {
        Account c = AccountIO.find(idCompte, dc);
        for (Preference p : findAll(dc)) {
            int idc = (Integer) p.getValues()[0];// idcompte
            if (idc == c.getId()) {
                Param a = ParamTableIO.findByKey(CostAccountCtrl.tableName, CostAccountCtrl.columnKey, (String) p.getValues()[1], dc);
                return new Account(a);
                //return new Account((String)p.getValues()[1]); // idanalytique
            }
        }
        return new Account("");
    }

    /**
     * Retrieves the different categories of default accounts.
     *
     * @param dc
     * @return an array of Strings
     * @throws SQLException
     */
    public static String[] findKeys(DataConnection dc) {
        String query = "SELECT DISTINCT " + COLUMNS[0] + " FROM " + TABLE;
        try (ResultSet rs = dc.executeQuery(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {

        if (!rs.last()) {
            return null;
        }
        int count = rs.getRow();
        rs.beforeFirst();

        String[] keys = new String[count];
        int i = 0;
        while (rs.next()) {
            keys[i++] = rs.getString(1);
        }
        return keys;
        } catch (SQLException ignore) {}
        return new String[0];
    }

    /**
     * Retrieves all the preferences for the default accounts.
     *
     * @param dc
     * @return VecListtor<Preference>
     * @throws SQLException
     */
    public static List<Preference> findAll(DataConnection dc) {
        String query = "SELECT * FROM " + TABLE + " ORDER BY " + COLUMNS[0];
        try (ResultSet rs = dc.executeQuery(query)) {

            if (!rs.next()) {
                return null;
            }
            List<Preference> prefs = new ArrayList<>();
            while (rs.next()) {
                Preference p = new Preference(rs.getString(1));
                p.setValues(new Object[]{rs.getInt(2), rs.getString(3)});
                prefs.add(p);
            }
            if (prefs.size() > 0) {
                return prefs;
            }
        } catch (SQLException ignore) {
        }
        return null;
    }

      
    public static void delete(Preference p, DataConnection dc) throws SQLException {
        String query = "DELETE FROM " + TABLE + " WHERE " + COLUMNS[0] + " = '" + p.getKey() + "')";
        dc.executeQuery(query);
    }
}
