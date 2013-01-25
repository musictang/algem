/*
 * @(#)TestDesktopMailHandler.java	2.6.a 08/10/12
 *
 * Copyright (c) 1999 Musiques Tangentes. All Rights Reserved.
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
package net.algem.util.desktop;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import junit.framework.TestCase;
import net.algem.TestProperties;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 * @since 2.4.a 22/05/12
 */
public class TestDesktopMailHandler
        extends TestCase
{

  private DataCache dataCache;
  private Desktop desktop;
  private DataConnection dc;

  public TestDesktopMailHandler(String testName) {
    super(testName);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    dc = TestProperties.getDataConnection();
    dataCache = TestProperties.getDataCache(dc);
    dataCache.load(null);
    desktop = Desktop.getDesktop();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testSend() {
    try {
      Runtime.getRuntime().exec("thunderbird -compose to='jm@musiques-tangentes.asso.fr',bcc='yanomito@numericable.com,sanchezmarie@orange.fr'");
    } catch (IOException ie) {
      fail(ie.getMessage());
    }
  }

  /**
   * Vérification compatibilité Desktop avec l'environnement en cours.
   */
  public void testMailTo() {
    try {
      desktop.mail(new URI("mailto", "jm@musiques-tangentes.asso.fr", null));
    } catch (IOException ie) {
      fail(ie.getMessage());
    } catch (URISyntaxException u) {
      fail(u.getMessage());
    }
    /* String mailTo = "user@gmail.com"; mailTo += "?subject=Test avec Java"; mailTo
     * += "?body=Envoyer un email avec Java"; */
  }

  public void testLogic() {

    assertTrue(!true || !false);
    assertTrue(!false || !true);
    assertFalse(!true || !true);
    assertTrue(!false || !false);
  }
}
