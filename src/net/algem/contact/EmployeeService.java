/*
 * @(#)EmployeeService.java 2.8.m 03/09/13
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

package net.algem.contact;

/**
 * Employee service interface.
 * 
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.m
 * @since 2.8.m 03/09/13
 */
public interface EmployeeService {

  /**
   * Finds an employee by id.
   * @param idper person id
   * @return an employee
   * @throws EmployeeException 
   */ 
  public Employee find(int idper) throws EmployeeException;
  
  /**
   * Creates a new employee.
   * @param e the employee to create
   * @throws EmployeeException 
   */
  public void create(Employee e) throws EmployeeException;
  
  /**
   * Updates an employee.
   * @param e the employee to update
   * @throws EmployeeException 
   */
  public void update(Employee e) throws EmployeeException;
  
  /**
   * Deletes an employee.
   * @param e the employee to delete
   * @throws EmployeeException 
   */
  public void delete(Employee e) throws EmployeeException;
  
  /**
   * Checks if insee number has a valid key.
   * @param insee
   * @return true if key is valid
   */
  public boolean checkNir(String insee);
}
