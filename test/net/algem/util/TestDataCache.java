/*
 * @(#) TestDataCache.java Algem 2.17.3 02/11/19
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

import net.algem.contact.Person;
import net.algem.security.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.17.3
 * @since 2.17.2 27/10/2019
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({DataCache.class})
@SuppressStaticInitializationFor({"net.algem.contact.Person"})
public class TestDataCache {

  private final Map<String, Map<Integer, Boolean>> authorizations = new HashMap<>();
  private final String menu = "MenuTest";
  private DataCache cache;

  @Before
  public void init() throws IllegalAccessException {
    Field field = PowerMockito.field(DataCache.class, "authorizations");
    field.set(DataCache.class, authorizations);
    cache = Mockito.mock(DataCache.class);

    when(cache.getUser()).thenReturn(new User(new Person(1)));
    when(cache.authorize(anyString())).thenCallRealMethod();
  }

  @Test
  public void shouldNotAuthorizeWhenMenuAccessIsFalse() {
    Map<Integer, Boolean> authorization = new HashMap<>();
    authorization.put(1, Boolean.FALSE);
    authorizations.put(menu, authorization);

    assertFalse(cache.authorize(menu));
  }

  @Test
  public void shouldAuthorizeWhenAuthorizationNotFound() {
    assertTrue(cache.authorize(menu));
  }

  @Test
  public void shouldAuthorizeWhenMenuAccessIsTrue() {
    Map<Integer, Boolean> authorization = new HashMap<>();
    authorization.put(1, Boolean.TRUE);
    authorizations.put(menu, authorization);
    assertTrue(cache.authorize(menu));
  }

  @Test
  public void shouldAuthorizeWhenMenuAccessNotFound() {
    Map<Integer, Boolean> authorization = new HashMap<>();
    authorization.put(2, Boolean.TRUE);
    authorizations.put(menu, authorization);

    assertTrue(cache.authorize(menu));
  }

}
