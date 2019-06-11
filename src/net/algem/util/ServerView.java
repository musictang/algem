/*
 * @(#)ServerView.java	2.6.a 06/08/12
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
package net.algem.util;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import net.algem.util.ui.*;

/**
 * comment
 * 
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.6.a
 */
public class ServerView
	extends GemPanel
	implements ActionListener {

	private DataConnection dc;
	private GemField pghost;
	private GemNumericField pgport;
	private GemField pgbase;
	private GemField pguser;
	private GemField pgetat;
	private GemButton connexion;
	private GemButton deconnexion;

	public ServerView(DataConnection dc) {
		this.dc = dc;

		this.setLayout(new GridBagLayout());

		pghost = new GemField(this.dc.getDbhost(), 24);
		pgport = new GemNumericField(String.valueOf(this.dc.getDbport()), 6);
		pgbase = new GemField(this.dc.getDbname(), 16);
		pguser = new GemField(DataConnection.DEFAULT_DB_USER, 8);
		pgetat = new GemField(16);
		pgetat.setText(this.dc.isConnected() ? "connect?" : "non connect?");

		connexion = new GemButton("Connexion");
		deconnexion = new GemButton("Déconnexion");
		connexion.addActionListener(this);
		deconnexion.addActionListener(this);

		this.setLayout(new GridBagLayout());
		GridBagHelper gb = new GridBagHelper(this);
		gb.add(new GemLabel("host"), 0, 0, 1, 1, GridBagHelper.EAST);
		gb.add(new GemLabel("port"), 0, 1, 1, 1, GridBagHelper.EAST);
		gb.add(new GemLabel("base"), 0, 2, 1, 1, GridBagHelper.EAST);
		gb.add(new GemLabel("user"), 0, 3, 1, 1, GridBagHelper.EAST);
		gb.add(new GemLabel("etat"), 0, 4, 1, 1, GridBagHelper.EAST);
		gb.add(pghost, 1, 0, 1, 1, GridBagHelper.WEST);
		gb.add(pgport, 1, 1, 1, 1, GridBagHelper.WEST);
		gb.add(pgbase, 1, 2, 1, 1, GridBagHelper.WEST);
		gb.add(pguser, 1, 3, 1, 1, GridBagHelper.WEST);
		gb.add(pgetat, 1, 4, 1, 1, GridBagHelper.WEST);

		gb.add(connexion, 0, 5, 1, 1, gb.HORIZONTAL, 1.0, 0.0);
		gb.add(deconnexion, 1, 5, 1, 1, gb.HORIZONTAL, 1.0, 0.0);
	}

	void clear() {
	}

	public void deconnexion() {
		dc.close();
		pgetat.setText("non connect?");
	}

	public boolean connexion() {
		String host = pghost.getText();
		if (host.length() == 0) {
			host = dc.getDbhost();
		}
		int port;
		String sport = pgport.getText();
		if (sport.length() != 0) {
			port = dc.getDbport();
		} else {
			port = Integer.parseInt(sport);
		}
		String base = pgbase.getText();
		if (base.length() == 0) {
			base = dc.getDbname();
		}
		String user = pguser.getText();
		if (user.length() == 0) {
			user = DataConnection.DEFAULT_DB_USER;
		}

		try {
			dc.connect(host, port, base);
			pgetat.setText("connect?");
		} catch (Exception e) {
			pgetat.setText("erreur:" + e);
		}

		return true;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Connexion")) {
			connexion();
		} else if (e.getActionCommand().equals("D?connexion")) {
			deconnexion();
		}
	}
}
