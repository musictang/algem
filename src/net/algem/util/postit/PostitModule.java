/*
 * @(#)PostitModule.java	2.8.w 27/08/14
 * 
 * Copyright (c) 1999-2014 Musiques Tangentes. All Rights Reserved.
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
package net.algem.util.postit;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.JInternalFrame;
import net.algem.planning.DateFr;
import net.algem.security.UserService;
import net.algem.util.GemLogger;
import net.algem.util.module.DefaultGemView;
import net.algem.util.module.GemModule;

/**
 * Internal frame used to display postits.
 *
 * @author <a href="mailto:eric@musiques-tangentes.asso.fr">Eric</a>
 * @author <a href="mailto:jmg@musiques-tangentes.asso.fr">Jean-Marc Gobat</a>
 * @version 2.8.w
 */
public class PostitModule
	extends GemModule
	implements VetoableChangeListener {

	private int lus;
	private CanvasPostit postit;
	private UserService service;

	public PostitModule(UserService service) {
		super("Postit");
		this.service = service;
	}

	@Override
	public void init() {
		view = new DefaultGemView(desktop, "Postit");

		postit = new CanvasPostit();
		postit.addActionListener(this);
		lus = 0;
		view.getContentPane().add("Center", postit);
		view.setMaximizable(false);
		view.setClosable(false);
		view.setIconifiable(false);
		view.addVetoableChangeListener(this);
		view.setSize(GemModule.POSTIT_SIZE);
	}

	@Override
	public String getSID() {
		return String.valueOf(lus);
	}

	/**
	 * Adds a postit.
	 * A receiver == 0 implies a public status.
   * Private postits are seen by current user only.
	 */
	public void getNewPostit() {

		int userId = dataCache.getUser().getId();
		
		Vector<Postit> v = service.getPostits(userId, lus);
		Enumeration<Postit> enu = v.elements();
		while (enu.hasMoreElements()) {
			Postit p = enu.nextElement();

			DateFr toDay = new DateFr(new java.util.Date());
			if (toDay.after(p.getTerm())) {
				try {
					service.delete(p);
				} catch (SQLException ex) {
					GemLogger.logException("Erreur suppression postit", ex);
				}
				continue;
			}
			lus = p.getId();
			// ajout 1.1d : gestion des postits privés
			if (p.getReceiver() != 0) { // si privé
				if (userId == p.getReceiver()) {
					postit.add(p);
				}
			} else {
				postit.add(p); // si public
			}
		}
	}

	@Override
	public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
		String name = evt.getPropertyName();
		if (name.equals(JInternalFrame.IS_CLOSED_PROPERTY)
			|| name.equals(JInternalFrame.IS_ICON_PROPERTY)) {
			throw new PropertyVetoException("pas d'accord", evt);
		}
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() instanceof PostitPosition) {
			PostitDlg dlg = new PostitDlg(desktop.getFrame());
			PostitPosition pp = (PostitPosition) evt.getSource();
			dlg.setPost(pp.getPostit());
			dlg.entry();
			if (dlg.isSuppression()) {
				try {
					service.delete(pp.getPostit());
					postit.remove(pp);
				} catch (SQLException e) {
					GemLogger.logException("suppression postit", e, desktop.getFrame());
				}
			} else if (dlg.isModif()) {
				try {
					Postit p = pp.getPostit();
					p.setText(dlg.getText());
					service.update(p);
				} catch (SQLException ex) {
					GemLogger.logException("update postit", ex);
				}
			}
			postit.repaint();
		}

	}

	public void addPostit(Postit p) {
		postit.add(p);
		lus = p.getId();
		System.out.println("PostitModule lu=" + lus);
	}
    
}
