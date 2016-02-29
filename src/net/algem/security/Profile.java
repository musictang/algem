/*
 * @(#)Profile.java 2.9.5 29/02/16
 *
 * Copyright (c) 1999-2015 Musiques Tangentes. All Rights Reserved.
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

package net.algem.security;

import net.algem.util.BundleUtil;

/**
 * Default profiles.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">jean-marc gobat</a>
 * @version 2.9.5
 * @since 2.9.5 29/02/16
 */
public enum Profile {

  BASIC(0, BundleUtil.getLabel("Profile.basic.label")),
  USER(1, BundleUtil.getLabel("Profile.user.label")),
  TEACHER(2, BundleUtil.getLabel("Profile.teacher.label")),
  PUBLIC(3, BundleUtil.getLabel("Profile.public.label")),
  ADMIN(4, BundleUtil.getLabel("Profile.administrator.label")),
  VISITOR(10, BundleUtil.getLabel("Profile.visitor.label")),
  MEMBER(11, BundleUtil.getLabel("Profile.member.label"));

  private final int id;
  private final String name;

  private Profile(int id, String name) {
    this.id = id;
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }
  
  public static Profile get(int id) {
    switch(id) {
      case 0: return BASIC;
      case 1: return USER;
      case 2: return TEACHER;
      case 3: return PUBLIC;
      case 4: return ADMIN;
      case 10: return VISITOR;
      case 11: return MEMBER;
      default: return BASIC;
    }
  }

  @Override
  public String toString() {
    return name;
  }
}
