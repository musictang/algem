/*
 * @(#)RentalService.java 2.17.1 29/08/2019
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
package net.algem.rental;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import net.algem.accounting.Account;
import net.algem.accounting.AccountPrefIO;
import static net.algem.accounting.AccountUtil.getIntValue;
import net.algem.accounting.OrderLine;
import net.algem.accounting.OrderLineIO;
import net.algem.config.ConfigKey;
import net.algem.config.ConfigUtil;
import net.algem.config.Preference;
import net.algem.contact.PersonFile;
import net.algem.contact.member.Member;
import net.algem.enrolment.ModuleOrder;
import net.algem.enrolment.ModuleOrderIO;
import net.algem.planning.DateFr;
import net.algem.util.BundleUtil;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.MessageUtil;
import net.algem.util.model.GemList;
import net.algem.util.model.Model;

/**
 * Service class for rentable objects.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @version 2.17.1
 * @since 2.17.1 29/08/2019
 */
public class RentalService
{

  private DataConnection dc;
  private RentableObjectIO rentableObjectIO;
  private RentalOperationIO rentalOperationIO;

  public RentalService(DataConnection dc) {
    this.dc = dc;
    rentableObjectIO = (RentableObjectIO) DataCache.getDao(Model.RentableObject);
    rentalOperationIO = new RentalOperationIO(dc);
  }

  public void create(RentableObject o) throws SQLException {
    rentableObjectIO.insert(o);
  }

  public void update(RentableObject o) throws SQLException {
    rentableObjectIO.update(o);
  }

  /*
   * @throws RentmException if rentableobject is used
  */
  public void delete(RentableObject o) throws RentException, SQLException {
    String query = "SELECT objet from location WHERE objet = " + o.getId();
    ResultSet rs = dc.executeQuery(query);

    if (rs.next()) {
      throw new RentException(MessageUtil.getMessage("rent.delete.warning"));
    }
    rentableObjectIO.delete(o);
  }

  /**
   * Gets the list of rentals for this rentable object {@code id}.
   *
   * @param rentableObject id
   * @param start start date
   * @param end end date
   * @return a list of rental operations or an empty list if no student was found
   * @throws SQLException
   */
  List<RentalOperation> findRentals(int rentableObject, Date start, Date end) throws SQLException {
    return RentalOperationIO.findRentals(rentableObject, start, end, dc); 
  }

 public GemList<RentableObject> findAvailableRentable() {
     return new GemList(rentableObjectIO.findAvailable());
 }
 
  void saveRental(RentalOperation r) throws SQLException {
      rentalOperationIO.insert(r);
  }

    /**
   * Saves an order line for a single rehearsal.
   *
   * @param pFile person file
   * @param date date of reservation
   * @param amount total of the order
   * @param linkId If subscription, this is the id of the card, else the id of the schedule
   * @throws SQLException
   */
  public void saveRentalOrderLine(RentableObject r, PersonFile pFile, DateFr date, double amount) throws SQLException {

    Preference p = AccountPrefIO.find(AccountPrefIO.PERSONAL, dc);
    OrderLine e = new OrderLine();
    e.setMember(pFile.getId());
    int payer;
    Member member = pFile.getMember();
    if (member != null && member.getPayer() > 0) {
      payer = member.getPayer();
    } else {
      payer = pFile.getId();
    }

    e.setPayer(payer);
    e.setDate(date);
    e.setOrder(0);  //FMIXME linkId;
    String s = ConfigUtil.getConf(ConfigKey.DEFAULT_SCHOOL.getKey());
    e.setSchool(Integer.parseInt(s));
    e.setAccount(new Account((Integer) p.getValues()[0]));
    e.setCostAccount(new Account((String) p.getValues()[1]));
    e.setLabel(BundleUtil.getLabel("Rental.label") + " " + r.toString());
    e.setCurrency("E");
    e.setDocument("");
    e.setPaid(false);
    e.setModeOfPayment("TP"); //FIXME set default mode of payment (ccmdl=TP)
    e.setTransfered(false);
    e.setAmount(getIntValue(amount));
    OrderLineIO.insert(e, dc);
  }

}
