/*
 * @(#)ServeurCtrl.java	2.9.4.6 03/06/15
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
package net.algem.util;

import java.awt.BorderLayout;
import net.algem.util.ui.GemPanel;
import net.algem.util.ui.GemTextArea;
import net.algem.util.ui.MessagePopup;
import net.algem.util.ui.SQLView;
import net.algem.util.ui.TabPanel;
import net.algem.util.ui.TableSGBDView;

//SELECT datname as base,usename as proprio from pg_database,pg_user where datdba = usesysid
//SELECT relname as relation from pg_class where (relkind = 'r' OR relkind = 'i') AND relname !~ '^pg' AND relname !~ '^Inv' ORDER BY relation
//SELECT a.attname,t.typname,a.attlen from pg_class c, pg_attribute a, pg_type t WHERE c.relname='"+relation.trim()+"' and a.attnum > 0 AND a.attrelid = c.oid AND a.atttypid = t.oid
/**
 * comment
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.9.4.6
 * @deprecated 
 */
public class ServeurCtrl
	extends GemPanel {

	private DataConnection dc;
	private ServerView vueServeur;
	private SQLView sql;
	private TableSGBDView table;
//	PgAdmin		admin;
	private GemPanel trace;
	private GemTextArea log;
	private TabPanel wCard;

	public ServeurCtrl(DataConnection dc) {
		this.dc = dc;

		vueServeur = new ServerView(dc);
		sql = new SQLView();
		table = new TableSGBDView(dc);

//		admin = new PgAdmin(dc);
//		wCard.addItem("admin",admin);

		trace = new GemPanel();
		trace.setLayout(new BorderLayout());
		log = new GemTextArea();
		log.setEditable(false);
		trace.add(log, BorderLayout.CENTER);

		wCard = new TabPanel();
		wCard.addItem(vueServeur, "cache");
		wCard.addItem(sql, "sql");
		wCard.addItem(table, "Tables SGBD");
		wCard.addItem(trace, "log");

		setLayout(new BorderLayout());
		add("Center", wCard);
	}
    
    /**
   * @deprecated
   */
  private void jmvLog() {
    Runtime r = Runtime.getRuntime();
    StringBuilder message = new StringBuilder("Mémoire utilisée avant réinitialisation : \n");
    message.append("Mémoire totale jvm : ").append(r.totalMemory() / 1024).append(" ko\n");
    message.append("Mémoire libre jvm : ").append(r.freeMemory() / 1024).append(" ko\n\n");
    System.out.println("Total Memory : " + r.totalMemory());
    System.out.println("Free  Memory : " + r.freeMemory());
    System.gc();
    System.runFinalization();
    System.out.println("gc+finalize");
    System.out.println("Total Memory : " + r.totalMemory());
    System.out.println("Free  Memory : " + r.freeMemory());
    message.append("Mémoire utilisée après réinitialisation :\n");
    message.append("Mémoire totale jvm : ").append(r.totalMemory() / 1024).append(" ko\n");
    message.append("Mémoire libre jvm : ").append(r.freeMemory() / 1024).append(" ko");
    System.out.println();
    MessagePopup.information(null, message.toString());
  }
}
