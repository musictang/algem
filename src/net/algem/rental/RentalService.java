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
import net.algem.enrolment.CourseOrderIO;
import net.algem.enrolment.ModuleOrder;
import net.algem.enrolment.ModuleOrderIO;
import net.algem.group.Musician;
import net.algem.planning.DateFr;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.MessageUtil;
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
  private RentableObjectIO RentableObjectIO;

  public RentalService(DataConnection dc) {
    this.dc = dc;
    RentableObjectIO = (RentableObjectIO) DataCache.getDao(Model.RentableObject);
  }

  boolean isUsed(int moduleId, DateFr start) throws SQLException {
    Vector<ModuleOrder> vm = ModuleOrderIO.find("WHERE m.id = " + moduleId + " AND cm.debut >= '" + start.toString() + "'", dc);
    return vm != null && vm.size() > 0;
  }

  public void create(RentableObject o) throws SQLException {
    RentableObjectIO.insert(o);
  }

  public void update(RentableObject o) throws SQLException {
    RentableObjectIO.update(o);
  }

  /*
   * @throws RentmException if rentableobject is used
  */
  public void delete(RentableObject o) throws RentException, SQLException {
    String query = "SELECT objet from location WHERE objet = " + o.getId();
    ResultSet rs = dc.executeQuery(query);

    if (rs.next()) {
      throw new RentException(MessageUtil.getMessage("rent.suppression.warning"));
    }
    RentableObjectIO.delete(o);
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

}
