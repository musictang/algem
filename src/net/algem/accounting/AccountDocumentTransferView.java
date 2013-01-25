/*
 * @(#)AccountDocumentTransferView.java	2.7.a 05/12/12
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
package net.algem.accounting;

import javax.swing.JLabel;
import net.algem.util.BundleUtil;
import net.algem.util.DataConnection;
import net.algem.util.ui.GemField;
import net.algem.util.ui.GridBagHelper;

/**
 * View transfer from document number.
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.7.a
 */
public class AccountDocumentTransferView
        extends AccountTransferView
{

  private GemField document;

  public AccountDocumentTransferView(DataConnection dc) {
    super(dc);
    document = new GemField(8);
    gb.add(new JLabel(BundleUtil.getLabel("Document.number.label")), 0, 4, 1, 1, GridBagHelper.EAST);
    gb.add(document, 1, 4, 2, 1, GridBagHelper.WEST);
  }

  public String getDocument() {
    return document.getText();
  }
}
