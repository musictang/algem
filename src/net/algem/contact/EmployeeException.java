/*
 * @(#)EmployeeException.java 2.8.m 03/09/13
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
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.m
 * @since 2.8.m 02/09/13
 */
public class EmployeeException extends Exception {

    /**
     * Creates a new instance of <code>EmployeeException</code> without detail message.
     */
    public EmployeeException() {
    }


    /**
     * Constructs an instance of <code>EmployeeException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public EmployeeException(String msg) {
        super(msg);
    }
}
