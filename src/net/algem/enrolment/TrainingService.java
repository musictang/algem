/*
 * @(#) TrainingService.java Algem 2.15.0 06/09/2017
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
 */

package net.algem.enrolment;

import java.sql.SQLException;
import java.util.List;
import net.algem.course.Module;
import net.algem.util.DataConnection;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.15.0
 * @since 2.15.0 06/09/2017
 */
public class TrainingService {
  private final TrainingContractIO contractIO;

  public TrainingService(DataConnection dc) {
    this.contractIO = new TrainingContractIO(dc);
  }

  public List<TrainingContract> findContracts(int idper) throws SQLException {
    return contractIO.findAll(idper);
  }

  public Module getModule(int orderId) throws SQLException {
    return contractIO.getModuleInfo(orderId);
  }

  public float getVolume(int orderId) throws SQLException {
    return contractIO.getVolume(orderId);
  }
  public void createContract(TrainingContract t) throws SQLException {
    contractIO.create(t);
  }

  public void updateContract(TrainingContract t) throws SQLException {
    contractIO.update(t);
  }

  public void deleteContract(int id) throws SQLException {
    contractIO.delete(id);
  }
}
