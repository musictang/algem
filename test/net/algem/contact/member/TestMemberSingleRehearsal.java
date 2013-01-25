/*
 * @(#)TestMemberSingleRehearsal.java 2.6.a 08/10/12
 * 
 * Copyright (c) 1999-2010 Musiques Tangentes. All Rights Reserved.
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
package net.algem.contact.member;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Properties;
import javax.swing.JFrame;
import junit.framework.TestCase;
import net.algem.TestProperties;
import net.algem.contact.Contact;
import net.algem.contact.PersonFile;
import net.algem.planning.DateFr;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemDesktopCtrl;
import net.algem.util.ui.PopupDlg;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class TestMemberSingleRehearsal
        extends TestCase
{

  private GemDesktop desktop;
  private DataConnection dc;
  private DataCache dataCache;
  private MemberRehearsalCtrl rehearsalCtrl;
  private Calendar cal = Calendar.getInstance();
  private PersonSubscriptionCardIO io;

  public TestMemberSingleRehearsal(String testName) {
    super(testName);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    dc = TestProperties.getDataConnection();
    dataCache = TestProperties.getDataCache(dc);
    dataCache.load(null);
    desktop = new GemDesktopCtrl(new JFrame(), dataCache, new Properties());
    rehearsalCtrl = new MemberRehearsalCtrl(desktop);
    io = new PersonSubscriptionCardIO(dc);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testCalcRest() {
    int duration = 120; // en minutes
    int nbs = 20;
    int sessionDuration = 30;

    RehearsalCard card = new RehearsalCard("ZikTest", 120.0F, nbs, sessionDuration);

    int rest = rehearsalCtrl.calcRemainder(card, duration);
    int expected = card.getTotalDuration() - duration;
    assertTrue(expected == rest); //duree totale > duree repet

    card.setSessionsNumber(3);
    rest = rehearsalCtrl.calcRemainder(card, duration);
    expected = 0;
    assertTrue(expected == rest);
  }

  public void testSetRehearsalCardWithRenewal() throws SQLException {
    float amount;
    int duration = 180; // duree de la repet
    int rest = 120; // duree restante sur la carte
    int idper = 2; // numéro d'adhérent arbitraire
    Contact c = new Contact();
    c.setId(idper);
    PersonFile dp = new PersonFile();
    dp.setContact(c);
    RehearsalCard card = new RehearsalCard("ZikTest", 120.0F, 20, 30);
    card.setId(1);
    PersonSubscriptionCard pcr = new PersonSubscriptionCard(idper, card.getId(), new DateFr(cal.getTime()), rest);
    io.insert(pcr);
    dp.setSubscriptionCard(pcr);
    // Emulation dialog
    PopupDlg dlg = new RehearsalCardDlg();
    ((RehearsalCardDlg) dlg).setCard(card);
    dlg.setValidation(true);
    amount = rehearsalCtrl.setRehearsalCard(dp, pcr.getPurchaseDate(), duration, dlg);

    PersonSubscriptionCard nc = io.find(idper, null); // dernière carte enregistrée
    assertNotNull("carte repet introuvable ?", nc);
    assertEquals("erreur date ?", new DateFr(cal.getTime()), nc.getPurchaseDate());
    assertTrue(card.getTotalDuration() - 60 == nc.getRest());
    assertTrue(0.0 < amount);
    assertTrue(card.getAmount() == amount);
    // clean up
    io.deleteByIdper(idper);

  }

  public void testSetRehearsalCardWithoutRenewal() throws SQLException {
    float amount; // montant de l'échéance en cas de création ou de renouvellement
    int duration = 120; // duree de la repet
    int rest = 120; // duree restante sur la carte
    int idper = 2; // numéro d'adhérent arbitraire
    RehearsalCard card = new RehearsalCard("ZikTest", 120.0F, 20, 30);
    card.setId(1);
    PersonSubscriptionCard pcr = new PersonSubscriptionCard(idper, card.getId(), new DateFr(cal.getTime()), rest);

    // Emulation dialog
    PopupDlg dlg = new RehearsalCardDlg();
    ((RehearsalCardDlg) dlg).setCard(card);
    dlg.setValidation(true);
    //montant = adhrepet.setRehearsalCard(duree, idper, pcr, dialog);
    PersonSubscriptionCard nc = io.find(idper, null);
    assertNull(nc);

  }

  public void testCreateNewCard() throws Exception {

    int idper = 3;
    RehearsalCard card = new RehearsalCard("ZikTest", 120.0F, 20, 30);
    //RehearsalCardIO.insert(card, dc);
    PersonSubscriptionCard pcr = new PersonSubscriptionCard(idper, card.getId(), new DateFr(cal.getTime()), 480);
    io.insert(pcr);

    PersonSubscriptionCard nc = io.find(idper, null);

    assertNotNull(nc);
    assertTrue(480 == nc.getRest());
    assertTrue(3 == nc.getIdper());
    assertTrue(card.getId() == nc.getRehearsalCardId());
    assertEquals(new DateFr(cal.getTime()), nc.getPurchaseDate());

    // clean up
    io.delete(nc.getId());
  }
}
