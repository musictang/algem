/*
 * @(#)GemTreeNode.java	3.0.0 13/09/2021
 * 
 * Copyright (c) 2021 eric@productionlibre.fr. All Rights Reserved.
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

/**
 * Generic Tree node.
 *
 * @author <a href="mailto:eric@productionlibre.fr">Eric</a>
 * @version 3.0.0
 * @since 3.0.0
 */
public class GemTreeNode
	extends javax.swing.tree.DefaultMutableTreeNode {

	public GemTreeNode() {
	}

	public GemTreeNode(String texte) {
		super(texte);
	}


}
