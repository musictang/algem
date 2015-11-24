/*
 * @(#)Profile.java	1.0.6 23/11/15
 *
 * Copyright (c) 2015 Musiques Tangentes. All Rights Reserved.
 *
 * This file is part of Algem Agenda.
 * Algem Agenda is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Algem Agenda is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Algem Agenda. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.algem.security;

/**
 * Profile enumeration.
 * Web profiles are sorted by permission levels, the weakest first
 * and begins at 10 to distinct them from the five initial Algem profiles.
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 1.0.6
 * @since 1.0.0 11/02/13
 */
public enum Profile
{
  Basic(0, "profile.basic.label"),
  User(1, "profile.user.label"),
  Teacher(2, "profile.teacher.label"),
  Public(3, "profile.public.label"),
  Admin(4, "profile.admin.label"),
  Visitor(10, "profile.visitor.label"),
  Member(11, "profile.member.label");

  private final int id;
  private final String label;

  Profile(int id, String label) {
    this.id = id;
    this.label = label;
  }

  public int getId() {
    return id;
  }

  public String getLabel() {
    return label;
  }

  @Override
  public String toString() {
    return label;
  }

}
