/*
 * @(#)TestGroupRehearsalCreateCtrl.java 2.6.a 08/10/12
 * 
 * Copyright (c) 1999-2012 Musiques Tangentes. All Rights Reserved.
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
package net.algem.group;

import java.util.Properties;
import javax.swing.JFrame;
import junit.framework.TestCase;
import net.algem.TestProperties;
import net.algem.planning.Hour;
import net.algem.planning.RehearsalUtil;
import net.algem.room.Room;
import net.algem.room.RoomRate;
import net.algem.room.RoomRateEnum;
import net.algem.util.DataCache;
import net.algem.util.DataConnection;
import net.algem.util.module.GemDesktop;
import net.algem.util.module.AbstractDesktopCtrl;
import net.algem.util.module.GemDesktopCtrl;

/**
 *
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class TestGroupRehearsalCreateCtrl
        extends TestCase
{

  GemDesktop desktop;
  private DataConnection dc;
  private DataCache dataCache;
  private Hour fhc; // fin Hour creuse

  public TestGroupRehearsalCreateCtrl(String testName) {
    super(testName);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    dc = TestProperties.getDataConnection();
    dataCache = TestProperties.getDataCache(dc);
    dataCache.load(null);
    desktop = new GemDesktopCtrl(new JFrame(), dataCache, new Properties());
    fhc = RehearsalUtil.getEndOffpeakHour(dc);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testCalculerMontantRepetPonctuelle() {
    GroupRehearsalCreateCtrl gc = new GroupRehearsalCreateCtrl(desktop);
    int nh = 3;// nombre d'Hours repet
    int nm = 4;
    RoomRate t = new RoomRate();
    t.setOffPeakRate(7.0);
    t.setFullRate(7.0);
    t.setMax(11.5);
    t.setType(null);

    Room s = new Room();
    s.setRate(t);

    Hour deb = new Hour(fhc);
    deb.decHour(nh);
    Hour fin = new Hour(fhc);

    double res = RehearsalUtil.calcSingleRehearsalAmount(deb, fin, s.getRate(), nm, dc);
    assertTrue("Le montant non nul quand tarif type non défini ?", res == 0);

  }

  public void testCalcHourRate() {
    //GroupeHourRepetCreateCtrl gc = new GroupeHourRepetCreateCtrl(desktop);
    double hc = 11.0;
    double hp = 14.0;
    double p = 14.0;//plafond
    RoomRate t = setHourRate(hc, hp, p);
    int nh = 3;//nombre d'Hours (ne pas descendre en dessous de 2)

    // Réservation en période creuse
    Hour deb = new Hour(fhc);
    deb.decHour(nh);
    Hour fin = new Hour(fhc);

    double expected = hc * nh;
    double res = RehearsalUtil.calcHourRate(deb, fin, t, dc);
    assertTrue("Erreur total Hours creuses", expected == res);

    // Réservation en période pleine
    deb = new Hour(fhc);
    fin = new Hour(fhc);
    fin.incHour(nh);
    expected = hp * nh;
    res = RehearsalUtil.calcHourRate(deb, fin, t, dc);
    assertTrue("Erreur total Hours pleines", expected == res);

    // Réservation en période mixte
    int dec = nh - 1;
    int inc = nh - (nh - 1);
    deb = new Hour(fhc);
    deb.decHour(dec);
    fin = new Hour(fhc);
    fin.incHour(inc);
    expected = (hc * dec) + (hp * inc);
    res = RehearsalUtil.calcHourRate(deb, fin, t, dc);
    assertTrue("Erreur total Hours mixtes", expected == res);
  }

  public void testCalcPersonRate() {
    //GroupeHourRepetCreateCtrl gc = new GroupeHourRepetCreateCtrl(desktop);
    double hc = 7.0;
    double hp = 7.0;
    double p = 11.5;//plafond
    int nh = 3;//nombre d'Hours (ne pas descendre en dessous de 2)
    RoomRate t = setHourRate(hc, hp, p);
    int nm = 3; // nombre de musiciens

    // Réservation en période creuse
    Hour deb = new Hour(fhc);
    deb.decHour(nh);
    Hour fin = new Hour(fhc);
    double excepted = hc * nm;
    double res = RehearsalUtil.calcPersonRate(deb, fin, t, nm);
    assertTrue("Erreur total Hours creuses", excepted == res);

    // Réservation en période mixte
    deb = new Hour(fhc);
    deb.decHour(nh - 1);
    fin = new Hour(fhc);
    fin.incHour(nh - (nh - 1));

    res = RehearsalUtil.calcPersonRate(deb, fin, t, nm);
    assertTrue("Erreur total Hours pleines", excepted == res);

    // Réservation pour plus de 4 musiciens
    nm = 5;
    deb = new Hour(fhc);
    fin = new Hour(fhc);
    fin.incHour(3);
    excepted = p * 3;
    res = RehearsalUtil.calcPersonRate(deb, fin, t, nm);
    assertTrue("Le plafond devrait rentrer en jeu", excepted == res);

  }

  private RoomRate setHourRate(double hc, double hp, double plafond) {
    RoomRate t = new RoomRate();
    t.setType(RoomRateEnum.HORAIRE);
    t.setOffPeakRate(hc);
    t.setFullRate(hp);
    t.setMax(plafond);
    return t;
  }
}
