/*
 * @(#)GemDecimalField.java 2.5.a 07/07/12
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
package net.algem.util.ui;

import java.text.Format;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import net.algem.accounting.AccountUtil;

/**
 * Gem decimal field.
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.5.a
 * @since 2.5.a 07/07/12
 */
public class GemDecimalField
        extends JFormattedTextField {

  private static Format f = AccountUtil.getDefaultNumberFormat();

  public GemDecimalField() {
    this(f);
  }

  public GemDecimalField(Format format) {
    super(format);
    setInputVerifier(new InputVerifier() {
      @Override
      public boolean verify(JComponent input) {
        if (GemDecimalField.this.getText() == null || GemDecimalField.this.getText().isEmpty()) {
          GemDecimalField.this.setValue(0.0);
        }
        return true;
      }
    });
  }

  @Override
  public Object getValue() {
    Object d = super.getValue();
    if (d instanceof Number) {
      return ((Number) d).doubleValue();
    }
    return d;
  }
  
  @Override
  public void setValue(Object d) {
    super.setValue(d);       
    if (d instanceof Double) {
      if ((Double) d == 0.0) {
        setText(null);
      } 
    }
  }
  
}
