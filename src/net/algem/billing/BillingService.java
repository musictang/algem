/*
 * @(#)BillingService 2.8.n 26/09/13
 *
 * Copyright (c) 1999-2013 Musiques Tangentes All Rights Reserved.
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
package net.algem.billing;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import net.algem.accounting.Account;
import net.algem.config.Param;
import net.algem.util.model.GemList;

/**
 * Service interface for billing.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.n
 * @since 2.3.a 06/02/12
 */
public interface BillingService
{

  public List<Invoice> getInvoices() throws SQLException;

  public List<Invoice> getInvoices(int idper) throws SQLException;

  public List<Quote> getQuotations() throws SQLException;

  public List<Quote> getQuotations(int idper) throws SQLException;

  public String getContact(int id);

  public void create(Invoice inv) throws SQLException, BillingException;

  public void create(Quote q) throws SQLException, BillingException;

  public void update(Invoice inv) throws BillingException;

  public void update(Quote d) throws BillingException;

  public void delete(Invoice inv) throws SQLException;

  public Item getItem(int id) throws SQLException;

  public Vector<Item> getItems(String where) throws SQLException;

  public void create(Item it) throws SQLException;

  public void update(Item it) throws SQLException;

  public void delete(Item it) throws SQLException;
  
  public Quote duplicate(Quote v);

  public Invoice createInvoiceFrom(Quote q) throws BillingException;

  public GemList<Account> getAccounts();

  public Collection<Param> getVat();
}
