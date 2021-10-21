/*
 * @(#)DefaultUserService.java	2.17.2 27/10/19
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
 *
 */
package net.algem.security;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import net.algem.planning.DateFr;
import net.algem.planning.Schedule;
import net.algem.planning.ScheduleIO;
import net.algem.room.Room;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.model.Model;
import net.algem.util.postit.Postit;
import net.algem.util.postit.PostitIO;
import org.apache.commons.codec.binary.Base64;

/**
 * User operations service.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.17.2
 * @since 2.6.a 06/08/2012
 */
public class DefaultUserService
        implements UserService {

    private final DataConnection dc;
    private final DataCache dataCache;
    private final UserIO dao;
    private final PasswordEncryptionService encryptionService;

    public DefaultUserService(DataCache dataCache) {
        this.dataCache = dataCache;
        this.dc = DataCache.getDataConnection();
        dao = (UserIO) DataCache.getDao(Model.User);
        encryptionService = new PasswordEncryptionService();
    }

    /**
     * Authenticates a user by his login and pass.
     *
     * @param login login name
     * @param pass clear text password
     * @return true if pass encryption equals stored encrypted pass
     */
    @Override
    public boolean authenticate(String login, String pass) {
        byte[] salt = findAuthInfo(login, "clef");// find salt in BD
        byte[] encryptedPassword = findAuthInfo(login, "pass");
        if (salt == null || encryptedPassword == null) {
            return false;
        }
        try {
            return encryptionService.authenticate(pass, encryptedPassword, salt);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            GemLogger.logException(ex);
        }
        return false;
    }

    /**
     * Authenticates a user with some {@code pass}.
     *
     * @param user user instance
     * @param pass clear text password
     * @return true if clearPass encryption equals stored encrypted pass
     */
    @Override
    public boolean authenticate(User user, String pass) {
        byte[] salt = user.getPassInfo().getKey();
        byte[] encryptedPassword = user.getPassInfo().getPass();
        if (salt == null || encryptedPassword == null) {
            return false;
        }
        try {
            return encryptionService.authenticate(pass, encryptedPassword, salt);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            GemLogger.logException(ex);
        }
        return false;
    }

    @Override
    public List<User> findAll(String where) {
        List<User> v = new ArrayList<>();
        try {
            v = dao.find(where);
        } catch (SQLException ex) {
            GemLogger.logException(ex);
        }
        return v;
    }

    @Override
    public User find(String login) {
        User u = null;
        try {
            u = dao.findLogin(login);
            if (u == null) {
                try {
                    u = dao.findId(Integer.parseInt(login));
                } catch (NumberFormatException ex) {
                    GemLogger.log(Level.WARNING, ex.getMessage());
                }
            }
        } catch (SQLException ex) {
            GemLogger.logException(ex);
        }

        return u;
    }

    /**
     * Search password authentification info for some user.
     *
     * @param login login name
     * @param colName table column name
     * @return an array of bytes
     */
    private byte[] findAuthInfo(String login, String colName) {
        byte[] info = null;
        try {
            int id = 0;
            try {
                Integer.parseInt(login);
            } catch (NumberFormatException ex) {
                id = -1;
            }
            String query = "SELECT " + colName + " FROM " + UserIO.TABLE + " WHERE login = '" + login + "' OR idper = " + id;
            ResultSet rs = dc.executeQuery(query);
            while (rs.next()) {
                info = Base64.decodeBase64(rs.getString(1));
            }
        } catch (SQLException ex) {
            GemLogger.logException(ex);
        }
        return info;

    }

    @Override
    public User findId(int id) {
        try {
            return (User) DataCache.findId(id, Model.User);
        } catch (SQLException ex) {
            GemLogger.logException(ex);
            return null;
        }
    }

    @Override
    public void update(User user) throws UserException {
        try {
            dao.update(user);
            dataCache.update(user);
        } catch (SQLException ex) {
            GemLogger.logException(ex);
            throw new UserException(ex.getMessage(), "MODIFICATION");
        }
    }

    @Override
    public void delete(User user) throws UserException {
        dao.delete(user.getId());
        dataCache.remove(user);
    }

    List<MenuAccess> getMenuAccess(int userId) {
        List<MenuAccess> mal = new ArrayList<MenuAccess>();
        String query = "SELECT idmenu,label,autorisation FROM menu2, menuaccess WHERE menu2.id = menuaccess.idmenu AND idper = " + userId + " ORDER BY idmenu";
        try (ResultSet rs = dc.executeQuery(query)) {
            while (rs.next()) {
                String label = BundleUtil.getLabel(rs.getString(2));
                MenuAccess m = new MenuAccess(rs.getInt(1), label, rs.getBoolean(3));
                mal.add(m);
            }
        } catch (SQLException ex) {
            GemLogger.logException(query, ex);
        }
        return mal;
    }

    void updateAccess(MenuAccess m, String col, int userId) {
        try {
            String query = "UPDATE menuaccess SET " + col + " = '" + (m.isAuth() ? "t" : "f")
                    + "' WHERE idper = " + userId + " AND idmenu = " + m.getId();
            dc.executeUpdate(query);
        } catch (SQLException ex) {
            GemLogger.logException("Update menuaccess", ex);
        }
    }

    public @Override
    List<SQLRights> getTableRights(int userId) {
        List<SQLRights> rtl = new ArrayList<>();
        String query = "SELECT nomtable,lecture,insertion,modification,suppression FROM droits WHERE idper=" + userId + " ORDER BY nomtable";
        try (ResultSet rs = dc.executeQuery(query)) {
            while (rs.next()) {
                SQLRights tr = new SQLRights();
                tr.setName(rs.getString(1));
                tr.setAuthRead(rs.getBoolean(2));
                tr.setAuthInsert(rs.getBoolean(3));
                tr.setAuthUpdate(rs.getBoolean(4));
                tr.setAuthDelete(rs.getBoolean(5));
                rtl.add(tr);
            }
        } catch (SQLException ex) {
            GemLogger.logException(query, ex);
        }
        return rtl;
    }

    @Override
    public void updateTableRights(String table, String col, boolean value, int userId) {
        String query = "UPDATE droits SET " + col + " = " + value
                + " WHERE idper = " + userId + " AND nomtable = '" + table + "'";
        try {
            dc.executeUpdate(query);
        } catch (SQLException ex) {
            GemLogger.logException(ex);
        }
    }

    @Override
    public List<User> getPostitUserList() {
        try {
            return dao.findPostitUserList();
        } catch (SQLException ex) {
            GemLogger.logException(ex);
            return new ArrayList<User>();
        }
    }

    @Override
    public void create(Postit p) throws SQLException {
        PostitIO.insert(p, dc);
    }

    @Override
    public void update(Postit p) throws SQLException {
        PostitIO.update(p, dc);
    }

    @Override
    public void delete(Postit p) throws SQLException {
        PostitIO.delete(p, dc);
    }

    /**
     * Gets the list of postits readable by current user. A postit is readable
     * if it meets one of the following conditions :
     * <ul>
     * <li>its receiver is public ( = 0)</li>
     * <li>its issuer equals {@code userId}</li>
     * <li>its receiver equals {@code userId}</li>
     * </ul>
     *
     * @param userId current user id
     * @param read last postit read
     * @return a list of readable postits
     */
    @Override
    public List<Postit> getPostits(int userId, int read) {
        String where = "WHERE (dest = 0 OR emet = " + userId + " OR dest = " + userId + ") AND id > " + read;
        return PostitIO.find(where, dc);
    }

    /**
     * Detects bookings equal to or after the current date, still to be
     * confirmed. The postit list the date and location of each pending booking.
     *
     * @return a postit if any booking still to be confirmed or null if not
     */
    @Override
    public Postit getBookingAlert() {
        String query = "SELECT jour,lieux FROM " + ScheduleIO.TABLE
                + " WHERE ptype in(" + Schedule.BOOKING_GROUP + "," + Schedule.BOOKING_MEMBER + ") AND jour >= CURRENT_DATE";
        StringBuilder sb = new StringBuilder(BundleUtil.getLabel("Booking.to.be.confirmed").toUpperCase());
        boolean toBeConfirmed = false;
        try (ResultSet rs = dc.executeQuery(query)) {
            while (rs.next()) {
                toBeConfirmed = true;
                sb.append('\n').append(new SimpleDateFormat("dd/MM/yy").format(rs.getDate(1)));
                Room room = (Room) DataCache.findId(rs.getInt(2), Model.Room);
                String r = room == null ? "" : room.toString();
                sb.append(':').append(r);
            }
        } catch (SQLException ex) {
            GemLogger.logException(ex);
            return null;
        }
        Postit b = null;
        if (toBeConfirmed) {
            b = new Postit();
            b.setText(sb.toString());
            b.setType(Postit.BOOKING);
            DateFr today = new DateFr(new Date());
            b.setDay(today);
            b.setTerm(today);
        }
        return b;
    }

    /**
     *
     * @param menu menu key
     * @param user user instance
     * @return true if menu is authorized for this user
     * @deprecated
     */
    @Override
    public boolean authorize(String menu, User user) {
        String query = "SELECT autorisation FROM menuaccess a, menu2 m"
                + " WHERE m.label = '" + menu + "'"
                + " AND a.idper = " + user.getId()
                + " AND a.idmenu = m.id";
        try (ResultSet rs = dc.executeQuery(query)) {
            if (rs.next()) {
                return (rs.getBoolean(1));
            }
        } catch (SQLException e) {
            GemLogger.logException(e);
        }
        return true; // autorisé par défaut
    }

    UserPass createPassword(String pass) {

        try {
            byte[] salt = encryptionService.generateSalt();
            UserPass up = new UserPass(encryptionService.getEncryptedPassword(pass, salt), salt);
            return up;
        } catch (InvalidKeySpecException | NoSuchAlgorithmException ex) {
            GemLogger.logException(ex);
        }
        return null;
    }

    @Override
    public void create(final User u) throws UserException {
        try {
            u.setPassInfo(createPassword(u.getPassword()));
            dc.withTransaction(new DataConnection.SQLRunnable<Void>() {
                @Override
                public Void run(DataConnection conn) throws Exception {
                    dao.insert(u);
                    dao.initMenus(u);
                    dao.initRights(u);
                    dao.initEstabStatus(u.getId());
                    return null;
                }
            });
        } catch (Exception ex) {
            GemLogger.logException(ex);
            throw new UserException(ex.getMessage(), "CREATION");
        }

    }

    @Override
    public boolean update(User nu, final User old) throws UserException {
        byte[] salt = null;

        if (old.getPassInfo() == null || old.getPassInfo().getKey() == null) {
            try {
                salt = encryptionService.generateSalt();
            } catch (NoSuchAlgorithmException ex) {
                throw new UserException(ex.getMessage(), "ENCRYPTION");
            }
        } else {
            salt = old.getPassInfo().getKey();
        }

        try {
            byte[] p = encryptionService.getEncryptedPassword(nu.getPassword(), salt);
            nu.setPassInfo(new UserPass(p, salt));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            // if password is null for exemple
            throw new UserException(ex.getMessage(), "ENCRYPTION");
        }

        if (!nu.equals(old) || nu.getPassword().length() > 0) {
            try {
            if (nu.getPassword() == null || nu.getPassword().length() < 1) {
                dao.update(nu, false);
            } else {
                dao.update(nu);
            }                
                return true;
            } catch (SQLException ex) {
                throw new UserException(ex.getMessage(), "MODIFICATION");
            }
        }
        return false;
    }
}
