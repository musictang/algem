/*
 * @(#)IbanField.java	2.8.p 17/10/13
 * 
 * Copyright (c) 1999-2013 Musiques Tangentes. All Rights Reserved.
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

package net.algem.bank;

import javax.swing.JFormattedTextField;
import javax.swing.text.MaskFormatter;
import net.algem.util.MessageUtil;

/**
 * IBAN field with mask formatter.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.p
 * @since 2.8.i 04/07/13
 */
public class IbanField 
  extends JFormattedTextField
{
  
  static final String FRENCH_MASK=  "UUAA AAAA AAAA AAAA AAAA AAAA AAA";
  
  static final MaskFormatter mf;
  
  static {
    mf = MessageUtil.createFormatter(FRENCH_MASK);
    mf.setValueContainsLiteralCharacters(false);
  }
  /** Max IBAN code length. */
  private static final int MAX_IBAN_LENGTH = 34;
  
  private static final String INTERNATIONAL_MASK=  "UUAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AA";
  
  /**
   * Constructs a field with french format.
   */
  public IbanField() {
    super(mf);
    setColumns(23);
  }
  
}
