/*
 * @(#)BillingService 2.8.y 29/09/14
 *
 * Copyright (c) 1999-2014 Musiques Tangentes All Rights Reserved.
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
import java.util.Date;
import java.util.List;
import java.util.Vector;
import net.algem.accounting.Account;
import net.algem.config.Param;
import net.algem.planning.DateRange;
import net.algem.util.model.GemList;

/**
 * Service interface for billing.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.y
 * @since 2.3.a 06/02/12
 */
public interface BillingService
{

  /**
   * Get a list of invoices. By default, all invoices are returned.
   * @return a list of invoices
   * @throws SQLException
   */
  public List<Invoice> getInvoices() throws SQLException;

  /**
   * Get a list of invoices between {@code start} and {@code end} date.
   * @param start start date
   * @param end end date
   * @return a list of invoices
   * @throws SQLException
   */
  public List<Invoice> getInvoices(Date start, Date end) throws SQLException;

  /**
   * Get the list of invoices related to the person with id {@code idper}.
   * @param idper person's id
   * @return a list of invoices
   * @throws SQLException
   */
  public List<Invoice> getInvoices(int idper) throws SQLException;

  /**
   * Get the list of bills edited between {@code start} and {@code end} date
   * and related to the person with id {@code idper}.
   * @param idper person's id
   * @param start start date
   * @param end end date
   * @return a list of invoices
   * @throws SQLException
   */
  public List<Invoice> getInvoices(int idper, Date start, Date end) throws SQLException;

  /**
   * Get a list of quotes. By default, all quotes are returned.
   * @return a list of quotes
   * @throws SQLException
   */
  public List<Quote> getQuotations() throws SQLException;

  /**
   * Get a list of quotes between {@code start} and {@code end} date.
   * @param start start date
   * @param end end date
   * @return a list of quotes
   * @throws SQLException
   */
  public List<Quote> getQuotations(Date start, Date end) throws SQLException;

  /**
   * Get the list of quotes related to the person with id {@code idper}.
   * @param idper person's id
   * @return a list of quotes
   * @throws SQLException
   */
  public List<Quote> getQuotations(int idper) throws SQLException;

  /**
   * Get the list of quotes edited between {@code start} and {@code end} date
   * and related to the person with id {@code idper}.
   * @param idper person's id
   * @param start start date
   * @param end end date
   * @return a list of quotes
   * @throws SQLException
   */
  public List<Quote> getQuotations(int idper,Date start, Date end) throws SQLException;

  /**
   * Find the period of the current financial year.
   * @return a couple of dates
   */
  public DateRange getFinancialYear();

  public String getContact(int id);

  public void create(Invoice inv) throws SQLException, BillingException;

  public void create(Quote q) throws SQLException, BillingException;

  public void update(Invoice inv) throws BillingException;

  public void update(Quote d) throws BillingException;

  public void delete(Quote q) throws SQLException;

  public Item getItem(int id) throws SQLException;

  public Vector<Item> getItems(String where) throws SQLException;

  public Collection<InvoiceItem> findItemsByInvoiceId(String invNumber) throws SQLException;

  /**
   * Creates a standard invoice line.
   * @param it standard line
   * @throws SQLException
   */
  public void create(Item it) throws SQLException;

  public void update(Item it) throws SQLException;

  public void delete(Item it) throws SQLException;

  public Quote duplicate(Quote v);

  public Invoice createInvoiceFrom(Quote q) throws BillingException;
  
  public Invoice createCreditNote(Quote i);

  public GemList<Account> getAccounts();

  public Collection<Param> getVat();
}
