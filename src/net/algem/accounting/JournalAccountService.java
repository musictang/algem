/*
 * @(#)JournalAccountService.java	2.6.a 02/08/2012
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
package net.algem.accounting;

import java.sql.SQLException;
import java.util.Vector;
import net.algem.util.DataConnection;
import net.algem.util.GemLogger;
import net.algem.util.MessageUtil;
import net.algem.util.model.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.2.a
 */
public class JournalAccountService
{

  private DataConnection dc;

  public JournalAccountService(DataConnection dc) {
    this.dc = dc;
  }

  public Vector<JournalAccount> find() throws ModelException {
    try {
      return JournalAccountIO.find(dc);
    } catch (SQLException ex) {
      GemLogger.logException(ex);
      throw new ModelNotFoundException(
              MessageUtil.getMessage("journal.notfound.exception") + "\n(" + ex.getMessage() + ")");
    }
  }

  public JournalAccount find(JournalAccount j) throws ModelException {
    try {
      return JournalAccountIO.find(j, dc);
    } catch (SQLException ex) {
      GemLogger.logException(ex);
      throw new ModelNotFoundException(
              MessageUtil.getMessage("journal.notfound.exception") + "\n(" + ex.getMessage() + ")");
      //return null;
    }
  }

  public JournalAccount find(int account) throws ModelException {
    try {
      return JournalAccountIO.find(account, dc);
    } catch (SQLException ex) {
      GemLogger.logException(ex);
      throw new ModelNotFoundException(
              MessageUtil.getMessage("journal.notfound.exception") + "\n(" + ex.getMessage() + ")");
      //return null;
    }
  }

  public void create(JournalAccount j) throws ModelException {
    try {
      JournalAccountIO.insert(j, dc);
    } catch (SQLException ex) {
      GemLogger.logException(ex);
      throw new ModelCreateException(
              MessageUtil.getMessage("journal.create.exception") + "\n(" + ex.getMessage() + ")");
    }
  }

  public void delete(JournalAccount j) throws ModelException {
    try {
      JournalAccountIO.delete(j, dc);
    } catch (SQLException ex) {
      GemLogger.logException(ex);
      throw new ModelDeleteException(
              MessageUtil.getMessage("journal.delete.exception") + "\n(" + ex.getMessage() + ")");
    }
  }

  public void update(JournalAccount j) throws ModelException {
    try {
      JournalAccountIO.update(j, dc);
    } catch (SQLException ex) {
      GemLogger.logException(ex);
      throw new ModelUpdateException(
              MessageUtil.getMessage("journal.update.exception") + "\n(" + ex.getMessage() + ")");
    }
  }

  public Vector<Account> getAccounts() {
    try {
      return AccountIO.find(true, dc);
      //return ActivableParamTableIO.find(dc, ComptesCtrl.tableName, ComptesCtrl.columnName, ComptesCtrl.columnFilter);
    } catch (SQLException ex) {
      GemLogger.logException(ex);
      return null;
    }
  }
}
