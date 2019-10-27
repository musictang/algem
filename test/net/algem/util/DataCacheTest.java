/*
 * @(#) DataCacheTest.java Algem 2.17.2 27/10/2019
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
 */
package net.algem.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 */
public class DataCacheTest {

  private final Map<String, Map<Integer, Boolean>> authorizations = new HashMap<>();
  private final String menu = "MenuTest";

  @Test
  public void shoudNotAuthorizeWhenMenuAccessIsFalse() {

    Map<Integer, Boolean> authorization = new HashMap<>();
    authorization.put(1, Boolean.FALSE);
    authorizations.put(menu, authorization);

    assertFalse(authorizeStubMethod(menu));
  }

  @Test
  public void shoudNotAuthorizeWhenAuthorizationNotFound() {
    assertFalse(authorizeStubMethod(menu));
  }

  @Test
  public void shoudAuthorizeWhenMenuAccessIsTrue() {
    Map<Integer, Boolean> authorization = new HashMap<>();
    authorization.put(1, Boolean.TRUE);
    authorizations.put(menu, authorization);

    assertTrue(authorizeStubMethod(menu));
  }

  @Test
  public void shoudAuthorizeWhenMenuAccessNotFound() {
    Map<Integer, Boolean> authorization = new HashMap<>();
    authorization.put(2, Boolean.TRUE);
    authorizations.put(menu, authorization);

    assertTrue(authorizeStubMethod(menu));
  }

  private boolean authorizeStubMethod(String menu) {
        int userId = 1;
        Map<Integer, Boolean> access = authorizations.get(menu);
        if (access == null) return false;
        return Optional.ofNullable(access.get(userId)).orElse(true);
    }


}
