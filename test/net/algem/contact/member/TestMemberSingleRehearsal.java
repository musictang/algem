/*
 * @(#)TestMemberSingleRehearsal.java 2.9.2 12/01/15
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
package net.algem.contact.member;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.swing.JFrame;
import junit.framework.TestCase;
import net.algem.TestProperties;
import net.algem.contact.Contact;
import net.algem.contact.PersonFile;
import net.algem.planning.DateFr;
import net.algem.planning.Hour;
import net.algem.planning.MemberRehearsalSchedule;
import net.algem.planning.ScheduleObject;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.AbstractDesktopCtrl;
import net.algem.util.module.GemDesktopCtrl;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.2
 */
public class TestMemberSingleRehearsal
        extends TestCase
{

  private GemDesktop desktop;
  private DataConnection dc;
  private DataCache dataCache;
  private MemberRehearsalCtrl rehearsalCtrl;
  private final Calendar cal = Calendar.getInstance();
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
    RehearsalPass card = new RehearsalPass("ZikTest", 120.0F, 600);

    int rest = rehearsalCtrl.calcRemainder(card.getTotalTime(), duration);
    int expected = card.getTotalTime() - duration;
    assertTrue(expected == rest);

    duration = 600;
    rest = rehearsalCtrl.calcRemainder(card.getTotalTime(), duration);
    expected = 0;
    assertTrue(expected == rest);
  }

  public void testSetRehearsalCardWithRenewal() throws SQLException, MemberException {

    int rest = 120; // duree restante sur la carte
    int idper = 2; // numéro d'adhérent arbitraire
    Contact c = new Contact();
    c.setId(idper);
    PersonFile dp = new PersonFile();
    dp.setContact(c);
    RehearsalPass pass = new RehearsalPass("ZikTest", 120.0F, 600);
    pass.setId(1);

    ScheduleObject p = new MemberRehearsalSchedule();
    p.setDate(new DateFr(new Date()));
    // 3 hours of rehearsal
    p.setStart(new Hour("10:00"));
    p.setEnd(new Hour("13:00"));
    p.setIdPerson(dp.getId());
    p.setIdRoom(1);
    // clean up before inserting dummy card
    io.deleteByIdper(idper);
    
    PersonSubscriptionCard pcr = new PersonSubscriptionCard(idper, pass.getId(), new DateFr(p.getDate()), rest);
    io.insert(pcr);
    dp.setSubscriptionCard(pcr);
    
    PersonSubscriptionCard newCard = rehearsalCtrl.updatePersonalCard(dp, pass, p);
    float amount = pass.getAmount();
    MemberService service = new MemberService(dc);
    service.saveRehearsalOrderLine(dp, new DateFr(p.getDate()), amount, newCard.getId());
    List<PersonSubscriptionCard> cardList = io.find(idper, null, false, 1);
    PersonSubscriptionCard nc = cardList.get(0); // dernière carte enregistrée
    assertNotNull("carte repet introuvable ?", nc);
    assertEquals("erreur date ?", new DateFr(new Date()), nc.getPurchaseDate());
    assertTrue(pass.getTotalTime() - 60 == nc.getRest());
    // clean up
    io.deleteByIdper(idper);
  }

  
  public void testSetRehearsalCardWithoutRenewal() throws SQLException, MemberException {
    int rest = 120; // duree restante sur la carte
    int idper = 2; // numéro d'adhérent arbitraire
    Contact c = new Contact();
    c.setId(idper);
    PersonFile dp = new PersonFile();
    dp.setContact(c);
    RehearsalPass pass = new RehearsalPass("ZikTest", 120.0F, 600);
    pass.setId(1);

    ScheduleObject p = new MemberRehearsalSchedule();
    DateFr yesterday = new DateFr(new Date());
    yesterday.decDay(-1);
    p.setDate(yesterday);
    // 3 hours of rehearsal
    p.setStart(new Hour("10:00"));
    p.setEnd(new Hour("12:00"));
    p.setIdPerson(dp.getId());
    p.setIdRoom(1);
    // clean up before inserting dummy card
    io.deleteByIdper(idper);
    
    PersonSubscriptionCard pcr = new PersonSubscriptionCard(idper, pass.getId(), new DateFr(p.getDate()), rest);
    io.insert(pcr);
    dp.setSubscriptionCard(pcr);
    
    PersonSubscriptionCard newCard = rehearsalCtrl.updatePersonalCard(dp, pass, p);
    assertNull(newCard);

    //clean up
    io.deleteByIdper(idper);

  }

  public void testCreateNewCard() throws Exception {

    int idper = 3;
    RehearsalPass card = new RehearsalPass("ZikTest", 120.0F, 600);
    //RehearsalCardIO.insert(card, dc);
    PersonSubscriptionCard pcr = new PersonSubscriptionCard(idper, card.getId(), new DateFr(cal.getTime()), 480);
    // clean up before inserting
    io.delete(idper);
    io.insert(pcr);
    List<PersonSubscriptionCard> cardList = io.find(idper, null, false, 1);
    PersonSubscriptionCard nc = cardList.get(0);

    assertNotNull(nc);
    assertTrue(480 == nc.getRest());
    assertTrue(3 == nc.getIdper());
    assertTrue(card.getId() == nc.getPassId());
    assertEquals(new DateFr(cal.getTime()), nc.getPurchaseDate());

    // clean up
    io.delete(idper);
  }
}
