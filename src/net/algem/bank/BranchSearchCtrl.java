/*
 * @(#)BranchSearchCtrl.java	2.9.4.13 15/10/15
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
package net.algem.bank;

import java.awt.CardLayout;
import java.util.List;
import net.algem.contact.Person;
import net.algem.util.DataConnection;
import net.algem.util.ui.SearchCtrl;

/**
 * Search controller for bank branch.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.13
 * @since 1.0a 07/07/1999
 */
public class BranchSearchCtrl
        extends SearchCtrl
{

	private BankBranchIO bankBranchIO;
	
  public BranchSearchCtrl(DataConnection dc) {
    super(dc, "");
    bankBranchIO = new BankBranchIO(dc);
  }

  @Override
  public void init() {
    searchView = new BranchSearchView();
    searchView.addActionListener(this);

    list = new BranchListCtrl();
    list.addMouseListener(this);
    list.addActionListener(this);

    mask = new BankBranchCtrl(dc, bankBranchIO);
    mask.addActionListener(this);

    wCard.add("cherche", searchView);
    wCard.add("masque", mask.getContentPane());
    wCard.add("liste", list);

    ((CardLayout) wCard.getLayout()).show(wCard, "cherche");
  }

  @Override
  public void search() {
    String query;
    String bankCode;
    String bankName;
    String branchCode;
    int id = getId();
    if (id > 0) {
      query = "WHERE p.id=" + id;
    } else if ((bankCode = searchView.getField(1)) != null) {
      query = "WHERE b.code = '" + bankCode + "'";
    } else if ((bankName = searchView.getField(2)) != null) {
      query = "WHERE b.nom ~ '" + bankName + "'";
    } else if ((branchCode = searchView.getField(3)) != null) {
      query = "WHERE g.code = '" + branchCode + "'";
    } else {
      query = "";
    }

    query += query.length() > 0 ? " AND " : " WHERE ";
    query += "ptype=" + Person.BANK;

    List<BankBranch> v = bankBranchIO.find(query, true);
    if (v.isEmpty()) {
      setStatus(EMPTY_LIST);
    } else if (v.size() == 1) {
      ((CardLayout) wCard.getLayout()).show(wCard, "masque");
      mask.loadCard(v.get(0));
    } else {
      ((CardLayout) wCard.getLayout()).show(wCard, "liste");
      list.loadResult(v);
    }
  }
}

