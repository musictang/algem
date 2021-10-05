/*
 * @(#)TestPlanModifCtrl.java 2.9.5 24/02/16
 * 
 * Copyright (c) 1999-2016 Musiques Tangentes. All Rights Reserved.
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
package net.algem.planning.editing;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import javax.swing.JFrame;
import junit.framework.TestCase;
import net.algem.TestProperties;
import net.algem.contact.member.*;
import net.algem.planning.*;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.MessageUtil;
import net.algem.util.jdesktop.DesktopMailHandler;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.GemDesktopCtrl;

/**
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.5
 */
public class TestPlanModifCtrl
        extends TestCase
{

  private GemDesktop desktop;
  private DataCache dataCache;
  private DataConnection dc;
  private PlanModifCtrl plmCtrl;
  private Date toDay;
  private MemberService memberService;
  PersonSubscriptionCardIO io;
  DateFr before;

  public TestPlanModifCtrl(String testName) {
    super(testName);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    dc = TestProperties.getDataConnection();
    dataCache = TestProperties.getDataCache(dc);
    dataCache.load(null);
    desktop = new GemDesktopCtrl(new JFrame(), dataCache, new Properties());
    memberService = new MemberService(dc);
    plmCtrl = new PlanModifCtrl(desktop);
    toDay = new Date();
    Calendar cal = Calendar.getInstance();
    cal.setTime(toDay);
    cal.add(Calendar.MONTH, -5);
    before = new DateFr(cal.getTime());
    io = new PersonSubscriptionCardIO(dc);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void failingtestUpdateCarteAbo() throws Exception {
    int idper = 2;
    RehearsalPass card = new RehearsalPass(100, "Pass", 22.0f, 600);
    RehearsalPassIO.insert(card, dc);
    // ancienne carte
    PersonSubscriptionCard abo1 = new PersonSubscriptionCard();
    abo1.setPurchaseDate(before);
    abo1.setIdper(idper);
    abo1.setRest(0);
    abo1.setPassId(card.getId());
    io.insert(abo1);

    PersonSubscriptionCard abo2 = new PersonSubscriptionCard();
    abo2.setPurchaseDate(new DateFr(toDay));
    abo2.setIdper(idper);
    abo2.setRest(420);
    abo2.setPassId(card.getId());
    io.insert(abo2);

    Schedule p = new Schedule();
    p.setDate(toDay);
    p.setIdPerson(idper);
    p.setType(Schedule.MEMBER);
    p.setIdRoom(1);
    p.setStart(new Hour("10:00"));
    p.setEnd(new Hour("12:00"));// ajout 2h sur la carte
    ScheduleObject plan = new MemberRehearsalSchedule(p);
    memberService.cancelSubscriptionCardSession(dataCache, plan);
    PersonSubscriptionCard c = io.find(abo2.getId());
    assertNotNull("abo2.getId = " + abo2.getId(), c);

    io.deleteByIdper(idper);
    RehearsalPassIO.delete(card.getId(), dc);
  }

  public void testPostponeCourse() {
    HourRangePanel hp = new HourRangePanel(new Hour("14:00"), new Hour("15:00"));
    HourField hf = new HourField("17:00");

    Hour end = hf.get().end(hp.getLength());
    assertTrue(end.equals(new Hour("18:00")));
  }

  public void testMailtoURI() throws UnsupportedEncodingException {

    String email = "jmao@free.fr";
    String subject = urlEncode(MessageUtil.getMessage("booking.confirmation.subject"));
    String body = urlEncode(MessageUtil.getMessage("booking.confirmation.message", new Object[]{"Jean-Marc Gobat", "10-02-2016", "10:30"}));
    DesktopMailHandler mailHandler = new DesktopMailHandler();
    mailHandler.send(email, subject, body);
  }

  private String urlEncode(String str) {
    try {
      return URLEncoder.encode(str, "UTF-8").replace("+", "%20");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }
}
