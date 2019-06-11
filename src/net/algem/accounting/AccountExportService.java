/*
 * @(#)AccountExportService.java	2.14.0 14/06/17
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

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

/**
 * Account export service interface.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.14.0
 * @since 2.8.r 13/12/13
 */
public interface AccountExportService {

  String getFileExtension();

  void export(String path, Vector<OrderLine> lines, String codeJournal, Account documentAccount) throws IOException;

  int tiersExport(String path, Vector<OrderLine> lines) throws IOException, SQLException;

  List<String> exportCSV(String path, Vector<OrderLine> orderLines) throws IOException;

  Account getAccount(String key) throws SQLException;

  Account getAccount(int id) throws SQLException;

  int getPersonalAccountId(int id) throws SQLException;

  String getCodeJournal(int account);

  Account getDocumentAccount(String mp) throws SQLException;

  String getAccount(OrderLine e);

  String getInvoiceNumber(OrderLine e);
}
