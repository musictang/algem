/*
 * @(#)GemAmount.java	2.9.4.13 05/11/15
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
package net.algem.accounting;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Formatted floating amount with 2 decimal places.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 */
public class GemAmount
        implements java.io.Serializable, Comparable
{

  private static final long serialVersionUID = 3382592365677086510L;
  
  private double amount;
  private NumberFormat nf = NumberFormat.getInstance(Locale.FRANCE);

  public GemAmount() {
    nf.setMaximumFractionDigits(2);
    nf.setMinimumFractionDigits(2);
  }

  public GemAmount(double m) {
    this();
    amount = m;
  }

  public GemAmount(float m) {
    this((double) m);
  }

  public GemAmount(int m) {
    this((double) m / 100);
  }

  @Override
  public String toString() {
    return nf.format(amount + 0.0001);
  }

  public double doubleValue() {
    return amount;
  }

  public float floatValue() {
    return (float) (amount);
  }

  public String toCadreString(int lg) {
    StringBuilder buf = new StringBuilder();
    String s = nf.format(amount + 0.0001);
    for (int i = s.length(); i < lg; i++) {
      buf.append(" ");
    }
    buf.append(s);
    return buf.toString();
  }

  @Override
  public int compareTo(Object o) {
    return (int) (doubleValue() - ((GemAmount) o).doubleValue());
  }
  
}